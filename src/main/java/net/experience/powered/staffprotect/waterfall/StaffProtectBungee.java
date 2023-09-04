package net.experience.powered.staffprotect.waterfall;

import net.experience.powered.staffprotect.waterfall.configuration.ProxyConfiguration;
import net.experience.powered.staffprotect.waterfall.listeners.PlayerListener;
import net.experience.powered.staffprotect.waterfall.messages.PluginMessageManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class StaffProtectBungee extends Plugin {

    private static StaffProtectBungee instance;

    private HashMap<UUID, Boolean> authorized;
    private PluginMessageManager messageManager;

    @Override
    public void onEnable() {
        StaffProtectBungee.instance = this;
        getProxy().registerChannel("staffprotect:bungee");
        getProxy().registerChannel("staffprotect:spigot");
        messageManager = new PluginMessageManager();
        authorized = new HashMap<>();

        getProxy().getPluginManager().registerListener(this, messageManager);
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    public HashMap<UUID, Boolean> getAuthorized() {
        return authorized;
    }

    public PluginMessageManager getMessageManager() {
        return messageManager;
    }

    public static StaffProtectBungee getInstance() {
        return instance;
    }
}
