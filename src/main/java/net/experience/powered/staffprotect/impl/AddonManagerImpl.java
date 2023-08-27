package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.addons.AbstractAddon;
import net.experience.powered.staffprotect.addons.AddonFile;
import net.experience.powered.staffprotect.addons.AddonManager;
import net.experience.powered.staffprotect.addons.GlobalConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

public class AddonManagerImpl implements AddonManager {

    private final Logger logger;
    private final StaffProtectAPI api;
    private final HashMap<AbstractAddon, URLClassLoader> addons;
    private GlobalConfiguration globalConfiguration;

    public AddonManagerImpl(final @NotNull StaffProtectAPI api) {
        this.api = api;
        this.logger = api.getPlugin().getLogger();
        this.addons = new HashMap<>();
    }

    public void enableAddons() {
        final File addonFolder = GlobalConfiguration.addonsFolder;
        if (!addonFolder.isDirectory()) {
            boolean ignore = addonFolder.mkdirs();
        }

        Arrays.stream(Objects.requireNonNull(addonFolder.listFiles())).filter(File::isFile).forEach(file -> {
            try {
                if (extractExtension(file).equals("jar")) {
                    final AbstractAddon addon = load(file);
                    addons.put(addon, addon.getClassLoader());
                    register(addon);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        globalConfiguration = new GlobalConfiguration();
        globalConfiguration.saveDefaultConfig();
        addons.forEach((addon, classLoader) -> enable(addon)); // Enables addons
    }

    public void disableAddons() {
        for (final Map.Entry<AbstractAddon, URLClassLoader> entry : addons.entrySet()) {
            try {
                final AbstractAddon addon = entry.getKey();
                unregister(addon);
                disable(addon);
                unload(addon);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String extractExtension(final @NotNull File file) {
        final String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index+1);
        }
        return "unknown";
    }

    @Override
    public AbstractAddon load(final @NotNull File file) throws Exception {
        if (!extractExtension(file).equals("jar")) {
            return null;
        }
        final URL url = file.toURI().toURL();
        final URL[] urlArray = new URL[]{url};
        final ClassLoader parentClassLoader = api.getPlugin().getClass().getClassLoader();

        final Class<?> clazz;
        final AddonFile addonFile;
        final URLClassLoader classLoader = new URLClassLoader(urlArray, parentClassLoader);

        try (final InputStream inputStream = classLoader.getResourceAsStream("addon.yml")) {
            if (inputStream == null) {
                throw new IllegalStateException("Couldn't find addon.yml for constructor " + file.getName());
            }
            final YamlConfiguration addonYml = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            final String mainClass = addonYml.getString("main");
            String name = addonYml.getString("name");
            String version = addonYml.getString("version");
            String author = addonYml.getString("author");

            if (mainClass == null) {
                throw new IllegalStateException("Addon " + file.getName() + " does not have main class in addon.yml.");
            }

            if (name == null) {
                logger.warning("Addon " + file.getName() + " does not have name in addon.yml, we will use file's name.");
                name = file.getName();
            }

            if (version == null) {
                logger.warning("Addon " + name + " does not have version, we will use '1.0-SNAPSHOT'.");
                version = "1.0-SNAPSHOT";
            }

            if (author == null) {
                logger.warning("Addon " + name + " does not have author, we will use 'Anonymous'.");
                logger.warning("Although we do not recommend using addons without author name.");
                author = "Anonymous";
            }
            addonFile = AddonFileImpl.getInstance(mainClass, name, version, author);
            clazz = classLoader.loadClass(addonFile.mainClass());
        }
        if (!AbstractAddon.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Tried to initiate addon " + addonFile.pluginName() + " which is not extending AbstractAddon.");
        }

        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        final AbstractAddon addon = (AbstractAddon) constructor.newInstance();
        final Method method = AbstractAddon.class.getDeclaredMethod("init", StaffProtectAPI.class, AddonFile.class, AbstractAddon.LoadingState.class, URLClassLoader.class);
        method.setAccessible(true);
        method.invoke(addon, api, addonFile, AbstractAddon.LoadingState.UNKNOWN, classLoader);

        this.register(addon);
        addon.onLoad();
        logger.info("Loaded addon " + addon + " v" + addon.getAddonFile().pluginVersion());
        return addon;
    }

    @Override
    public void unload(final @NotNull AbstractAddon addon) throws IOException {
        final URLClassLoader classLoader = addons.get(addon);
        classLoader.close();
        logger.info("Unloaded addon " + addon + " v" + addon.getAddonFile().pluginVersion());
    }

    @Override
    public void register(final @NotNull AbstractAddon addon) {
        if (!addons.containsKey(addon)) {
            addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.REGISTERED);
            logger.info("Registered addon " + addon + " v" + addon.getAddonFile().pluginVersion());
        }
    }

    @Override
    public void unregister(final @NotNull AbstractAddon addon) {
        if (addons.containsKey(addon)) {
            addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.UNKNOWN);
            logger.info("Unregistered addon " + addon + " v" + addon.getAddonFile().pluginVersion());
        }
    }

    @Override
    public void disable(final @NotNull AbstractAddon addon) throws IOException {
        addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.UNKNOWN);
        addon.onDisable();
        logger.info("Disabled addon " + addon + " v" + addon.getAddonFile().pluginVersion());
    }

    @Override
    public void enable(final @NotNull AbstractAddon addon) {
        addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.ENABLED);
        addon.onEnable();
        logger.info("Enabled addon " + addon + " v" + addon.getAddonFile().pluginVersion());
    }

    @Override
    public @NotNull Set<AbstractAddon> getAddons() {
        return Collections.unmodifiableSet(addons.keySet());
    }
}
