package net.experience.powered.staffprotect.addons;

import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MinecraftCommand extends Command {

    public MinecraftCommand(final @NotNull String name) {
        super(name);
    }

    public MinecraftCommand(final @NotNull String name, final @NotNull String description) {
        this(name, description, "", new ArrayList<>());
    }

    public MinecraftCommand(final @NotNull String name, final @NotNull String description, final @NotNull List<String> aliases) {
        this(name, description, "", aliases);
    }

    public MinecraftCommand(final @NotNull String name, final @NotNull String description, final @NotNull String usageMessage, final @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }
}
