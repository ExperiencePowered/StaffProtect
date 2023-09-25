package net.experience.powered.staffprotect.records;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ActionType {

    COMMAND_USE,
    PLUGIN,
    SERVER_STATE,
    INVENTORY,
    AUTHORIZATION_STATE,
    PLAYER_CONNECTION;

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return name();
    }
}
