package net.experience.powered.staffprotect.spigot.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class Subcommand {

    // Will be added in future, for now is commented as there is no use for it
    //private final String label;

    //public Subcommand(final @NotNull String label) {
    //    this.label = label;
    //}

    public final void register() {
        SubcommandManager.register(getClass(), this);
    }

    public final void unregister() {
        SubcommandManager.unregister(getClass());
    }

    //public final String getLabel() {
    //    return label;
    //}

    public abstract boolean command(final @NotNull CommandSender sender, final @NotNull String[] args);

}
