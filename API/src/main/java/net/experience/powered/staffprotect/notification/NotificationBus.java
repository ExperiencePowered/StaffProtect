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
     * @deprecated in favour of {@link #getModernSubscribers()}
     */
    @NotNull List<UUID> getSubscribers();

    /**
     * Subscribes player to notifications
     * @param subscriber subscriber
     */
    void subscribe(final @NotNull Subscriber subscriber);

    /**
     * Unsubscribes player to notifications
     * @param subscriber subscriber
     */
    void unsubscribe(final @NotNull Subscriber subscriber);

    /**
     * List of subscribers
     * @return list of subscribers
     */
    @NotNull List<Subscriber> getModernSubscribers();
}
