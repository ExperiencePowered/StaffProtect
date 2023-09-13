package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.addons.AbstractAddon;
import net.experience.powered.staffprotect.spigot.commands.StaffProtectCommand;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

import static net.kyori.adventure.text.format.NamedTextColor.BLUE;

public class AddonsSub extends Subcommand {


    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String[] args) {
        final Set<AbstractAddon> addons = StaffProtect.getInstance().getAddonManager().getAddons();
        Component component = MiniMessage.miniMessage().deserialize("<blue>Addons <white>(<gold>" + addons.size() + "<white>): ");
        final Iterator<AbstractAddon> iterator = addons.iterator();
        while (iterator.hasNext()) {
            final AbstractAddon addon = iterator.next();
            component = component.append(Component.text(addon.getAddonFile().pluginName(), StaffProtectCommand.getColor(addon.getLoadingState())));
            if (iterator.hasNext()) {
                component = component.append(Component.text(", ", BLUE));
            }
        }
        final Component fComponent = component;
        SenderImpl.getInstance((Player) sender).sendMessage(fComponent);
        return true;
    }
}
