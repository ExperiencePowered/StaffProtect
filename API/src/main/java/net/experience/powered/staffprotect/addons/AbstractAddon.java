package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtectAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Addons which are in addon folder
 * Every method like {@link AbstractAddon#onRegister()} are called after the loading state is set and every logic is run
 * Method from {@link JavaPlugin#onLoad()} is called first, then is called {@link AbstractAddon#onRegister()} and lastly {@link JavaPlugin#onEnable()}
 *
 */
public abstract class AbstractAddon extends JavaPlugin {

    private GlobalConfiguration globalConfig = null;
    private LoadingState loadingState = LoadingState.UNKNOWN; // Must be non-final
    private StaffProtectAPI api;

    /**
     * Gets a global config,
     * which is located in <b>plugins/StaffProtect/addons/</b> with name <b>global_config.yml</b><br>
     * This should be really used instead of normal configuration {@link JavaPlugin} is having as that move
     * would make next folder and then it could be messy <br>
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
     * Sets API
     * @param api new api
     * @throws RuntimeException thrown when trying to set api while not being enabled, this should happen only when someone else than manager is interacting with this
     */
    @ApiStatus.Internal
    public void setAPI(final @NotNull StaffProtectAPI api) throws RuntimeException {
        if (loadingState != LoadingState.ENABLED) {
            throw new RuntimeException("Tried to set API while not being enabled.");
        }
        if (this.api == null) {
            this.api = api;
        }
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

    /**
     * Called when addon is registered
     */
    public void onRegister() {
    }

    /**
     * Called when addon is unregistered
     */
    public void onUnregister() {
    }

    /**
     * Gets loading state of this class
     * @return loading state
     */
    public LoadingState getLoadingState() {
        return loadingState;
    }

    @Override
    public void saveDefaultConfig() {
        saveConfig();
    }

    @Override
    public void saveConfig() {
        globalConfig.saveConfig();
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return getGlobalConfig();
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
