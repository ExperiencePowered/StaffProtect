package net.experience.powered.staffprotect.spigot.utils;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.impl.VerificationImpl;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Authorizer extends BukkitRunnable {

    private final Player player;
    private int i = 0;

    public Authorizer(final @NotNull Player player) {
        this.player = player;
        runTaskTimer(StaffProtectPlugin.getPlugin(StaffProtectPlugin.class), 0L, 1L);
    }

    @Override
    public void run() {
        i++;
        if (QRCode.getCodes().containsKey(player.getUniqueId())) {
            VerificationImpl.getInstance().forceAuthorize(player);
            cancel();
        }
        if (i == 20) {
            cancel();
            StaffProtect.getInstance().getPlugin().getLogger().warning("Could not authorize player " + player.getName());
        }
    }
}
