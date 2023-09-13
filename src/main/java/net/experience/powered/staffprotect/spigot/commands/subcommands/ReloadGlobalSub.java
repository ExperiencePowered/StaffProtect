package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.addons.GlobalConfiguration;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.experience.powered.staffprotect.spigot.StaffProtectPlugin.getInstance;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class ReloadGlobalSub extends Subcommand {

    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String[] args) {
        final Component component = Component.text("Reloaded global_configuration.yml.", GOLD);
        final GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();
        globalConfiguration.saveConfig();
        globalConfiguration.reloadConfig();
        SenderImpl.getInstance((Player) sender).sendMessage(MiniMessage.miniMessage().deserializeOr(getInstance().getConfig().getString("commands.reloaded-global-config"), component));
        return true;
    }
}
