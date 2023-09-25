package net.experience.powered.staffprotect.spigot.commands.subcommands;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.addons.AbstractAddon;
import net.experience.powered.staffprotect.spigot.impl.AddonManagerImpl;
import net.experience.powered.staffprotect.spigot.impl.SenderImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.experience.powered.staffprotect.spigot.StaffProtectPlugin.getInstance;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class AddonSub extends Subcommand {

    @Override
    public boolean command(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        final JavaPlugin javaPlugin = getInstance();
        final Player player = (Player) sender;
        if (args.length < 2) {
            final Component component = Component.text("Missing addon name.", RED);
            SenderImpl.getInstance(player).sendMessage(MiniMessage.miniMessage().deserializeOr(javaPlugin.getConfig().getString("commands.addon.missing-name"), component));
            return false;
        }
        final AddonManagerImpl impl = (AddonManagerImpl) StaffProtect.getInstance().getAddonManager();
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
