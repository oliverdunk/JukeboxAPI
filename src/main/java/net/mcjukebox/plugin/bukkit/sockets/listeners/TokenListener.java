package net.mcjukebox.plugin.bukkit.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class TokenListener implements Emitter.Listener {

	@Override
	public void call(Object... objects) {
		JSONObject data = (JSONObject) objects[0];
		Player linkFor = Bukkit.getPlayer(data.getString("username"));
		if(linkFor == null) return;
		MessageUtils.sendURL(linkFor, data.getString("token"));
	}
}
