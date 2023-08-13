package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtectAPI;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper class for {@link FileConfiguration}
 */
public class GlobalConfiguration extends YamlConfiguration {

    public static final File path = new File(StaffProtectAPI.getInstance().getPlugin().getDataFolder() + "addons" + File.separator, "global_config.yml");

    public GlobalConfiguration() {
        reloadConfig();
    }

    public void saveConfig() {
        if (!path.exists()) {
            CompletableFuture.runAsync(() -> {
                try {
                    save(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void reloadConfig() {
        CompletableFuture.runAsync(() -> {
            try {
                load(GlobalConfiguration.path);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }).thenRun(() -> {
            final StaffProtectAPI api = StaffProtectAPI.getInstance();
            for (final AbstractAddon addon : api.getAddonManager().getAddons()) {
                final InputStream configStream = addon.getResource("config.yml");
                if (configStream == null) {
                    return;
                }
                addDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(configStream, StandardCharsets.UTF_8)));
            }
        });
    }
}
