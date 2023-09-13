package net.experience.powered.staffprotect.spigot.utils;

import net.experience.powered.staffprotect.records.Record;
import net.experience.powered.staffprotect.records.RecordFile;
import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.impl.VerificationImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Expiring extends BukkitRunnable {

    private final VerificationImpl impl;
    private final Player player;
    private int time;

    public Expiring(final @NotNull Player player, final @NotNull VerificationImpl impl) {
        StaffProtectPlugin plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
        this.impl = impl;
        this.player = player;
        this.time = plugin.getConfig().getInt("staff-verification.qr-code.time-out", 60);

        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        if (impl.isAuthorized(player) && !isCancelled()) {
            cancel();
        }
        if (time == 0 || time < 0) {
            cancel();

            final String fallback = "<red>Your authorizing took too long.";
            final StaffProtectPlugin plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
            final Component component = MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("staff-verification.messages.time-expired", fallback));
            impl.end(player);
            player.kickPlayer(LegacyComponentSerializer.legacySection().serialize(component));
            RecordFile.getInstance().writeRecord(new Record(System.currentTimeMillis(), player.getName(), "Staff failed to authorize (Authorizing took too long)."));
        }
        time--;
    }

    // For future updates for actionbar
    public int getTime() {
        return time;
    }
}
