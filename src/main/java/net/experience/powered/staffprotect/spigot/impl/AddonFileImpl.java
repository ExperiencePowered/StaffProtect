package net.experience.powered.staffprotect.spigot.impl;

import net.experience.powered.staffprotect.addons.AddonFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record AddonFileImpl(String mainClass,
                            String pluginName,
                            String pluginVersion,
                            String author) implements AddonFile {

    public AddonFileImpl(final @NotNull String mainClass,
                         final @NotNull String pluginName,
                         final @NotNull String pluginVersion,
                         final @NotNull String author) {
        this.mainClass = mainClass;
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.author = author;
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull AddonFile getInstance(final @NotNull String mainClass,
                                                 final @NotNull String pluginName,
                                                 final @NotNull String pluginVersion,
                                                 final @NotNull String author) {
        return new AddonFileImpl(mainClass, pluginName, pluginVersion, author);
    }
}
