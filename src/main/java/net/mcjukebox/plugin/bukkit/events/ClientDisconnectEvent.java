package net.mcjukebox.plugin.bukkit.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player disconnects from the audio server
 */
public class ClientDisconnectEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Username of player who has connected
     */
    @Getter private final String username;
    /**
     * Timestamp of when they connected
     */
    @Getter private final long timestamp;

    public ClientDisconnectEvent(String username, long timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
