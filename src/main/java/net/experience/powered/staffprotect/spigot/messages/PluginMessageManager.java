package net.experience.powered.staffprotect.spigot.messages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.spigot.impl.VerificationImpl;
import net.experience.powered.staffprotect.spigot.utils.Authorizer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PluginMessageManager implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equalsIgnoreCase("staffprotect:spigot")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("authorized")) {
            VerificationImpl.getInstance().forceAuthorize(player);
            Authorizer.authorize(player);
        }
    }

    public void sendAuthorization(final @NotNull Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("authorized");
        player.sendPluginMessage(StaffProtect.getInstance().getPlugin(), "staffprotect:bungee", out.toByteArray());
    }
}
