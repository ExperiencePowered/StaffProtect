package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HelpSub extends Subcommand {

    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String[] args) {
        final List<Component> components = new ArrayList<>();
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        components.add(miniMessage.deserialize("<bold><white>Staff<gradient:#a541ff:#3fbbfe>Protect <gold>Help Menu</bold>"));
        components.add(miniMessage.deserialize("<red> "));
        components.add(miniMessage.deserialize("<red>/staffprotect help <blue>| <gold>Shows available commands"));
        components.add(miniMessage.deserialize("<red>/staffprotect reload <blue>| <gold>Reloads config.yml"));
        components.add(miniMessage.deserialize("<red>/staffprotect reloadglobal <blue>| <gold>Reloads global_configuration.yml"));
        components.add(miniMessage.deserialize("<red>/staffprotect addon <addon> reload <blue>| <gold>Reloads addon"));
        components.add(miniMessage.deserialize("<red>/staffprotect addon <addon> disable <blue>| <gold>Disables addon"));
        components.add(miniMessage.deserialize("<red>/staffprotect addon <addon> enable <blue>| <gold>Enables addon"));
        components.add(miniMessage.deserialize("<red>/staffprotect addons <blue>| <gold>Shows all addons"));
        components.add(miniMessage.deserialize("<red>/staffprotect notification subscribe <blue>| <gold>Subscribes to notifications"));
        components.add(miniMessage.deserialize("<red>/staffprotect notification unsubscribe <blue>| <gold>Unsubscribes to notifications"));
        components.add(miniMessage.deserialize("<red> "));
        for (Component component : components) {
            SenderImpl.getInstance(((Player) sender)).sendMessage(component);
        }
        return true;
    }
}
