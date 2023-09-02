package net.experience.powered.spigot.utils;

import net.experience.powered.spigot.StaffProtectPlugin;
import net.experience.powered.spigot.impl.VerificationImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class Expiring implements Runnable {

    private final BukkitTask task;
    private final VerificationImpl impl;
    private final Player player;
    private int time;

    public Expiring(final @NotNull BukkitTask task, final @NotNull Player player, final @NotNull VerificationImpl impl) {
        StaffProtectPlugin plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
        this.task = task;
        this.impl = impl;
        this.player = player;
        this.time = plugin.getConfig().getInt("staff-verification.qr-code.time-out", 60);
    }

    @Override
    public void run() {
        if (impl.isAuthorized(player) && !task.isCancelled()) {
            task.cancel();
        }
        if (time == 0 || time < 0) {
            task.cancel();

            final String fallback = "staff-verification.messages.time-expired";
            final StaffProtectPlugin plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
            final Component component = MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("staff-verification.messages.time-expired", fallback));
            impl.end(player);
            player.kickPlayer(LegacyComponentSerializer.legacyAmpersand().serialize(component));
        }
        time--;
    }

    // For future updates for actionbar
    public int getTime() {
        return time;
    }
}
