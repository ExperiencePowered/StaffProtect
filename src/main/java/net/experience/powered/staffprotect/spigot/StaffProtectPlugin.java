package net.experience.powered.staffprotect.spigot;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.StaffProtectProvider;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.experience.powered.staffprotect.notification.Subscriber;
import net.experience.powered.staffprotect.records.ActionType;
import net.experience.powered.staffprotect.records.Record;
import net.experience.powered.staffprotect.records.RecordFile;
import net.experience.powered.staffprotect.spigot.commands.StaffProtectCommand;
import net.experience.powered.staffprotect.database.AbstractDatabase;
import net.experience.powered.staffprotect.database.DatabaseProperties;
import net.experience.powered.staffprotect.spigot.database.MySQL;
import net.experience.powered.staffprotect.spigot.database.SQLite;
import net.experience.powered.staffprotect.spigot.impl.*;
import net.experience.powered.staffprotect.spigot.listeners.InventoryListener;
import net.experience.powered.staffprotect.spigot.listeners.PlayerListener;
import net.experience.powered.staffprotect.spigot.messages.PluginMessageManager;
import net.experience.powered.staffprotect.spigot.utils.Metrics;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public final class StaffProtectPlugin extends JavaPlugin {

    public static ZoneId zoneId;

    private static StaffProtectPlugin instance;
    private static boolean bungee;
    private PluginMessageManager messageManager;
    private AbstractDatabase database;
    private Metrics metrics;
    private VersionController versionController;
    private StaffProtect api;

    public StaffProtectPlugin() {
        final String string = getConfig().getString("zoneId", "");
        zoneId = string.equalsIgnoreCase("") ? ZoneId.systemDefault() : ZoneId.of(string);
    }

    @Override
    public void onEnable() {
        StaffProtectPlugin.instance = this;
        this.versionController = new VersionController(getDataFolder());

        String info = " (Git: " +
                versionController.getGitHash() +
                ", branch " +
                versionController.getGitBranchName() +
                ")";
        getLogger().info(info);

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        saveConfigDefaults();

        api = getStaffProtectAPI();
        getNotificationManager();
        Bukkit.getServicesManager().register(StaffProtect.class, api, this, ServicePriority.Normal);
        ((AddonManagerImpl) api.getAddonManager()).enableAddons();

        final DatabaseProperties databaseProperties = new DatabaseProperties("public");
        final String databaseType = getConfig().getString("database.type", "SQLite");
        if (databaseType.equalsIgnoreCase("SQLite")) {
            final File databaseFile = new File(getDataFolder(), "Database.db");
            if (!databaseFile.exists()) {
                try {
                    databaseFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.database = new SQLite(databaseProperties, databaseFile);
        }
        else if (databaseType.equalsIgnoreCase("MySQL")) {
            final String host = getConfig().getString("database.mysql.host", "127.0.0.1");
            final int port = getConfig().getInt("database.mysql.port", 3306);
            final String database = getConfig().getString("database.mysql.database", "db");
            final String username = getConfig().getString("database.mysql.username", "root");
            final String password = getConfig().getString("database.mysql.password", "");
            final boolean useSSL = getConfig().getBoolean("database.mysql.useSSL", true);
            databaseProperties.write("host", host);
            databaseProperties.write("port", port);
            databaseProperties.write("database", database);
            databaseProperties.write("password", password);
            databaseProperties.write("username", username);
            databaseProperties.write("useSSL", useSSL);
            this.database = new MySQL(databaseProperties);
        }
        else {
            throw new IllegalStateException("This database type : " + databaseType + " does not exist.");
        }
        database.connect();
        database.createDefaultTable();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(api), this);
        pluginManager.registerEvents(new PlayerListener(api), this);

        api.getCommandManager().register(new StaffProtectCommand(api));

        new RecordFileImpl();
        NotificationManager.getInstance().sendQuietMessage("Server", "StaffProtect was enabled.", ActionType.SERVER_STATE);

        metrics = new Metrics(this, 19629);
        metrics.addCustomChart(new Metrics.SingleLineChart("amount_of_addons", () -> api.getAddonManager().getAddons().size()));

        new VerificationImpl();

        if (getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean( "bungeecord")) {
            this.messageManager = new PluginMessageManager();

            final Messenger messenger = getServer().getMessenger();
            messenger.registerIncomingPluginChannel(this, "staffprotect:spigot", messageManager);
            messenger.registerOutgoingPluginChannel(this, "staffprotect:bungee");

            StaffProtectPlugin.bungee = true;
        }
        else {
            StaffProtectPlugin.bungee = false;
        }
    }

    @Override
    public void onDisable() {
        final Messenger messenger = getServer().getMessenger();
        messenger.unregisterIncomingPluginChannel(this, "staffprotect:bungee");
        messenger.unregisterOutgoingPluginChannel(this, "staffprotect:spigot");

        final RecordFile recordFile = RecordFile.getInstance();
        if (recordFile != null) {
            NotificationManager notificationManager = NotificationManager.getInstance();
            notificationManager.sendQuietMessage("Server", "StaffProtect was disabled.", ActionType.SERVER_STATE);
            notificationManager.sendQuietMessage("StaffProtect", "Saved file.", ActionType.PLUGIN);
        }
        if (database != null) {
            database.disconnect();
        }
        if (metrics != null) {
            metrics.shutdown();
        }
        if (api != null) {
            ((AddonManagerImpl) api.getAddonManager()).disableAddons();
        }
    }

    @NotNull
    private StaffProtect getStaffProtectAPI() {
        final NotificationBus bus = new NotificationBus() {

            private final List<Subscriber> subscribers = new ArrayList<>();

            @Override
            public void subscribe(final @NotNull UUID uuid) {
                subscribers.add(new SubscriberImpl(uuid));
            }

            @Override
            public void unsubscribe(final @NotNull UUID uuid) {
                Subscriber subscriber = null;
                for (Subscriber sub : subscribers) {
                    if (sub.getUniqueId().equals(uuid)) {
                        subscriber = sub;
                    }
                }
                if (subscriber != null) {
                    subscribers.remove(new SubscriberImpl(uuid));
                }
            }

            @Override
            public @NotNull @UnmodifiableView List<UUID> getSubscribers() {
                final List<UUID> legacy = new ArrayList<>();
                subscribers.forEach(subscriber -> legacy.add(subscriber.getUniqueId()));
                return Collections.unmodifiableList(legacy);
            }

            @Override
            public void subscribe(@NotNull Subscriber subscriber) {
                subscribers.add(subscriber);
            }

            @Override
            public void unsubscribe(@NotNull Subscriber subscriber) {
                subscribers.remove(subscriber);
            }

            @Override
            public @NotNull List<Subscriber> getModernSubscribers() {
                return subscribers;
            }
        };
        final StaffProtect staffProtect = new StaffProtectImpl(this, bus);
        new StaffProtectProvider(staffProtect);
        return staffProtect;
    }

    private void getNotificationManager() {
        new NotificationManager(this, api.getNotificationBus()) {
            @Override
            public void sendMessage(final @Nullable String player, final @NotNull UUID uuid, final @NotNull Component component, final @NotNull ActionType actionType) {
                final BukkitAudiences audience = BukkitAudiences.create(api.getPlugin());
                bus.getModernSubscribers().forEach(sub -> {
                    if (!sub.getIgnoredPlayers().contains(uuid)) {
                        audience.player(sub.getUniqueId()).sendMessage(component);
                    }
                });
                sendQuietMessage(player, PlainTextComponentSerializer.plainText().serialize(component), actionType);
            }

            @Override
            public void sendQuietMessage(final @Nullable String player, final @NotNull String string, final @NotNull ActionType actionType) {
                RecordFile.getInstance().writeRecord(new Record(Instant.now(Clock.system(zoneId)).toEpochMilli(), player == null ? "Anonymous" : player, actionType, string));
            }
        };
    }

    public void saveConfigDefaults() {
        try (InputStream inputStream = getResource("config.yml")) {
            if (inputStream == null) {
                return;
            }
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            configuration.getKeys(true).stream()
                    .filter(string -> !configuration.isConfigurationSection(string))
                    .filter(string -> getConfig().getString(string) == null)
                    .forEach(string -> getConfig().set(string, configuration.get(string)));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBungee() {
        return bungee;
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull Optional<PluginMessageManager> getMessageManager() {
        return Optional.of(messageManager);
    }

    public AbstractDatabase getDatabase() {
        return database;
    }

    public static StaffProtectPlugin getInstance() {
        return instance;
    }
}
