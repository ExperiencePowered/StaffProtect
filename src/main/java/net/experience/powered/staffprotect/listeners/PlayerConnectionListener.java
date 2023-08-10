package net.experience.powered.staffprotect.listeners;

import net.experience.powered.staffprotect.StaffProtectAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final StaffProtectAPI api;

    public PlayerConnectionListener(final @NotNull StaffProtectAPI api) {
        this.api = api;
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
}
