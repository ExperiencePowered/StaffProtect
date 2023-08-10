package net.experience.powered.staffprotect.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Permission {
    default boolean hasPermission(final @NotNull UUID uuid, final @NotNull String permission) {
        final Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return player.hasPermission(permission);
    }
}