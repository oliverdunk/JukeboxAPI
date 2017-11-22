package net.mcjukebox.plugin.bukkit.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.events.ClientConnectEvent;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class ClientConnectListener implements Emitter.Listener {

    @Override
    public void call(Object... objects) {
        JSONObject data = (JSONObject) objects[0];
        ClientConnectEvent event = new ClientConnectEvent(
                data.getString("username"), data.getLong("timestamp"));
        MCJukebox.getInstance().getServer().getPluginManager().callEvent(event);

        if(Bukkit.getPlayer(data.getString("username")) == null) return;
        Player player = Bukkit.getPlayer(data.getString("username"));
        MessageUtils.sendMessage(player, "event.clientConnect");

        MCJukebox.getInstance().getSocketHandler().getConnectedPlayers().add(Bukkit.getPlayer(data.getString("username")));

        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        if(!showManager.inInShow(player.getUniqueId())) return;

        for(Show show : showManager.getShowsByPlayer(player.getUniqueId())) {
            if(show.getCurrentTrack() != null)
                JukeboxAPI.play(player, show.getCurrentTrack());
        }
    }

}
