package net.experience.powered.staffprotect.listeners;

import net.experience.powered.staffprotect.StaffProtect;
import net.experience.powered.staffprotect.StaffProtectPlugin;
import net.experience.powered.staffprotect.notification.NotificationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    private final StaffProtect api;
    private final StaffProtectPlugin plugin;

    public InventoryListener(final @NotNull StaffProtect api) {
        this.api = api;
        this.plugin = StaffProtectPlugin.getPlugin(StaffProtectPlugin.class);
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

        ItemStack itemStack = e.getCurrentItem();
        if (itemStack.getType() == Material.AIR)
            if (e.getClickedInventory() != null)
                itemStack = e.getClickedInventory().getItem(e.getSlot());
        if (itemStack == null) return;

        final Configuration configuration = plugin.getConfig();
        final String replacement = configuration.getString("notification.creative-tracking.replacements." + itemStack.getType().name());

        String item;
        if (replacement != null) {
            item = replacement.replace("<amount>", "" + itemStack.getAmount());
        }
        else {
            item = itemStack.getAmount() + "x " + "<lang:block.minecraft." + itemStack.getType().name().toLowerCase() + ">";
        }

        final String fItem = item;
        final String string = configuration.getString("notification.creative-tracking.message", "String not found.");
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        final Component component = miniMessage.deserialize(string, Placeholder.parsed("player", player.getName()), Placeholder.parsed("item", fItem));
        NotificationManager.getInstance().sendMessage(player.getName(), component);
    }
}