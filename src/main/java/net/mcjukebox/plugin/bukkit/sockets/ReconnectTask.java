package net.mcjukebox.plugin.bukkit.sockets;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Random;

public class ReconnectTask implements Runnable {

	private SocketHandler socketHandler;
	private long lastReconnectionAttempt = 0;
	private int reconnectionFailures = 0;
	@Setter private boolean reconnecting = true;

	public ReconnectTask(SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}

	@Override
	public void run() {
		if(reconnecting || socketHandler.getServer().connected()) return;
		long timeSinceRun = System.currentTimeMillis() - lastReconnectionAttempt;
		if(timeSinceRun < getCurrentReconnectionDelay(reconnectionFailures)) return;

		//Add some randomness to reconnect interval to prevent server overload
		if(new Random().nextInt(15) != 7) return;

		reconnecting = true;
		reconnectionFailures = reconnectionFailures + 1;
		lastReconnectionAttempt = System.currentTimeMillis();

		socketHandler.attemptConnection();
		Bukkit.getConsoleSender().sendMessage(ChatColor.BOLD + "I AM GIVING RECONNECTING AGO OK?");
	}

	public void reset() {
		lastReconnectionAttempt = 0;
		reconnectionFailures = 0;
		reconnecting = false;
	}

	private int getCurrentReconnectionDelay(int reconnectionFailures) {
		if(reconnectionFailures <= 3) return 3 * 1000;
		if(reconnectionFailures <= 6) return 15 * 1000;
		if(reconnectionFailures <= 8) return 30 * 1000;
		return 2 * 60 * 1000;
	}

}
