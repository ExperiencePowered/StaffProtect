package net.experience.powered.staffprotect.spigot;

import com.zaxxer.hikari.HikariConfig;
import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.StaffProtectProvider;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.experience.powered.staffprotect.notification.Subscriber;
import net.experience.powered.staffprotect.records.Record;
import net.experience.powered.staffprotect.records.RecordFile;
import net.experience.powered.staffprotect.spigot.commands.StaffProtectCommand;
import net.experience.powered.staffprotect.spigot.database.AbstractDatabase;
import net.experience.powered.staffprotect.spigot.database.SQLite;
import net.experience.powered.staffprotect.spigot.impl.AddonManagerImpl;
import net.experience.powered.staffprotect.spigot.impl.StaffProtectImpl;
import net.experience.powered.staffprotect.spigot.impl.SubscriberImpl;
import net.experience.powered.staffprotect.spigot.impl.VerificationImpl;
import net.experience.powered.staffprotect.spigot.listeners.InventoryListener;
import net.experience.powered.staffprotect.spigot.listeners.PlayerListener;
import net.experience.powered.staffprotect.spigot.messages.PluginMessageManager;
import net.experience.powered.staffprotect.spigot.utils.Metrics;
import net.experience.powered.staffprotect.util.Counter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public final class StaffProtectPlugin extends JavaPlugin {

    private static boolean bungee;
    private PluginMessageManager messageManager;
    private AbstractDatabase database;
    private Metrics metrics;
    private VersionController versionController;
    private StaffProtect api;

    @Override
    public void onEnable() {
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

        api = getStaffProtectAPI();
        getNotificationManager();
        Bukkit.getServicesManager().register(StaffProtect.class, api, this, ServicePriority.Normal);
        ((AddonManagerImpl) api.getAddonManager()).enableAddons();

        final HikariConfig config = new HikariConfig();
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
            this.database = new SQLite(config, databaseFile);
        }
        else if (databaseType.equalsIgnoreCase("MySQL")) {

        }
        else {
            throw new IllegalStateException("This database type : " + databaseType + " does not exist.");
        }
        database.connect();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(api), this);
        pluginManager.registerEvents(new PlayerListener(api), this);

        api.getCommandManager().register(new StaffProtectCommand(api));

        File file;
        {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
            final String string = getConfig().getString("zoneId", "");
            final ZoneId zoneId = string.equalsIgnoreCase("") ? ZoneId.systemDefault() : ZoneId.of(string);
            final Instant instant = Instant.now(Clock.system(zoneId));
            final String date = dateFormat.format(Date.from(instant));
            final Counter counter = new Counter();
            file = new File(RecordFile.folder, date + ".txt");
            try {
                while (!file.createNewFile()) {
                    file = new File(RecordFile.folder, date + "-" + counter + ".txt");
                    counter.increment();
                }
            } catch (IOException e) {
                getLogger().severe("Could not create record file : " + file);
                e.printStackTrace();
            }
        }
        final RecordFile recordFile = new RecordFile(file);
        recordFile.writeRecord(new Record(System.currentTimeMillis(), "Server", "StaffProtect was enabled."));

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
            recordFile.writeRecord(new Record(System.currentTimeMillis(), "Server", "StaffProtect was disabled."));
            recordFile.writeRecord(new Record(System.currentTimeMillis(), "StaffProtect", "Saved file."));
        }

        metrics.shutdown();
        if (api == null) return;
        ((AddonManagerImpl) api.getAddonManager()).disableAddons();
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
            public void sendMessage(final @Nullable String player, final @NotNull Component component) {
                final BukkitAudiences audience = BukkitAudiences.create(api.getPlugin());
                bus.getSubscribers().forEach(uuid -> audience.player(uuid).sendMessage(component));
                sendQuietMessage(player, PlainTextComponentSerializer.plainText().serialize(component));
            }

            @Override
            public void sendQuietMessage(final @Nullable String player, final @NotNull String string) {
                RecordFile.getInstance().writeRecord(new Record(System.currentTimeMillis(), player == null ? "Anonymous" : player, string));
            }
        };
    }

    public static boolean isBungee() {
        return bungee;
    }

    public PluginMessageManager getMessageManager() {
        return messageManager;
    }

    public AbstractDatabase getDatabase() {
        return database;
    }
}
