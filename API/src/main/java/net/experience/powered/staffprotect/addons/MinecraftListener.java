package net.experience.powered.staffprotect.addons;

import net.experience.powered.staffprotect.StaffProtect;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class MinecraftListener implements Listener {

    private boolean enabled;

    private void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void enable() {
        setEnabled(true);
        Bukkit.getPluginManager().registerEvents(this, StaffProtect.getInstance().getPlugin());
    }

    public void disable() {
        setEnabled(false);
        HandlerList.unregisterAll(this);
    }
}
