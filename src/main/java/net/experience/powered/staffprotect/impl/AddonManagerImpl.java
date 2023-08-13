package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.addons.AbstractAddon;
import net.experience.powered.staffprotect.addons.AddonManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AddonManagerImpl implements AddonManager {

    private final StaffProtectAPI api;
    private final Set<AbstractAddon> addons;

    public AddonManagerImpl(final @NotNull StaffProtectAPI api) {
        this.api = api;
        this.addons = new HashSet<>();
    }

    public void enableAddons() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final File addonFolder = new File("plugins" + File.separator + "StaffProtect" + File.separator + "addons");
        if (!addonFolder.isDirectory()) {
            if (!addonFolder.mkdir()) {
                throw new RuntimeException("Could not create directory: " + addonFolder);
            }
        }

        final Plugin[] potential = pluginManager.loadPlugins(addonFolder);
        for (final Plugin plugin : potential) {
            if (plugin instanceof final AbstractAddon addon) {
                enable(addon);
            }
            else {
                pluginManager.disablePlugin(plugin);
            }
        }
    }

    public void disableAddons() {
        for (final AbstractAddon addon : addons) {
            disable(addon);
        }
    }

    public void setLoadingState(
            final @NotNull AbstractAddon addon,
            final @NotNull AbstractAddon.LoadingState state,
            final @Nullable Runnable runnable) {
        try {
            Field field = AbstractAddon.class.getDeclaredField("loadingState");
            field.setAccessible(true);
            field.set(addon, state);
            if (runnable != null) runnable.run();
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(final @NotNull AbstractAddon addon) {
        if (addons.add(addon)) {
            setLoadingState(addon, AbstractAddon.LoadingState.REGISTERED, addon::onRegister);
        }
    }

    @Override
    public void unregister(final @NotNull AbstractAddon addon) {
        if (addons.remove(addon)) {
            setLoadingState(addon, AbstractAddon.LoadingState.UNKNOWN, addon::onUnregister);
        }
    }

    @Override
    public void disable(final @NotNull AbstractAddon addon) {
        unregister(addon);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled(addon)) {
            setLoadingState(addon, AbstractAddon.LoadingState.UNKNOWN, () -> pluginManager.disablePlugin(addon));
        }
    }

    @Override
    public void enable(final @NotNull AbstractAddon addon) {
        register(addon);
        final PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled(addon)) {
            setLoadingState(addon, AbstractAddon.LoadingState.ENABLED, () -> {
                addon.setAPI(api);
                pluginManager.enablePlugin(addon);
            });
        }
    }

    @Override
    public @NotNull Set<AbstractAddon> getAddons() {
        return Collections.unmodifiableSet(addons);
    }
}
