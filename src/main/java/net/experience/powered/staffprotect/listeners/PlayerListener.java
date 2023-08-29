package net.experience.powered.staffprotect.listeners;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.StaffProtectPlugin;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final StaffProtect api;
    private final StaffProtectPlugin plugin;

    public PlayerListener(final @NotNull StaffProtect api) {
        this.api = api;
        this.plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
    }

    @EventHandler
    public void PlayerJoin(final @NotNull PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (player.hasPermission("staffprotect.notification")) {
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
        final Configuration configuration = plugin.getConfig();
        final String string = configuration.getString("notification.command-executed", "String not found.");
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        final Component component = miniMessage.deserialize(string, Placeholder.parsed("player", player.getName()), Placeholder.parsed("command", e.getMessage()));
        NotificationManager.getInstance().sendMessage(player.getName(), component);
    }
}
