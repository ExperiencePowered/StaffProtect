package net.experience.powered.staffprotect.waterfall.listeners;

import net.experience.powered.staffprotect.waterfall.StaffProtectBungee;
import net.experience.powered.staffprotect.waterfall.configuration.ProxyConfiguration;
import net.experience.powered.staffprotect.waterfall.impl.SenderImpl;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(final @NotNull PostLoginEvent e) {
        StaffProtectBungee.getInstance().getAuthorized().put(e.getPlayer().getUniqueId(), false);
    }

    @EventHandler
    public void onLeave(final @NotNull PlayerDisconnectEvent e) {
        StaffProtectBungee.getInstance().getAuthorized().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerConnect(final @NotNull ServerConnectEvent e) {
        if (!StaffProtectBungee.getInstance().getAuthorized().getOrDefault(e.getPlayer().getUniqueId(), false)) {
            if (!(e.getReason() == ServerConnectEvent.Reason.JOIN_PROXY)) {
                e.setCancelled(true);
                SenderImpl.getInstance(e.getPlayer()).sendMessage(MiniMessage.miniMessage().deserialize(ProxyConfiguration.unauthorized_access));
            }
        }
    }

    @EventHandler
    public void onServerSwitch(final @NotNull ServerSwitchEvent e) {
        if (StaffProtectBungee.getInstance().getAuthorized().getOrDefault(e.getPlayer().getUniqueId(), false)) {
            if (e.getFrom() != null) {
                StaffProtectBungee.getInstance().getMessageManager().sendAuthorization(e.getPlayer(), e.getPlayer().getServer());
            }
        }
    }

    @EventHandler
    public void onChat(final @NotNull ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer player)) {
            return;
        }
        final boolean isCommand = e.getMessage().startsWith("/");
        if (isCommand && !StaffProtectBungee.getInstance().getAuthorized().getOrDefault(player.getUniqueId(), false)) {
            e.setCancelled(true);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(ProxyConfiguration.unauthorized_access));
        }
    }
}
