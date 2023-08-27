package net.experience.powered.staffprotect.notification;

import net.experience.powered.staffprotect.StaffProtect;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Efficient notification manager through you can send a message
 * @deprecated since this is an inefficient way of sending components, use {@link Sender} instead
 */
@Deprecated(forRemoval = true)
public class NotificationManager {

    private final NotificationBus bus;
    private final JavaPlugin plugin;

    public NotificationManager(final @NotNull JavaPlugin plugin, final @NotNull NotificationBus bus) {
        this.bus = bus;
        this.plugin = plugin;
    }

    /**
     * Sends a message to all subscribers ({@link NotificationBus#getSubscribers()})
     * @param component message to send
     */
    public void sendMessage(final @NotNull Component component) {
        final BukkitAudiences audience = BukkitAudiences.create(plugin);
        bus.getSubscribers().forEach(uuid -> {
            // Get player async
            final CompletableFuture<Player> future = CompletableFuture.supplyAsync(() -> Bukkit.getPlayer(uuid));
            future.thenAcceptAsync(player -> {
                if (player == null) {
                    throw new IllegalStateException("Player is offline, but is still subscribing to notifications");
                }
                audience.player(uuid).sendMessage(component);
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
        final BukkitAudiences audience = BukkitAudiences.create(plugin);
        // Serializes string to component
        final Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
        bus.getSubscribers().forEach(uuid -> {
            // Get player async
            final CompletableFuture<Player> future = CompletableFuture.supplyAsync(() -> Bukkit.getPlayer(uuid));
            future.thenAcceptAsync(player -> {
                if (player == null) {
                    throw new IllegalStateException("Player is offline, but is still subscribing to notifications");
                }
                audience.player(uuid).sendMessage(component);
            });
        });
    }

    /**
     * Creates new instance with notification bus got from static singleton of StaffProtectAPI
     * @return new instance of this class
     */
    @Contract(" -> new")
    public static @NotNull NotificationManager getInstance() {
        final StaffProtect api = StaffProtect.getInstance();
        return new NotificationManager(api.getPlugin(), api.getNotificationBus());
    }
}
