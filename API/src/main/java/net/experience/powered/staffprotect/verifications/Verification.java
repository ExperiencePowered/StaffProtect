package net.experience.powered.staffprotect.verifications;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Verification {

    private static Verification instance;

    public Verification() {
        Verification.instance = this;
    }

    public abstract void start(final @NotNull Player player);
    public abstract void end(final @NotNull Player player);
    public abstract boolean authorize(final @NotNull Player player, int code);
    public abstract void forceAuthorize(final @NotNull Player player);
    public abstract boolean isAuthorized(final @NotNull Player player);

    public static Verification getInstance() {
        return instance;
    }
}
