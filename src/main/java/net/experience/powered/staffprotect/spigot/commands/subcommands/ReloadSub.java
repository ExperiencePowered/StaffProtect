package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.experience.powered.staffprotect.spigot.StaffProtectPlugin.getInstance;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class ReloadSub extends Subcommand {

    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String[] args) {
        final StaffProtectPlugin plugin = getInstance();
        final Component component = Component.text("Reloaded config.yml.", GOLD);
        plugin.saveConfig();
        plugin.reloadConfig();
        SenderImpl.getInstance((Player) sender).sendMessage(MiniMessage.miniMessage().deserializeOr(plugin.getConfig().getString("commands.reloaded-config"), component));
        return true;
    }
}
