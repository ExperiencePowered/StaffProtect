package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.util.CommandRegisterer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.MorePaperLib;

import java.lang.reflect.Field;

public class CommandRegistererImpl implements CommandRegisterer {

    private CommandMap commandMap;

    private CommandRegistererImpl(final @NotNull String bukkitName) {
        final var api = StaffProtectAPI.getInstance();
        final var paperLib = new MorePaperLib(api.getPlugin());
        switch (bukkitName) {
            // Changed with MorePaperLib api which ensures backwards compatibility
            case "Paper", "Spigot" -> commandMap = paperLib.commandRegistration().getServerCommandMap();
            default -> {
                Bukkit.getLogger().warning("You're running on unsupported version.");
                try {
                    Field fCommandMap;
                    fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                    fCommandMap.setAccessible(true);
                    final Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
                    if (commandMapObject instanceof CommandMap) {
                        this.commandMap = (CommandMap) commandMapObject;
                    }
                } catch (final Exception e) {
                    new CommandRegistererImpl("Spigot");
                }
            }
        }
    }

    @Override
    public void register(final @NotNull Command command) {
        commandMap.register("staffProtect", command);
    }
}
