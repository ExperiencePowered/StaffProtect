package net.experience.powered.staffprotect.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPreVerifyEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private String code; // Code is here, this is string before parsing
    private boolean cancelled;

    public PlayerPreVerifyEvent(final Player player, final String code) {
        super(false); // Not async
        this.player = player;
        this.code = code;
    }

    /**
     * Gets code which would be used for verify, Integer it becomes in {@link PlayerVerifyEvent}
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     *  Sets code which would be used for verify
      * @param code code which will be also parsed
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets player involved in this player
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
