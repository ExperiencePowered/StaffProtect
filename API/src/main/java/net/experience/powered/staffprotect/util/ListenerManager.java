package net.experience.powered.staffprotect.util;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface ListenerManager {

    void registerListener(final @NotNull Listener listener);

}
