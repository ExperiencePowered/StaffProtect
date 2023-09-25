package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.records.ActionType;
import net.experience.powered.staffprotect.records.RecordFile;
import net.experience.powered.staffprotect.spigot.StaffProtectPlugin;
import net.experience.powered.staffprotect.spigot.impl.RecordFileImpl;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class RecordSub extends Subcommand {

    private final static String limitType = "l:";
    private final static String playerType = "p:";
    private final static String actionType = "a:";
    private final static String timeType = "t:";

    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String[] args) {
        final Player player = (Player) sender;
        final JavaPlugin javaPlugin = StaffProtectPlugin.getInstance();

        final boolean limitState = Arrays.stream(args).anyMatch(str -> str.contains(limitType));
        final boolean playerState = Arrays.stream(args).anyMatch(str -> str.contains(playerType));
        final boolean actionState = Arrays.stream(args).anyMatch(str -> str.contains(actionType));
        final boolean timeState = Arrays.stream(args).anyMatch(str -> str.contains(timeType));
        String playerName = null;
        Integer limit = null;
        ActionType action = null;
        String time = null;
        MiniMessage miniMessage = MiniMessage.miniMessage();
        for (String string : args) {
            if (limitState && string.startsWith(limitType)) {
                String cut = string.substring(limitType.length());
                try {
                    limit = Integer.parseInt(cut);
                } catch (NumberFormatException e) {
                    String fallback = "<red>Couldn't parse limit as a number!";
                    SenderImpl.getInstance(player).sendMessage(miniMessage.deserialize(javaPlugin.getConfig().getString("commands.records.parse-error", fallback)));
                    return false;
                }
            }
            if (playerState && string.startsWith(playerType)) {
                playerName = string.substring(playerType.length());
            }
            if (actionState && string.startsWith(actionType)) {
                String cut = string.substring(actionType.length());
                try {
                    action = ActionType.valueOf(cut.toUpperCase());
                } catch (IllegalArgumentException e) {
                    String fallback = "<red>This action does not exist.";
                    SenderImpl.getInstance(player).sendMessage(miniMessage.deserialize(javaPlugin.getConfig().getString("commands.records.unknown-action", fallback)));
                    return false;
                }
            }
            if (timeState && string.startsWith(timeType)) {
                String cut = string.substring(timeType.length());
                if (RecordFile.pattern.matcher(cut).matches()) {
                    time = cut;
                }
                else {
                    String fallback = "<red>This time does not follow time format! (e.g. 01-11-23)";
                    SenderImpl.getInstance(player).sendMessage(miniMessage.deserialize(javaPlugin.getConfig().getString("commands.records.invalid-time", fallback)));
                    return false;
                }
            }
        }
        SenderImpl impl = (SenderImpl) SenderImpl.getInstance(player);
        impl.sendMessage(miniMessage.deserialize(javaPlugin.getConfig().getString("commands.records.processing-query", "<gold>Processing query, this may take a while!")));
        RecordFileImpl.getInstance().readRecords(playerName, limit, action, time).thenAccept(list -> {
            impl.sendMessage(miniMessage.deserialize(
                    javaPlugin.getConfig().getString("commands.records.found-results", "<gold>We have found out <red><result_amount></red> results!"),
                    Placeholder.parsed("result_amount", "" + list.size())
            ));
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    javaPlugin.getConfig().getString("dateFormat", "YYYY.MM.dd HH:mm")
            );
            list.forEach(record -> {
                Date date = new Date(record.getTime());
                impl.sendMessage(miniMessage.deserialize(
                        javaPlugin.getConfig().getString("commands.records.result", "<red>(<time>)</red> <gold><player>: <content>"),
                        Placeholder.parsed("time", dateFormat.format(date)),
                        Placeholder.parsed("player", record.getPlayer()),
                        Placeholder.parsed("content", record.getContent()),
                        Placeholder.parsed("action", record.getAction().toString())
                ));
            });
        });
        return true;
    }
}
