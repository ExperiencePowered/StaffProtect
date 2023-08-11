package net.experience.powered.staffprotect.listeners;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final StaffProtectAPI api;
    private final StaffProtect plugin;

    public PlayerListener(final @NotNull StaffProtectAPI api) {
        this.api = api;
        this.plugin = StaffProtect.getPlugin(StaffProtect.class);
    }

    @EventHandler
    public void PlayerJoin(final @NotNull PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (api.getPermission().hasPermission(uuid, "staffprotect.notification")) {
            api.getNotificationBus().subscribe(uuid);
        }
    }

    @EventHandler
    public void PlayerQuit(final @NotNull PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        api.getNotificationBus().unsubscribe(uuid);
    }

    @EventHandler
    public void PlayerCommandExecute(final @NotNull PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        final var configuration = plugin.getConfig();
        final var string = configuration.getString("notification.command-executed", "String not found.");
        final var miniMessage = MiniMessage.miniMessage();
        final var component = miniMessage.deserialize(string, Placeholder.parsed("player", player.getName()), Placeholder.parsed("command", e.getMessage()));
        final var notificationManager = NotificationManager.getInstance(api.getNotificationBus());

        notificationManager.sendMessage(component);
    }
}
