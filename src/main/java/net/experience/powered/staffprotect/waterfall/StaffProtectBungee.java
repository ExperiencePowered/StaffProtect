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
        messageManager = new PluginMessageManager();
        authorized = new HashMap<>();

        getProxy().getPluginManager().registerListener(this, messageManager);
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    public void saveResource(@NotNull String resourcePath, final boolean replace) {
        Objects.requireNonNull(resourcePath, "ResourcePath cannot be null");
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be empty");
        }
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
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
