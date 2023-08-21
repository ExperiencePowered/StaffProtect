package net.experience.powered.staffprotect;

import net.experience.powered.staffprotect.hooks.LuckPermsHook;
import net.experience.powered.staffprotect.impl.AddonManagerImpl;
import net.experience.powered.staffprotect.impl.StaffProtectAPIImpl;
import net.experience.powered.staffprotect.interfaces.Permission;
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

public final class StaffProtect extends JavaPlugin {

    private VersionController versionController;
    private StaffProtectAPI api;
    private Permission permission = new Permission() {};

    @Override
    public void onEnable() {
        this.versionController = new VersionController(getDataFolder());

        String info = "Enabling StaffProtect v" +
                versionController.getVersion() +
                " (Git: " +
                versionController.getGitHash() +
                ", branch " +
                versionController.getGitBranchName() +
                ")";
        getLogger().info(info);

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            this.permission = new LuckPermsHook();
        }

        api = getStaffProtectAPI();
        Bukkit.getServicesManager().register(StaffProtectAPI.class, api, this, ServicePriority.Normal);
        ((AddonManagerImpl) api.getAddonManager()).enableAddons();

        final var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(api), this);
        pluginManager.registerEvents(new PlayerListener(api), this);
    }

    @Override
    public void onDisable() {
        if (api == null) return;
        ((AddonManagerImpl) api.getAddonManager()).disableAddons();
    }

    @NotNull
    private StaffProtectAPIImpl getStaffProtectAPI() {
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
        return new StaffProtectAPIImpl(this, permission, bus);
    }
}
