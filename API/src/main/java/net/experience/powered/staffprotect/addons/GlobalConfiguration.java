package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtect;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Wrapper class for {@link FileConfiguration}
 */
public class GlobalConfiguration extends YamlConfiguration {

    private static GlobalConfiguration instance;

    public static final String configVersion = "1.0";
    public static final File addonsFolder;
    public static final File path;

    static {
        JavaPlugin plugin = StaffProtect.getInstance().getPlugin();
        addonsFolder = new File(plugin.getDataFolder() + File.separator + "addons" + File.separator);
        path = new File(addonsFolder, "global_config.yml");
    }

    public GlobalConfiguration() {
        if (!GlobalConfiguration.path.exists()) {
            saveConfig();
        }
        reloadConfig();
        if (instance == null) {
            GlobalConfiguration.instance = this;
        }
    }

    public void saveConfig() { // Copied from JavaPlugin
        // Has to be sync!
       try {
           save(GlobalConfiguration.path);
       }
       catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public void reloadConfig() { // Copied from JavaPlugin
        // Has to be sync!
        try {
            load(GlobalConfiguration.path);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDefaultConfig() {
        final List<String> header = List.of(
                "#############################################",
                "",
                "StaffProtect | Global Configuration",
                "",
                "This is a configuration that will have all",
                "settings from addons",
                "",
                "#############################################"
        );

        setDefaultString("config_version", configVersion); // Sets a config version
        options().setHeader(header);
        options().parseComments(true);

        StaffProtect.getInstance().getAddonManager().getAddons().forEach(addon -> {
            try (final InputStream configStream = addon.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (configStream == null) {
                    return;
                }
                final Configuration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(configStream, StandardCharsets.UTF_8));
                for (final String key : configuration.getKeys(true)) {
                    if (!configuration.isConfigurationSection(key)) {
                        setDefaultString(key, configuration.get(key));
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            saveConfig();
        });
        reloadConfig();
    }

    private void setDefaultString(final @NotNull String path, final Object value) {
        if (!contains(path)) {
            set(path, value);
        }
    }

    public static GlobalConfiguration getInstance() {
        if (instance == null) {
            new GlobalConfiguration();
        }
        return instance;
    }
}
