package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLClassLoader;

/**
 * Addon which is in addon folder
 *
 */
public abstract class AbstractAddon {

    private URLClassLoader classLoader;
    private StaffProtectAPI api;
    private AddonFile addonFile;
    private GlobalConfiguration globalConfig;
    private LoadingState loadingState;

    public AbstractAddon() {
    }

    private void init(final @NotNull StaffProtectAPI api,
                            final @NotNull AddonFile addonFile,
                            final @Nullable LoadingState loadingState,
                            final @NotNull URLClassLoader classLoader) {
        this.loadingState = loadingState == null ? AbstractAddon.LoadingState.UNKNOWN : loadingState;
        this.api = api;
        this.globalConfig = new GlobalConfiguration();
        this.addonFile = addonFile;
        this.classLoader = classLoader;
    }

    public void registerListener(final @NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, api.getPlugin());
    }

    /**
     * Sets a loading state
     * @param loadingState loading state
     */
    public void setLoadingState(final @NotNull Class<?> access, final @NotNull LoadingState loadingState) {
        if (!access.getClassLoader().equals(api.getPlugin().getClass().getClassLoader())) {
            throw new IllegalStateException("Trying to set loading state with different class loader.");
        }
        this.loadingState = loadingState;
    }

    /**
     * Gets a class loader
     * @return class loader
     */
    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Gets an addon file
     * @return addon file
     */
    public AddonFile getAddonFile() {
        return addonFile;
    }

    /**
     * Gets a global config,
     * which is located in <b>plugins/StaffProtect/addons/</b> with name <b>global_config.yml</b><br>
     * <br>
     * Although to add messages to global config,
     * you can also use the simpler way and create config.yml in your plugin,
     * and it will be automatically added when config will be reloaded or server will be restarted
     * @return global configuration
     */
    public GlobalConfiguration getGlobalConfig() {
        if (!loadingState.equals(LoadingState.ENABLED)) {
            throw new RuntimeException("Cannot access global configuration until plugin is fully enabled.");
        }
        if (globalConfig == null) {
            globalConfig = new GlobalConfiguration();
        }
        return globalConfig;
    }

    /**
     * Gets API
     * @return api
     */
    public StaffProtectAPI getAPI() {
        return api;
    }

    /**
     * Gets whether plugin should be shown in a list of addons <br>
     * It is good to return false in case  <br>
     * If you want to change value, override it
     * @return should be shown as addon
     */
    public boolean showAsAddon() {
        return true;
    }

    public void onLoad() {
    }

    public void onUnload() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    /**
     * Gets loading state of this class
     * @return loading state
     */
    public LoadingState getLoadingState() {
        return loadingState;
    }

    @Override
    public final @NotNull String toString() {
        return addonFile.pluginName();
    }

    public enum LoadingState {
        /**
         * When addon is registered
         */
        REGISTERED,
        /**
         * When addon is enabled
         */
        ENABLED,
        /**
         * When addon is unregistered, disabled or none of those two
         */
        UNKNOWN
    }
}
