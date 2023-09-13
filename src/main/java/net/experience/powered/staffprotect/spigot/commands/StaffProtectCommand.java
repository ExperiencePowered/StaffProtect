package net.experience.powered.staffprotect.spigot.commands;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.addons.AbstractAddon;
import net.experience.powered.staffprotect.spigot.commands.subcommands.*;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class StaffProtectCommand extends Command {

    private final StaffProtect api;

    private static final NamedTextColor DISABLED = RED;
    private static final NamedTextColor UNKNOWN = DARK_RED;
    private static final NamedTextColor ENABLED = GREEN;
    private static final NamedTextColor REGISTERED = GOLD;

    public StaffProtectCommand(final @NotNull StaffProtect api) {
        super("staffProtect", "Reload addons, show addons and more", "For general usage of StaffProtect", new ArrayList<>());
        this.api = api;
        api.getCommandManager().register(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is only for players");
            return false;
        }
        final JavaPlugin javaPlugin = api.getPlugin();
        if (!sender.hasPermission("staffprotect.admin")) {
            final Component component = Component.text("You do not have enough permissions to continue.", RED);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.insufficient-permissions"), component));
            return false;
        }
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            return SubcommandManager.getSubcommand(HelpSub.class).command(sender, args);
        }

        final String sub = args[0];
        if (sub.equalsIgnoreCase("reload")) {
            return SubcommandManager.getSubcommand(ReloadSub.class).command(sender, args);
        }
        if (sub.equalsIgnoreCase("reloadglobal")) {
            return SubcommandManager.getSubcommand(ReloadGlobalSub.class).command(sender, args);
        }
        if (sub.equalsIgnoreCase("addon")) {
            return SubcommandManager.getSubcommand(AddonSub.class).command(sender, args);
        }
        if (sub.equalsIgnoreCase("addons")) {
            return SubcommandManager.getSubcommand(AddonSub.class).command(sender, args);
        }
        if (sub.equalsIgnoreCase("notification")) {
            return SubcommandManager.getSubcommand(NotificationSub.class).command(sender, args);
        }
        return execute(sender, commandLabel, new String[]{});
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("help", "reload", "reloadglobal", "addon", "addons", "notification"), new ArrayList<>());
        }
        else if (args.length > 0 && args[0].equalsIgnoreCase("addon")) {
            if (args.length == 2) {
                List<String> list = new ArrayList<>();
                for (final AbstractAddon addon : api.getAddonManager().getAddons()) {
                    list.add(addon.toString());
                }
                return StringUtil.copyPartialMatches(args[1], list, new ArrayList<>());
            }
            if (args.length == 3) {
                return StringUtil.copyPartialMatches(args[2], List.of("enable", "disable", "reload"), new ArrayList<>());
            }
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("notification")) {
            return StringUtil.copyPartialMatches(args[1], List.of("subscribe", "unsubscribe"), new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public static NamedTextColor getColor(final @NotNull AbstractAddon.LoadingState loadingState) {
        switch (loadingState) {
            case ENABLED -> {
                return ENABLED;
            }
            case DISABLED -> {
                return DISABLED;
            }
            case REGISTERED -> {
                return REGISTERED;
            }
            default -> {
                return UNKNOWN;
            }
        }
    }
}
