package net.experience.powered.staffprotect.spigot.impl;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.notification.Sender;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public Sender player(final @NotNull Player player) {
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
    public void sendMessage(final @NotNull Component component) {
        final BukkitAudiences audience = BukkitAudiences.create(api.getPlugin());
        uuids.forEach(uuid -> audience.player(uuid).sendMessage(component));
    }

    @Override
    public void sendMessage(final @NotNull String string) {
        final BukkitAudiences audience = BukkitAudiences.create(api.getPlugin());
        uuids.forEach(uuid -> audience.player(uuid).sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(string)));
    }

    @Contract("_ -> new")
    public static @NotNull Sender getInstance(final @NotNull Player player) {
        return new SenderImpl(StaffProtect.getInstance(), List.of(player.getUniqueId()));
    }
}
