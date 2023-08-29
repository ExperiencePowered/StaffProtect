package net.experience.powered.staffprotect;

import net.experience.powered.staffprotect.addons.AddonManager;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.experience.powered.staffprotect.notification.Sender;
import net.experience.powered.staffprotect.util.CommandRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * API providing some useful interfaces which should be used in addons
 * <p>
 * To get instance of this class you have three options:<br>
 * a) You can use deprecated method {@link StaffProtect#getInstance()}, which does not have any issues although is not recommended to use as you can't write your own logic for this stuff<br>
 * b) You can use method {@link StaffProtect#getRegistration()}, which is more effective than option a) as it is flexible as possible<br>
 * c) You can also use class {@link StaffProtectProvider} where is method {@link StaffProtectProvider#getInstance()}, this option should be used for simple gaining instance of this class
 */
public interface StaffProtect {

    /**
     * Getter for NotificationBus class
     *
     * @return notification bus class with some methods related to logging
     */
    @NotNull NotificationBus getNotificationBus();

    /**
     * Getter for plugin instance which loaded this class
     * @return plugin class
     */
    @NotNull JavaPlugin getPlugin();

    /**
     * Getter for addon manager which is manager for {@link net.experience.powered.staffprotect.addons.AbstractAddon}
     * @return addon manager class
     */
    @NotNull AddonManager getAddonManager();

    /**
     * Getter for command registerer
     * @return command registerer class
     */
    @NotNull CommandRegisterer getCommandManager();

    /**
     * Returns new instance of class {@link Sender}
     * @return sender class for sending messages to all players
     */
    @NotNull Sender getDefaultSender();

    /**
     * Gets instance directly, so you don't have to write code for getting instance
     * @return instance of this class
     * @throws IllegalStateException thrown when plugin accessing this code is loaded before StaffProtect so this class isn't initialized yet, this can be fixed by adding 'StaffProtect' as a dependency in plugin.yml
     * @deprecated In favour of {@link StaffProtect#getRegistration()}
     */
    @Deprecated
    static @NotNull StaffProtect getInstance() throws IllegalStateException {
        final Optional<RegisteredServiceProvider<StaffProtect>> provider = StaffProtect.getRegistration();
        if (provider.isPresent()) return provider.get().getProvider();
        throw new IllegalStateException("StaffProtect was not initialised.");
    }

    /**
     * Gets instance of this class
     * @return instance of this class
     * @throws IllegalStateException thrown when plugin accessing this code is loaded before StaffProtect so this class isn't initialized yet, this can be fixed by adding 'StaffProtect' as a dependency in plugin.yml
     */
    static @NotNull Optional<RegisteredServiceProvider<StaffProtect>> getRegistration() {
        final ServicesManager servicesManager = Bukkit.getServicesManager();
        final RegisteredServiceProvider<StaffProtect> provider = servicesManager.getRegistration(StaffProtect.class);
        return Optional.ofNullable(provider);
    }
}
