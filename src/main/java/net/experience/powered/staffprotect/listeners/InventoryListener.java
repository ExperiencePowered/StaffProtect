package net.experience.powered.staffprotect.listeners;

import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    private final StaffProtectAPI api;

    public InventoryListener(final @NotNull StaffProtectAPI api) {
        this.api = api;
    }

    @EventHandler
    public void InventoryTrack(final @NotNull InventoryClickEvent e) {
        final var notification = NotificationManager.getInstance(api.getNotificationBus());
        notification.sendMessage(MiniMessage.miniMessage().deserialize("<red>Hey this is test notification!"));
    }
}