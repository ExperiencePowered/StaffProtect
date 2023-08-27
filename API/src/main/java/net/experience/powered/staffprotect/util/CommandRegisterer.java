package net.experience.powered.staffprotect.util;

import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;


public interface CommandRegisterer {
    boolean register(final @NotNull Command command);
}