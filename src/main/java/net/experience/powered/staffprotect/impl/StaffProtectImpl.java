package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.addons.AddonManager;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.experience.powered.staffprotect.notification.Sender;
import net.experience.powered.staffprotect.util.CommandRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class StaffProtectImpl implements StaffProtect {

    private final Sender sender;
    private final CommandRegisterer commandRegisterer;
    private final AddonManager addonManager;
    private final JavaPlugin plugin;
    private final NotificationBus bus;

    public StaffProtectImpl(final @NotNull JavaPlugin plugin, final @NotNull NotificationBus bus) {
        this.plugin = plugin;
        this.bus = bus;
        this.addonManager = new AddonManagerImpl(this);
        this.commandRegisterer = new CommandRegistererImpl(this, Bukkit.getName());
        this.sender = new SenderImpl(this);
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

    @Override
    public @NotNull Sender getDefaultSender() {
        return sender;
    }
}
