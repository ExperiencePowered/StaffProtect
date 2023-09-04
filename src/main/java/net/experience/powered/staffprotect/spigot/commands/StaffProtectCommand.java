package net.experience.powered.staffprotect.spigot.commands;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.addons.AbstractAddon;
import net.experience.powered.staffprotect.addons.GlobalConfiguration;
import net.experience.powered.staffprotect.spigot.impl.AddonManagerImpl;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.experience.powered.staffprotect.notification.NotificationBus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
                SenderImpl.getInstance(player).sendMessage(component);
            }
            return true;
        }

        final String sub = args[0];
        if (sub.equalsIgnoreCase("reload")) {
            final Component component = Component.text("Reloaded config.yml.", GOLD);
            javaPlugin.saveConfig();
            javaPlugin.reloadConfig();
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.reloaded-config"), component));
            return true;
        }
        if (sub.equalsIgnoreCase("reloadglobal")) {
            final Component component = Component.text("Reloaded global_configuration.yml.", GOLD);
            final GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();
            globalConfiguration.saveConfig();
            globalConfiguration.reloadConfig();
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.reloaded-global-config"), component));
            return true;
        }

        if (sub.equalsIgnoreCase("addon")) {
            if (args.length < 2) {
                final Component component = Component.text("Missing addon name.", RED);
                SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.addon.missing-name"), component));
                return false;
            }
            final AddonManagerImpl impl = (AddonManagerImpl) api.getAddonManager();
            final @Nullable AbstractAddon addon = impl.findAddon(args[1]);
            if (addon == null) {
                final Component component = Component.text("Couldn't find addon with this name.", RED);
                SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.addon.invalid-addon"), component));
                return false;
            }
            if (args.length < 3) {
                final Component component = Component.text("Missing action type (reload/enable/disable).", RED);
                SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.addon.missing-action"), component));
                return false;
            }
            final String action = args[2];
            final String pluginName = addon.getAddonFile().pluginName();
            if (action.equalsIgnoreCase("reload")) {
                final boolean result = reloadAddon(javaPlugin, impl, addon, player);
                final Component component = Component.text("Reloaded addon ", GOLD).append(Component.text(pluginName, BLUE));
                final String string = javaPlugin.getConfig().getString("commands.addon.reloaded");
                if (result) {
                    if (string == null) {
                        SenderImpl.getInstance(player).sendMessage(component);
                    }
                    else {
                        SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(string, Placeholder.parsed("addon", pluginName)));
                    }
                }
                return result;
            }
            else if (action.equalsIgnoreCase("enable")) {
                final boolean result = enableAddon(javaPlugin, impl, addon, player);
                final Component component = Component.text("enabled addon ", GOLD).append(Component.text(pluginName, BLUE));
                final String string = javaPlugin.getConfig().getString("commands.addon.enabled");
                if (result) {
                    if (string == null) {
                        SenderImpl.getInstance(player).sendMessage(component);
                    }
                    else {
                        SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(string, Placeholder.parsed("addon", pluginName)));
                    }
                }
                return result;
            }
            else if (action.equalsIgnoreCase("disable")) {
                final boolean result = disableAddon(javaPlugin, impl, addon, player);
                final Component component = Component.text("Disabled addon ", GOLD).append(Component.text(pluginName, BLUE));
                final String string = javaPlugin.getConfig().getString("commands.addon.disabled");
                if (result) {
                    if (string == null) {
                        SenderImpl.getInstance(player).sendMessage(component);
                    }
                    else {
                        SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserialize(string, Placeholder.parsed("addon", pluginName)));
                    }
                }
                return result;
            }
            final Component component = Component.text("Couldn't find action type with this input.", RED);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.addon.invalid-action"), component));
            return false;
        }

        if (sub.equalsIgnoreCase("addons")) {
            final Set<AbstractAddon> addons = api.getAddonManager().getAddons();
            Component component = MiniMessage.miniMessage().deserialize("<blue>Addons <white>(<gold>" + addons.size() + "<white>): ");
            final Iterator<AbstractAddon> iterator = addons.iterator();
            while (iterator.hasNext()) {
                final AbstractAddon addon = iterator.next();
                component = component.append(Component.text(addon.getAddonFile().pluginName(), getColor(addon.getLoadingState())));
                if (iterator.hasNext()) {
                    component = component.append(Component.text(", ", BLUE));
                }
            }
            final Component fComponent = component;
            SenderImpl.getInstance(player).sendMessage(fComponent);
            return true;
        }

        if (sub.equalsIgnoreCase("notification")) {
            if (args.length < 2) {
                final Component component = MiniMessage.miniMessage().deserialize("<red>Wrong command usage: <gold>/staffprotect notification subscribe/unsubscribe");
                SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.notification.missing-type"), component));
                return false;
            }
            final String type = args[1];
            final NotificationBus bus = api.getNotificationBus();
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
            return execute(sender, commandLabel, new String[]{"notification"});
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

    public NamedTextColor getColor(final @NotNull AbstractAddon.LoadingState loadingState) {
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

    private boolean reloadAddon(final @NotNull JavaPlugin javaPlugin, final @NotNull AddonManagerImpl impl, final @NotNull AbstractAddon addon, final @NotNull Player player) {
        disableAddon(javaPlugin, impl, addon, player);
        enableAddon(javaPlugin, impl, addon, player);
        return true;
    }

    private boolean disableAddon(final @NotNull JavaPlugin javaPlugin, final @NotNull AddonManagerImpl impl, final @NotNull AbstractAddon addon, final @NotNull Player player) {
        final String string = javaPlugin.getConfig().getString("commands.addon.already.disabled");
        if (addon.getLoadingState().equals(AbstractAddon.LoadingState.ENABLED)) {
            impl.disable(addon);
            return true;
        }
        else {
            final Component component = Component.text("Addon is already disabled.", RED);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(string, component));
            return false;
        }
    }

    private boolean enableAddon(final @NotNull JavaPlugin javaPlugin, final @NotNull AddonManagerImpl impl, final @NotNull AbstractAddon addon, final @NotNull Player player) {
        final String string = javaPlugin.getConfig().getString("commands.addon.already.enabled");
        if (addon.getLoadingState().equals(AbstractAddon.LoadingState.DISABLED)) {
            impl.enable(addon);
            return true;
        }
        else {
            final Component component = Component.text("Addon is already enabled.", RED);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(string, component));
            return false;
        }
    }
}
