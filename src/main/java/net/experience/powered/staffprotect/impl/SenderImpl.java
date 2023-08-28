package net.experience.powered.staffprotect.impl;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.notification.Sender;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SenderImpl extends Sender {

    private final StaffProtect api;
    private final List<UUID> uuids;

    public SenderImpl(final @NotNull StaffProtect api) {
        this.uuids = new ArrayList<>();
        this.api = api;
    }

    public SenderImpl(final @NotNull StaffProtect api, final @NotNull List<UUID> uuids) {
        this.uuids = uuids;
        this.api = api;
    }

    @Override
    public Sender player(@NotNull Player player) {
        final SenderImpl impl = new SenderImpl(api);
        impl.uuids.add(player.getUniqueId());
        return impl;
    }

    @Override
    public Sender all() {
        final SenderImpl impl = new SenderImpl(api);
        impl.uuids.addAll(api.getNotificationBus().getSubscribers());
        return impl;
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        final BukkitAudiences audience = BukkitAudiences.create(api.getPlugin());
        uuids.forEach(uuid -> {
            // Get player async
            final CompletableFuture<Player> future = CompletableFuture.supplyAsync(() -> Bukkit.getPlayer(uuid));
            future.thenAcceptAsync(player -> {
                if (player == null) {
                    throw new IllegalStateException("Player is offline, but is still subscribing to notifications");
                }
                audience.player(uuid).sendMessage(component);
            });
        });
    }

    @Override
    public void sendMessage(@NotNull String string) {
        final BukkitAudiences audience = BukkitAudiences.create(api.getPlugin());
        uuids.forEach(uuid -> {
            // Get player async
            final CompletableFuture<Player> future = CompletableFuture.supplyAsync(() -> Bukkit.getPlayer(uuid));
            future.thenAcceptAsync(player -> {
                if (player == null) {
                    throw new IllegalStateException("Player is offline, but is still subscribing to notifications");
                }
                audience.player(uuid).sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(string));
            });
        });
    }
}
