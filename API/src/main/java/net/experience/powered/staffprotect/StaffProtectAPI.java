package net.experience.powered.staffprotect;

import net.experience.powered.staffprotect.interfaces.Permission;
import net.experience.powered.staffprotect.notification.NotificationBus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * API providing some useful interfaces which should be used in addons
 */
public interface StaffProtectAPI {

    /**
     * Getter for Permission class
     *
     * @return permission class which comes with simple {@link Permission#hasPermission(UUID, String)} method that supports both vanilla and luckperms permissions
     */
    @NotNull Permission getPermission();

    /**
     * Getter for NotificationBus class
     *
     * @return notification bus class with some methods related to logging
     */
    @NotNull NotificationBus getNotificationBus();

    /**
     * Gets plugin instance which loaded this class
     * @return plugin class
     */
    @NotNull JavaPlugin getPlugin();

    /**
     * Gets instance directly, so you don't have to write code for getting instance
     * @return instance of this class
     * @throws IllegalStateException thrown when plugin accessing this code is loaded before StaffProtect so this class isn't initialized yet, this can be fixed by adding 'StaffProtect' as a dependency in plugin.yml
     */
    static @NotNull StaffProtectAPI getInstance() throws IllegalStateException {
        final ServicesManager servicesManager = Bukkit.getServicesManager();
        final RegisteredServiceProvider<StaffProtectAPI> provider = servicesManager.getRegistration(StaffProtectAPI.class);
        if (provider != null) return provider.getProvider();
        throw new IllegalStateException("StaffProtect was not initialised.");
    }
}
