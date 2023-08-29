package net.experience.powered.staffprotect;

import net.experience.powered.staffprotect.commands.StaffProtectCommand;
import net.experience.powered.staffprotect.impl.AddonManagerImpl;
import net.experience.powered.staffprotect.impl.StaffProtectImpl;
import net.experience.powered.staffprotect.listeners.InventoryListener;
import net.experience.powered.staffprotect.listeners.PlayerListener;
import net.experience.powered.staffprotect.notification.NotificationBus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class StaffProtectPlugin extends JavaPlugin {

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
        Bukkit.getServicesManager().register(StaffProtect.class, api, this, ServicePriority.Normal);
        ((AddonManagerImpl) api.getAddonManager()).enableAddons();

        final var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(api), this);
        pluginManager.registerEvents(new PlayerListener(api), this);

        api.getCommandManager().register(new StaffProtectCommand(api));

        metrics = new Metrics(this, 19629);
        metrics.addCustomChart(new Metrics.SingleLineChart("amount_of_addons", () -> api.getAddonManager().getAddons().size()));
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
        if (api == null) return;
        ((AddonManagerImpl) api.getAddonManager()).disableAddons();
    }

    @NotNull
    private StaffProtect getStaffProtectAPI() {
        final NotificationBus bus = new NotificationBus() {

            private final List<UUID> subscribers = new ArrayList<>();

            @Override
            public void subscribe(final @NotNull UUID uuid) {
                subscribers.add(uuid);
            }

            @Override
            public void unsubscribe(final @NotNull UUID uuid) {
                subscribers.remove(uuid);
            }

            @Override
            public @NotNull @UnmodifiableView List<UUID> getSubscribers() {
                return Collections.unmodifiableList(subscribers);
            }
        };
        final StaffProtect staffProtect = new StaffProtectImpl(this, bus);
        new StaffProtectProvider(staffProtect);
        return staffProtect;
    }
}
