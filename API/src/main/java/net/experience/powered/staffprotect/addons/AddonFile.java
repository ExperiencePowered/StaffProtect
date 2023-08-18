package net.experience.powered.staffprotect.addons;

import org.jetbrains.annotations.NotNull;

/**
 * Class representing values in addon.yml
 */
public interface AddonFile {

    /**
     * Represents string with path 'main'
     * @return main class string
     */
    @NotNull String mainClass();

    /**
     * Represents string with path 'name'
     * @return plugin name string
     */
    @NotNull String pluginName();

    /**
     * Represents string with path 'version'
     * @return plugin version string
     */
    @NotNull String pluginVersion();

    /**
     * Represents string with path 'author'
     * @return author string
     */
    @NotNull String author();
}
