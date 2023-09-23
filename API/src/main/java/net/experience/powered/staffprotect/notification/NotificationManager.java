package net.experience.powered.staffprotect.notification;

import net.experience.powered.staffprotect.records.ActionType;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
     * Sends a message to all subscribers ({@link NotificationBus#getModernSubscribers()})
     * @param player player which caused notification to happen
     * @param uuid player's uuid which caused notification
     * @param actionType type of action which happened
     * @param component component which contains the message
     */
    public abstract void sendMessage(final @NotNull String player, final @NotNull UUID uuid, final @NotNull Component component, final @NotNull ActionType actionType);

    /**
     * Sends a message to record file
     * @param player player which caused notification to happen, can be null, although it is not good to do and may be changed in the future
     * @param string message
     * @param actionType type of action which happened
     */
    public abstract void sendQuietMessage(final @Nullable String player, final @NotNull String string, final @NotNull ActionType actionType);

    /**
     * Gets public instance of this class
      * @return instance of this class
     */
    public static NotificationManager getInstance() {
        return instance;
    }
}
