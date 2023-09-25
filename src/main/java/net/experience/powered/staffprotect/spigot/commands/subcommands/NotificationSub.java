package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class NotificationSub extends Subcommand {

    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        final JavaPlugin javaPlugin = StaffProtectPlugin.getInstance();
        final Player player = (Player) sender;
        if (args.length < 2) {
            final Component component = MiniMessage.miniMessage().deserialize("<red>Wrong command usage: <gold>/staffprotect notification subscribe/unsubscribe");
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.notification.missing-type"), component));
            return false;
        }
        final String type = args[1];
        final NotificationBus bus = StaffProtect.getInstance().getNotificationBus();
        if (type.equalsIgnoreCase("subscribe")) {
            bus.subscribe(player.getUniqueId());
            final Component component = Component.text("Notifications were enabled.", GOLD);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.notification.enabled"), component));
            return true;
        }
        if (type.equalsIgnoreCase("unsubscribe")) {
            bus.unsubscribe(player.getUniqueId());
            final Component component = Component.text("Notifications were disabled.", GOLD);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.notification.disabled"), component));
            return true;
        }
        return command(sender, new String[]{"notification"});
    }
}
