package net.mcjukebox.plugin.bukkit.sockets.listeners;

import io.socket.emitter.Emitter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.sockets.SocketHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionListener {

    @Getter private ConnectionFailedListener connectionFailedListener;
    @Getter private ConnectionSuccessListener connectionSuccessListener;

    private SocketHandler socketHandler;
    private HashMap<String, List<JSONObject>> queue = new HashMap<String, List<JSONObject>>();
    private boolean noConnectionWarned = false;

    public ConnectionListener(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
        connectionFailedListener = new ConnectionFailedListener();
        connectionSuccessListener = new ConnectionSuccessListener(socketHandler.getDropListener());
    }

    public class ConnectionFailedListener implements Emitter.Listener {

        @Override
        public void call(Object... objects) {
            String reason = objects.length > 0 && objects[0] instanceof String ? (String) objects[0] : null;

            if(reason != null) {
                socketHandler.getKeyHandler().onKeyRejected(reason);
                return;
            }

            socketHandler.getReconnectTask().setReconnecting(false);
            if(noConnectionWarned) return;

            CommandSender recipient = socketHandler.getKeyHandler().getCurrentlyTryingKey();
            if(recipient == null) recipient = Bukkit.getConsoleSender();
            recipient.sendMessage(ChatColor.RED + "Unable to connect to MCJukebox.");
            recipient.sendMessage(ChatColor.RED + "This could be caused by a period of server maintenance.");
            recipient.sendMessage(ChatColor.GOLD + "If the problem persists, please email support@mcjukebox.net");
            noConnectionWarned = true;
        }

    }

    @AllArgsConstructor
    public class ConnectionSuccessListener implements Emitter.Listener {

        private DropListener dropListener;

        @Override
        public void call(Object... objects) {
            CommandSender recipient = socketHandler.getKeyHandler().getCurrentlyTryingKey();
            String message = "Key accepted and connection to MCJukebox established.";

            if(recipient != null) recipient.sendMessage(ChatColor.GREEN + message);
            else MCJukebox.getInstance().getLogger().info(message);

            dropListener.setLastDripSent(System.currentTimeMillis());
            socketHandler.emit("drip", null);

            for(String channel : queue.keySet()) {
                for(JSONObject params : queue.get(channel)) {
                    socketHandler.emit(channel, params);
                }
            }

            queue.clear();
            noConnectionWarned = false;
            socketHandler.getReconnectTask().reset();
        }

    }

    public void addToQueue(String channel, JSONObject params) {
        List<JSONObject> toRun = queue.containsKey(channel) ? queue.get(channel) : new ArrayList<JSONObject>();
        toRun.add(params);
        queue.put(channel, toRun);
    }

}
