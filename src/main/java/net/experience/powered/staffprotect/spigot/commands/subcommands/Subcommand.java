package net.experience.powered.staffprotect.spigot.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class Subcommand {

    public final void register() {
        SubcommandManager.register(getClass(), this);
    }

    public final void unregister() {
        SubcommandManager.unregister(getClass());
    }

    public abstract boolean command(final @NotNull CommandSender sender, final @NotNull String[] args);

}
