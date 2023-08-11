package net.experience.powered.staffprotect.listeners;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.StaffProtectAPI;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    private final StaffProtectAPI api;
    private final StaffProtect plugin;

    public InventoryListener(final @NotNull StaffProtectAPI api) {
        this.api = api;
        this.plugin = StaffProtect.getPlugin(StaffProtect.class);
    }

    @EventHandler
    public void InventoryTrack(final @NotNull InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        if (!player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (e.getCurrentItem() == null) return;

        // TO DO: Add multiple messages as there are multiple actions
        //switch (e.getAction()) {
        //
        //}

        var itemStack = e.getCurrentItem();
        if (itemStack.getType() == Material.AIR)
            if (e.getClickedInventory() != null)
                itemStack = e.getClickedInventory().getItem(e.getSlot());
        if (itemStack == null) return;

        var item = "";

        final var configuration = plugin.getConfig();
        final var replacement = configuration.getString("notification.creative-tracking.replacements." + itemStack.getType().name());
        if (replacement != null) {
            item = replacement.replace("<amount>", "" + itemStack.getAmount());
        }
        else {
            item = itemStack.getAmount() + "x " + "<lang:block.minecraft." + itemStack.getType().name().toLowerCase() + ">";
        }

        final var fItem = item;
        final var string = configuration.getString("notification.creative-tracking.message", "String not found.");
        final var miniMessage = MiniMessage.miniMessage();
        final var component = miniMessage.deserialize(string, Placeholder.parsed("player", player.getName()), Placeholder.parsed("item", fItem));
        final var notificationManager = NotificationManager.getInstance(api.getNotificationBus());

        notificationManager.sendMessage(component);
    }
}