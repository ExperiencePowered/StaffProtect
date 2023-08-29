package net.experience.powered.staffprotect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class StaffProtectProvider {

    private static StaffProtect instance = null;

    public StaffProtectProvider(final @NotNull StaffProtect instance) {
        StaffProtectProvider.instance = instance;
    }

    /**
     * Returns static instance of StaffProtect class
     * @return static instance of staffprotect class
     */
    @Contract(value = " -> new", pure = true)
    public static @NotNull Optional<StaffProtect> getInstance() {
        return Optional.of(instance);
    }
}
