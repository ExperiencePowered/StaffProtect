package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtectAPI;
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

    private LoadingState loadingState = LoadingState.UNKNOWN; // Must be non-final
    private StaffProtectAPI api;

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
