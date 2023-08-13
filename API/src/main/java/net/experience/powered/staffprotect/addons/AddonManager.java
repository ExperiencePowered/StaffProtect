package net.experience.powered.staffprotect.addons;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface AddonManager {

    /**
     * Register an addon
     * @param addon addon to register
     */
    void register(final @NotNull AbstractAddon addon);
    
    /**
     * Unregister an addon
     * @param addon addon to unregister
     */
    void unregister(final @NotNull AbstractAddon addon);

    /**
     * Disables an addon
     * @param addon addon to disable
     */
    void disable(final @NotNull AbstractAddon addon);

    /**
     * Enables an addon
     * @param addon addon to enable
     */
    void enable(final @NotNull AbstractAddon addon);

    /**
     * Gets a list of addons
     * @return addons
     */
    @NotNull Set<AbstractAddon> getAddons();
}
