package net.experience.powered.staffprotect.notification;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Sender {

    /**
     * Makes sender instance of this player
     * @param player player instance
     * @return instance
     */
    public abstract Sender player(final @NotNull Player player);

    /**
     * Makes sender instance for all player's in {@link NotificationBus#getSubscribers()}
     * @return instance
     */
    public abstract Sender all();

    /**
     * Sends message
     * @param component component
     */
    public abstract void sendMessage(final @NotNull Component component);

    /**
     * Sends message
     * @deprecated in favour of {@link Sender#sendMessage(Component)}
     * @param string string
     */
    @Deprecated
    public abstract void sendMessage(final @NotNull String string);
}
