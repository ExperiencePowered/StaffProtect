package net.experience.powered.staffprotect.waterfall;

import net.experience.powered.staffprotect.waterfall.listeners.PlayerListener;
import net.experience.powered.staffprotect.waterfall.messages.PluginMessageManager;
import net.experience.powered.staffprotect.waterfall.utils.Metrics;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

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

        new Metrics(this, 19766);
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
