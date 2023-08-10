package net.experience.powered.staffprotect.notification;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface NotificationBus {

    /**
     * Subscribes player to notifications
     *
     * @param uuid player's unique id
     */
    void subscribe(final @NotNull UUID uuid);

    /**
     * Unsubscribes player to notifications
     *
     * @param uuid player's unique id
     */
    void unsubscribe(final @NotNull UUID uuid);

    /**
     * List of subscribers
     * @return list of subscribers
     */
    @NotNull List<UUID> getSubscribers();
}
