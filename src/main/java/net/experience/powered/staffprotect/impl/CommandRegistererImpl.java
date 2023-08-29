package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.util.CommandRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class CommandRegistererImpl implements CommandRegisterer {

    private CommandMap commandMap;

    public CommandRegistererImpl(final @NotNull StaffProtect api, final @NotNull String bukkitName) {
        switch (bukkitName) {
            case "Paper", "Spigot" -> init(api, bukkitName);
            default -> {
                Bukkit.getLogger().warning("You're running on unsupported version.");
                init(api, bukkitName);
            }
        }
    }

    private void init(final @NotNull StaffProtect api, final @NotNull String bukkitName) {
        try {
            Field fCommandMap;
            fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            fCommandMap.setAccessible(true);
            final Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
            if (commandMapObject instanceof CommandMap) {
                this.commandMap = (CommandMap) commandMapObject;
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean register(final @NotNull Command command) {
        return commandMap.register("staffProtect", command);
    }

    @Override
    public boolean unregister(@NotNull Command command) {
        final Command foundCommand = commandMap.getCommand(command.getName());
        if (foundCommand == null) {
            return false;
        }
        return foundCommand.unregister(commandMap);
    }
}
