package net.mcjukebox.plugin.bukkit.sockets;

import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.sockets.listeners.*;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import javax.annotation.Nullable;

public class SocketHandler {

	@Getter private Socket server;
	@Getter private ReconnectTask reconnectTask;
	@Getter private DropListener dropListener = new DropListener();
	@Getter private KeyHandler keyHandler = new KeyHandler(this);
	@Getter private ConnectionListener connectionListener = new ConnectionListener(this);

	public SocketHandler() {
		reconnectTask = new ReconnectTask(this);
		Bukkit.getScheduler().runTaskTimerAsynchronously(MCJukebox.getInstance(), reconnectTask, 0, 1 * 20);
		attemptConnection();
	}

	public void attemptConnection() {
		try {
			if(MCJukebox.getInstance().getAPIKey() == null) {
				MCJukebox.getInstance().getLogger().warning("No API key set - ignoring attempt to connect.");
				return;
			}

			IO.Options opts = new IO.Options();
			opts.secure = true;
			opts.reconnection = false;
			opts.query = "APIKey=" + MCJukebox.getInstance().getAPIKey();

			String url = "https://secure.ws.mcjukebox.net";

			server = IO.socket(url, opts);
			server.connect();

			registerEventListeners();
		}catch(Exception ex){
			MCJukebox.getInstance().getLogger().warning("An unknown error occurred, disabling plugin...");
			ex.printStackTrace();
			//Disable plugin due to an error
			Bukkit.getPluginManager().disablePlugin(MCJukebox.getInstance());
		}
	}

	private void registerEventListeners() {
		//Connection issue listener
		server.on(Socket.EVENT_ERROR, connectionListener.getConnectionFailedListener());
		server.on(Socket.EVENT_CONNECT_ERROR, connectionListener.getConnectionFailedListener());
		server.on(Socket.EVENT_CONNECT_TIMEOUT, connectionListener.getConnectionFailedListener());
		server.on(Socket.EVENT_CONNECT, connectionListener.getConnectionSuccessListener());

		//Event and data listeners
		server.on("drop", dropListener);
		server.on("event/clientConnect", new ClientConnectListener());
		server.on("event/clientDisconnect", new ClientDisconnectListener());
		server.on("data/lang", new LangListener());
		server.on("data/token", new TokenListener());
	}

	public void emit(String channel, @Nullable JSONObject params) {
		if(server == null || !server.connected()) connectionListener.addToQueue(channel, params);
		else server.emit(channel, params);
	}

	public void disconnect() {
		if(server == null || !server.connected()) return;
		server.close();
	}

}
