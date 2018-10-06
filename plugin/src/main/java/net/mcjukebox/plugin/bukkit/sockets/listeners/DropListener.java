package net.mcjukebox.plugin.bukkit.sockets.listeners;

import io.socket.emitter.Emitter;
import lombok.Setter;
import net.mcjukebox.plugin.bukkit.MCJukebox;

public class DropListener implements Emitter.Listener {

	@Setter private long lastDripSent;

	@Override
	public void call(Object... objects) {
		long roundTripTime = System.currentTimeMillis() - lastDripSent;
		long serverTime = (long) objects[0];
		MCJukebox.getInstance().getTimeUtils().updateOffset(roundTripTime, serverTime);
	}

}
