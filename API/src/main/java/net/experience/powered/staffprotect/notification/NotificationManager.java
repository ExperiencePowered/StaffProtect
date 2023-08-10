package net.experience.powered.staffprotect.notification;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Efficient notification manager through you can send a message
 */
public class NotificationManager {

    private final NotificationBus bus;

    public NotificationManager(final @NotNull NotificationBus bus) {
        this.bus = bus;
    }

    /**
     * Sends a message to all subscribers ({@link NotificationBus#getSubscribers()})
     * @param component message to send
     */
    public void sendMessage(final @NotNull Component component) {
        bus.getSubscribers().forEach(uuid -> {
            // Get player async
            final var future = CompletableFuture.supplyAsync(() -> Bukkit.getPlayer(uuid));
            future.thenAcceptAsync(player -> {
                if (player == null) {
                    throw new IllegalStateException("Player is offline, but is still subscribing to notifications");
                }
                // Send message async
                player.sendMessage(component);
            });
        });
    }

    /**
     * Sends a message to all subscribers ({@link NotificationBus#getSubscribers()})
     * @param string message to send, which will be later serialized to a component
     * @deprecated Deprecated in favor of {@link NotificationManager#sendMessage(Component)}
     */
    @Deprecated(forRemoval = true)
    public void sendMessage(final @NotNull String string) {
        // Serializes string to component
        final var component = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
        bus.getSubscribers().forEach(uuid -> {
            // Get player async
            final var future = CompletableFuture.supplyAsync(() -> Bukkit.getPlayer(uuid));
            future.thenAcceptAsync(player -> {
                if (player == null) {
                    throw new IllegalStateException("Player is offline, but is still subscribing to notifications");
                }
                // Send message async
                player.sendMessage(component);
            });
        });
    }

    /**
     * Creates new instance with specified param
     * @param bus notification bus which contains all subscribers
     * @return new instance of this class
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull NotificationManager getInstance(final @NotNull NotificationBus bus) {
        return new NotificationManager(bus);
    }

    /**
     * Creates new instance with notification bus got from static singleton of StaffProtectAPI ({@link StaffProtectAPI#getInstance()}), it is recommended to use {@link NotificationManager#getInstance(NotificationBus)} instead
     * @return new instance of this class
     */
    @Contract(" -> new")
    public static @NotNull NotificationManager getInstance() {
        return getInstance(StaffProtectAPI.getInstance().getNotificationBus());
    }
}
