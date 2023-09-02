package net.experience.powered.spigot.listeners;

import net.experience.powered.spigot.StaffProtectPlugin;
import net.experience.powered.spigot.impl.SenderImpl;
import net.experience.powered.spigot.impl.VerificationImpl;
import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.events.PlayerPreVerifyEvent;
import net.experience.powered.staffprotect.events.PlayerVerifyEvent;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.kyori.adventure.text.format.NamedTextColor.*;

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
        final boolean verification = plugin.getConfig().getBoolean("staff-verification.enabled", true);
        if (verification && player.hasPermission(plugin.getConfig().getString("staff-verification.permission", "group.staff")) || player.isOp()) {
            VerificationImpl.getInstance().start(player);
        }
    }

    @EventHandler
    public void onAsyncChat(final @NotNull AsyncPlayerChatEvent e) {
        final String message = e.getMessage();
        final Player player = e.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            final PlayerPreVerifyEvent preVerifyEvent = new PlayerPreVerifyEvent(player, message);
            Bukkit.getPluginManager().callEvent(preVerifyEvent);
            if (preVerifyEvent.isCancelled()) {
                return;
            }
            if (!VerificationImpl.getInstance().isAuthorized(player)) {
                e.setCancelled(true);

                int code;
                PlayerVerifyEvent verifyEvent;
                try {
                    code = Integer.parseInt(preVerifyEvent.getCode());
                } catch (NumberFormatException ex) {
                    verifyEvent = new PlayerVerifyEvent(player, -1, false);
                    Bukkit.getPluginManager().callEvent(verifyEvent);
                    player.sendMessage("Your code is invalid, please write all numbers without spaces.");
                    return;
                }

                if (VerificationImpl.getInstance().authorize(player, code)) {
                    verifyEvent = new PlayerVerifyEvent(player, code, true);
                    final String fallback = "<gold>Welcome on <server>.";
                    SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("staff-verification.messages.authorized", fallback),
                            Placeholder.parsed("server", plugin.getConfig().getString("staff-verification.qr-code.server-name", "ExampleServer"))));

                    Component component = Component.text("Authorized player ", RED)
                            .append(Component.text(player.getName(), BLUE))
                            .append(Component.text(".", RED));
                    NotificationManager.getInstance().sendMessage(player.getName(), component);
                }
                else {
                    verifyEvent = new PlayerVerifyEvent(player, code, false);
                    final String fallback = "<red>Failed to authorize you.";
                    SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("staff-verification.messages.failed-to-authorize", fallback)));

                    Component component = Component.text("Failed to authorize player ", RED)
                            .append(Component.text(player.getName(), BLUE))
                            .append(Component.text(".", RED));
                    NotificationManager.getInstance().sendMessage(player.getName(), component);
                }

                Bukkit.getPluginManager().callEvent(verifyEvent);
            }
        });
    }


    @EventHandler
    public void PlayerQuit(final @NotNull PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        api.getNotificationBus().unsubscribe(uuid);
        if (!VerificationImpl.getInstance().isAuthorized(player)) {
            VerificationImpl.getInstance().end(player);
        }
    }

    @EventHandler
    public void PlayerCommandExecute(final @NotNull PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();


        final String string = plugin.getConfig().getString("notification.command.executed", "String not found.");
        final Component component =  MiniMessage.miniMessage().deserialize(string,
                Placeholder.parsed("player", player.getName()), Placeholder.parsed("command", e.getMessage()));
        NotificationManager.getInstance().sendMessage(player.getName(), component);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerInteract(final @NotNull PlayerInteractEvent e) {
        if (!VerificationImpl.getInstance().isAuthorized(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerMove(final @NotNull PlayerMoveEvent e) {
        if (!VerificationImpl.getInstance().isAuthorized(e.getPlayer())) {
            if (e.getTo() != null) {
                if (e.getFrom().getX() != e.getTo().getX() ||
                    e.getFrom().getY() != e.getTo().getY() ||
                    e.getFrom().getZ() != e.getTo().getZ()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamage(final @NotNull EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        if (!VerificationImpl.getInstance().isAuthorized(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamageByEntity(final @NotNull EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) {
            return;
        }
        if (!VerificationImpl.getInstance().isAuthorized(player)) {
            e.setCancelled(true);
        }
    }
}
