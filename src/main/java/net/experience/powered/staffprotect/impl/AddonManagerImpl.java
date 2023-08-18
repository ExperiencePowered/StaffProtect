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
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class AddonManagerImpl implements AddonManager {

    private final StaffProtectAPI api;
    private final HashMap<AbstractAddon, URLClassLoader> addons;
    private GlobalConfiguration globalConfiguration;

    public AddonManagerImpl(final @NotNull StaffProtectAPI api) {
        this.api = api;
        this.addons = new HashMap<>();
    }

    public void enableAddons() {
        final File addonFolder = GlobalConfiguration.addonsFolder;
        if (!addonFolder.isDirectory()) {
            boolean ignore = addonFolder.mkdirs();
        }

        Arrays.stream(Objects.requireNonNull(addonFolder.listFiles())).filter(File::isFile).forEach(file -> {
            try {
                final AbstractAddon addon = load(file);
                addons.put(addon, (URLClassLoader) addon.getClass().getClassLoader());
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
                disable(entry.getKey());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public AbstractAddon load(final @NotNull File file) throws Exception {
        final var url = file.toURI().toURL();
        final var urlArray = new URL[]{url};
        final var parentClassLoader = api.getPlugin().getClass().getClassLoader();

        final Class<?> clazz;
        final AddonFile addonFile;

        try (URLClassLoader classLoader = new URLClassLoader(urlArray, parentClassLoader);
             final var inputStream = classLoader.getResourceAsStream("addon.yml")) {
            if (inputStream == null) {
                throw new IllegalStateException("Couldn't find addon.yml for constructor " + file.getName());
            }
            final var addonYml = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            final var mainClass = addonYml.getString("main");
            var name = addonYml.getString("name");
            var version = addonYml.getString("version");
            var author = addonYml.getString("author");

            final var logger = api.getPlugin().getLogger();
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

            clazz = classLoader.loadClass(mainClass);
            if (!AbstractAddon.class.isAssignableFrom(clazz)) {
                throw new IllegalStateException("Tried to initiate addon " + name + " which is not extending AbstractAddon.");
            }
            inputStream.close();

            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            final AbstractAddon addon = (AbstractAddon) constructor.newInstance();
            addon.init(api, AbstractAddon.LoadingState.UNKNOWN, addonFile);
            register(addon);
            addon.onLoad();
            return addon;
        }
        catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload(final @NotNull AbstractAddon addon) throws IOException {
        final URLClassLoader classLoader = addons.get(addon);
        classLoader.close();
    }

    @Override
    public void register(final @NotNull AbstractAddon addon) {
        if (!addons.containsKey(addon)) {
            addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.REGISTERED);
        }
    }

    @Override
    public void unregister(final @NotNull AbstractAddon addon) {
        if (addons.containsKey(addon)) {
            addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.UNKNOWN);
        }
    }

    @Override
    public void disable(final @NotNull AbstractAddon addon) throws IOException {
        unregister(addon);
        addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.UNKNOWN);
        addon.onDisable();
        unload(addon);
    }

    @Override
    public void enable(final @NotNull AbstractAddon addon) {
        if (!addons.containsKey(addon)) {
            /* Addon should always be registered before enabling,
            this may happen only as a result by playing with reflections */
            throw new IllegalStateException("Tried to enable addon while not being registered.");
        }
        addon.setLoadingState(AddonManagerImpl.class, AbstractAddon.LoadingState.ENABLED);
        addon.onEnable();
    }

    @Override
    public @NotNull Set<AbstractAddon> getAddons() {
        return Collections.unmodifiableSet(addons.keySet());
    }
}
