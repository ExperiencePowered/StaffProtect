package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.addons.AddonManager;
import net.experience.powered.staffprotect.interfaces.Permission;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.experience.powered.staffprotect.util.CommandRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class StaffProtectAPIImpl implements StaffProtectAPI {

    private final CommandRegisterer commandRegisterer;
    private final AddonManager addonManager;
    private final JavaPlugin plugin;
    private final Permission permission;
    private final NotificationBus bus;

    public StaffProtectAPIImpl(
            final @NotNull JavaPlugin plugin,
            final @NotNull Permission permission,
            final @NotNull NotificationBus bus) {
        this.plugin = plugin;
        this.permission = permission;
        this.bus = bus;
        this.addonManager = new AddonManagerImpl(this);
        this.commandRegisterer = new CommandRegistererImpl(Bukkit.getName());
    }


    @Override
    public @NotNull Permission getPermission() {
        return permission;
    }

    @Override
    public @NotNull NotificationBus getNotificationBus() {
        return bus;
    }

    @Override
    public @NotNull JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull AddonManager getAddonManager() {
        return addonManager;
    }

    @Override
    public @NotNull CommandRegisterer getCommandManager() {
        return commandRegisterer;
    }
}
