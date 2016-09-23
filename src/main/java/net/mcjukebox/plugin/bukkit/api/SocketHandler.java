package net.mcjukebox.plugin.bukkit.api;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import net.mcjukebox.plugin.bukkit.events.ClientConnectEvent;
import net.mcjukebox.plugin.bukkit.events.ClientDisconnectEvent;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocketHandler {

	private Socket server;
	private CommandSender senderTryingKey;
	private boolean noConnectionWarned = false;
	private long lastDripSent;

	public SocketHandler() {
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
		Emitter.Listener connectionFailedListener = new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				keyStatus(false, objects.length > 0 && objects[0] instanceof String ? (String) objects[0] : null);
			}
		};

		server.on(Socket.EVENT_ERROR, connectionFailedListener);
		server.on(Socket.EVENT_CONNECT_ERROR, connectionFailedListener);
		server.on(Socket.EVENT_CONNECT_TIMEOUT, connectionFailedListener);

		server.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				keyStatus(true, null);
			}
		});

		server.on("drop", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				long roundTripTime = System.currentTimeMillis() - lastDripSent;
				long serverTime = (long) objects[0];
				MCJukebox.getInstance().getTimeUtils().updateOffset(roundTripTime, serverTime);
			}
		});

		server.on("event/clientConnect", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject data = (JSONObject) objects[0];
				ClientConnectEvent event = new ClientConnectEvent(
						data.getString("username"), data.getLong("timestamp"));
				MCJukebox.getInstance().getServer().getPluginManager().callEvent(event);

				if(Bukkit.getPlayer(data.getString("username")) == null) return;
				Player player = Bukkit.getPlayer(data.getString("username"));
				MessageUtils.sendMessage(player, "event.clientConnect");

				ShowManager showManager = MCJukebox.getInstance().getShowManager();
				if(!showManager.inInShow(player.getUniqueId())) return;

				for(Show show : showManager.getShowsByPlayer(player.getUniqueId())) {
					if(show.getCurrentTrack() != null)
						JukeboxAPI.play(player, show.getCurrentTrack());
				}
			}
		});

		server.on("event/clientDisconnect", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject data = (JSONObject) objects[0];
				ClientDisconnectEvent event = new ClientDisconnectEvent(
						data.getString("username"), data.getLong("timestamp"));
				MCJukebox.getInstance().getServer().getPluginManager().callEvent(event);

				if(Bukkit.getPlayer(data.getString("username")) == null) return;
				MessageUtils.sendMessage(Bukkit.getPlayer(data.getString("username")), "event.clientDisconnect");
			}
		});

		server.on("data/lang", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject data = (JSONObject) (objects.length == 2 ? objects[1] : objects[0]);
				MCJukebox.getInstance().getLangManager().loadLang(data);
			}
		});

		server.on("data/token", new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				JSONObject data = (JSONObject) objects[0];
				Player linkFor = Bukkit.getPlayer(data.getString("username"));
				if(linkFor == null) return;
				MessageUtils.sendURL(linkFor, data.getString("token"));
			}
		});
	}

	private HashMap<String, List<JSONObject>> queue = new HashMap<String, List<JSONObject>>();

	public void emit(String channel, JSONObject params) {
		if(server == null || !server.connected()){
			MCJukebox.getInstance().getLogger().warning("Queuing request as no connection to MCJukebox is present.");

			List<JSONObject> toSend = queue.containsKey(channel) ? queue.get(channel) : new ArrayList<JSONObject>();
			toSend.add(params);
			queue.put(channel, toSend);
			return;
		};
		server.emit(channel, params);
	}

	public void disconnect() {
		if(server == null) return;
		server.close();
	}

	public void tryKey(CommandSender sender, String key) {
		senderTryingKey = sender;
		MCJukebox.getInstance().setAPIKey(key);
	}

	public void keyStatus(boolean success, String message) {
		String displayMessage;

		if(success) displayMessage = ChatColor.GREEN + "Key accepted and connection successful.";
		else if(message != null) displayMessage = ChatColor.RED + "Key rejected with message: " + message;
		else {
			if(noConnectionWarned) return;
			noConnectionWarned = true;
			displayMessage = ChatColor.RED + "Lost connection to MCJukebox, servers may be updating...";
		}

		//Empty queue
		if(success) {
			int ran = 0;
			for(String channel : queue.keySet()) {
				List<JSONObject> toSend = queue.get(channel);
				for(JSONObject params : toSend) {
					emit(channel, params);
					ran = ran + 1;
				}
			}
			queue.clear();

			if(ran != 0) {
				String queueMessage = ChatColor.GREEN + "Ran " + ran + " items from the queue.";
				if (senderTryingKey == null) MCJukebox.getInstance().getLogger().info(queueMessage);
				else senderTryingKey.sendMessage(queueMessage);
			}

			noConnectionWarned = false;

			lastDripSent = System.currentTimeMillis();
			server.emit("drip");
		}

		if(!success && message != null) new File(MCJukebox.getInstance().getDataFolder() + "/api.key").delete();

		if(senderTryingKey == null) MCJukebox.getInstance().getLogger().info(ChatColor.stripColor(displayMessage));
		else senderTryingKey.sendMessage(displayMessage);

		senderTryingKey = null;
	}

}
