package net.experience.powered.staffprotect.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerVerifyEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final int code;
    private final boolean authorized;

    public PlayerVerifyEvent(final Player player, final int code, final boolean authorized) {
        super(false); // Not async
        this.player = player;
        this.code = code;
        this.authorized = authorized;
    }

    /**
     * Gets code, if -1 was returned, the parsed code was invalid
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets whether player was authorized or not, true means he was authorized, false if he was not
     * @return state
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * Gets player involved in this player
     * @return player
     */
    public Player getPlayer() {
        return player;
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
