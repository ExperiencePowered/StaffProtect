package net.experience.powered.staffprotect.waterfall.impl;

import net.experience.powered.staffprotect.notification.Sender;
import net.experience.powered.staffprotect.waterfall.StaffProtectBungee;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SenderImpl {

    private final List<UUID> uuids;

    public SenderImpl() {
        this.uuids = new ArrayList<>();
    }

    public SenderImpl(final @NotNull List<UUID> uuids) {
        this.uuids = uuids;
    }

    public SenderImpl player(final @NotNull ProxiedPlayer player) {
        final SenderImpl impl = new SenderImpl();
        impl.uuids.add(player.getUniqueId());
        return impl;
    }

    public void sendMessage(final @NotNull Component component) {
        try (BungeeAudiences audience = BungeeAudiences.create(StaffProtectBungee.getInstance())) {
            uuids.forEach(uuid -> audience.player(uuid).sendMessage(component));
        }
    }

    @Contract("_ -> new")
    public static @NotNull SenderImpl getInstance(final @NotNull ProxiedPlayer player) {
        return new SenderImpl(List.of(player.getUniqueId()));
    }
}
