package net.experience.powered.staffprotect.notification;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface Subscriber {

    @NotNull UUID getUniqueId();
    @NotNull List<UUID> getIgnoredPlayers();

}
