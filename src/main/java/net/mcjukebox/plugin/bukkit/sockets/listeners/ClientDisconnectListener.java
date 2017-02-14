package net.mcjukebox.plugin.bukkit.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.events.ClientDisconnectEvent;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

public class ClientDisconnectListener implements Emitter.Listener {

    @Override
    public void call(Object... objects) {
        JSONObject data = (JSONObject) objects[0];
        ClientDisconnectEvent event = new ClientDisconnectEvent(
                data.getString("username"), data.getLong("timestamp"));
        MCJukebox.getInstance().getServer().getPluginManager().callEvent(event);

        if(Bukkit.getPlayer(data.getString("username")) == null) return;
        MessageUtils.sendMessage(Bukkit.getPlayer(data.getString("username")), "event.clientDisconnect");
    }

}
