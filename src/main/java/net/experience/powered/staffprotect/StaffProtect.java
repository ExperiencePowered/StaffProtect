package net.experience.powered.staffprotect;

import net.experience.powered.staffprotect.hooks.LuckPermsHook;
import net.experience.powered.staffprotect.impl.StaffProtectAPIImpl;
import net.experience.powered.staffprotect.listeners.InventoryListener;
import net.experience.powered.staffprotect.listeners.PlayerConnectionListener;
import net.experience.powered.staffprotect.interfaces.Permission;
import net.experience.powered.staffprotect.notification.NotificationBus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class StaffProtect extends JavaPlugin {

    private StaffProtectAPI api;
    private Permission permission = new Permission() {};

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            this.permission = new LuckPermsHook();
        }
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

            @Contract(pure = true)
            @Override
            public @NotNull List<UUID> getSubscribers() {
                return subscribers;
            }
        };
        this.api = new StaffProtectAPIImpl(permission, bus);
        Bukkit.getServicesManager().register(StaffProtectAPI.class, api, this, ServicePriority.Normal);

        final var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(api), this);
        pluginManager.registerEvents(new PlayerConnectionListener(api), this);
    }
}
