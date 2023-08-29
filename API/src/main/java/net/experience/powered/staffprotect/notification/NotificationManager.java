package net.experience.powered.staffprotect.notification;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NotificationManager {

    private static NotificationManager instance;

    protected final NotificationBus bus;
    protected final JavaPlugin plugin;

    public NotificationManager(final @NotNull JavaPlugin plugin, final @NotNull NotificationBus bus) {
        this.bus = bus;
        this.plugin = plugin;

        NotificationManager.instance = this;
    }

    /**
     * Sends a message to all subscribers ({@link NotificationBus#getSubscribers()})
     * @param player player which caused notification to happen, can be null, although it is not good to do and may be changed in the future
     * @param component component which contains the message
     */
    public abstract void sendMessage(final @Nullable String player, final @NotNull Component component);

    /**
     * Sends a message to record file
     * @param player player which caused notification to happen, can be null, although it is not good to do and may be changed in the future
     * @param string message
     */
    public abstract void sendQuietMessage(final @Nullable String player, final @NotNull String string);

    /**
     * Gets public instance of this class
      * @return instance of this class
     */
    public static NotificationManager getInstance() {
        return instance;
    }
}
