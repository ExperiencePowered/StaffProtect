package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.notification.Subscriber;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriberImpl implements Subscriber {

    private final UUID uuid;
    private final List<UUID> ignoredPlayers;

    public SubscriberImpl(final @NotNull UUID uuid) {
        this.uuid = uuid;
        this.ignoredPlayers = new ArrayList<>();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }
}
