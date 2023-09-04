package net.experience.powered.staffprotect.waterfall.messages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.experience.powered.staffprotect.waterfall.StaffProtectBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PluginMessageManager implements Listener {

    @EventHandler
    public void onMessage(final @NotNull PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase( "staffprotect:bungee")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase( "authorized")) {
            if (e.getReceiver() instanceof ProxiedPlayer receiver) {
                StaffProtectBungee.getInstance().getAuthorized().put(receiver.getUniqueId(), true);
            }
        }
    }

    public void sendAuthorization(final @NotNull ProxiedPlayer player, final @Nullable Server server) {
        final ServerInfo info = server == null ? player.getServer().getInfo() : server.getInfo();

        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();
        if ( networkPlayers == null || networkPlayers.isEmpty()) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("authorized");
        info.sendData("staffprotect:spigot", out.toByteArray(), true);
    }
}
