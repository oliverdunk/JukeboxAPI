package net.mcjukebox.plugin.bukkit.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import org.json.JSONObject;

public class LangListener implements Emitter.Listener {

    @Override
    public void call(Object... objects) {
        JSONObject data = (JSONObject) (objects.length == 2 ? objects[1] : objects[0]);
        MCJukebox.getInstance().getLangManager().loadLang(data);
    }

}
