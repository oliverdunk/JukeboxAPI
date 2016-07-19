package net.mcjukebox.plugin.bukkit.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClientConnectEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	@Getter private String username;
	@Getter private long timestamp;

	public ClientConnectEvent(String username, long timestamp) {
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
