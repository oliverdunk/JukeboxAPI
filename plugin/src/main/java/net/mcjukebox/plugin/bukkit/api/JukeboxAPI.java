package net.mcjukebox.plugin.bukkit.api;

import lombok.NonNull;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.sockets.listeners.TokenListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class JukeboxAPI {

    /**
     * Requests that an audio file is played to the player.
     *
     * @param player The player for which the song should be played
     * @param media The file which should be played
     */
    public static void play(Player player, final Media media){
        final JSONObject params = new JSONObject();
        params.put("username", player.getName());
        params.put("url", media.getURL());
        params.put("volume", media.getVolume());
        params.put("looping", media.isLooping());
        params.put("channel", media.getChannel());
        if(media.getFadeDuration() != -1) params.put("fadeDuration", media.getFadeDuration());
        if(media.getStartTime() != -1) params.put("startTime", media.getStartTime());
        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
            public void run() {
                String channel = media.getType() == ResourceType.MUSIC ? "playMusic" : "playSound";
                MCJukebox.getInstance().getSocketHandler().emit("command/" + channel, params);
            }
        });
    }
//TODO: Wait for oliver to implement something to help with this on the web client side! [Enable and compile at your own risk, might only work with my custom client method till the near future]

//    /**
//     * Requests a change of volume for the player.
//     *
//     * @param player The player for which the volume should be changed
//     * @param volume The new volume music should be at
//     * @param channel The channel where the volume should be changed
//     */
//    public static void changeVolume(Player player, int volume, String channel){
//        if(volume < 0) volume = 0;
//        if(volume > 100) volume = 100;
//        final JSONObject params = new JSONObject();
//        params.put("username", player.getName());
//        params.put("volume", volume);
//        params.put("channel", channel);
//        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
//            public void run() {
//                MCJukebox.getInstance().getSocketHandler().emit("command/changeVolume", params);
//            }
//        });
//    }
//
//    /**
//     * Requests a change of volume for the player.
//     *
//     * @param player The player for which the volume should be changed
//     * @param volume The new volume music should be at
//     */
//    public static void changeVolume(Player player, int volume){
//        if(volume < 0) volume = 0;
//        if(volume > 100) volume = 100;
//        final JSONObject params = new JSONObject();
//        params.put("username", player.getName());
//        params.put("volume", volume);
//        params.put("channel", "default");
//        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
//            public void run() {
//                MCJukebox.getInstance().getSocketHandler().emit("command/changeVolume", params);
//            }
//        });
//    }

    /**
     * Stops the current music track.
     *
     * @param player The player to stop the music for.
     */
    public static void stopMusic(Player player){
        stopMusic(player, "default", -1);
    }

    /**
     * Stops the current music track.
     *
     * @param player The player to stop the music for.
     * @param channel Channel to play music in, default is "default"
     * @param fadeDuration Length of fade, use 0 to disable and -1 for default
     */
    public static void stopMusic(Player player, String channel, int fadeDuration){
        final JSONObject params = new JSONObject();
        params.put("username", player.getName());
        params.put("channel", channel);
        if(fadeDuration != -1) params.put("fadeDuration", fadeDuration);
        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
            public void run() {
                MCJukebox.getInstance().getSocketHandler().emit("command/stopMusic", params);
            }
        });
    }

    /**
     * Stops all music and sounds in a channel.
     *
     * @param player The player to stop the music for.
     * @param channel Channel to play music in, default is "default"
     * @param fadeDuration Length of fade, use 0 to disable and -1 for default
     */
    public static void stopAll(Player player, String channel, int fadeDuration) {
        final JSONObject params = new JSONObject();
        params.put("username", player.getName());
        params.put("channel", channel);
        if(fadeDuration != -1) params.put("fadeDuration", fadeDuration);
;        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
            public void run() {
                MCJukebox.getInstance().getSocketHandler().emit("command/stopAll", params);
            }
        });
    }

    /**
     * Gets the authentication token needed for a player to open the client. Since tokens are requested from the server,
     * this method blocks the thread until a token is received. It should always be run asynchronously.
     *
     * @param player The player to get the token for
     * @return Token to be used in the query parameters of the client URL
     */
    public static String getToken(Player player) {
        // Construct and send request for token
        JSONObject params = new JSONObject();
        params.put("username", player.getName());
        MCJukebox.getInstance().getSocketHandler().emit("command/getToken", params);

        // Create a new lock that can be notified when a token is received
        Object lock = new Object();

        synchronized (lock) {
            try {
                TokenListener tokenListener = MCJukebox.getInstance().getSocketHandler().getTokenListener();
                // Register the lock so that we are notified when a new token is received
                tokenListener.addLock(player.getName(), lock);
                lock.wait();
                return tokenListener.getToken(player.getName());
            } catch (InterruptedException exception) {
                // This should never happen, so someone has gone out of their way to cancel this call
                return null;
            }
        }
    }

	/**
	 * Gets the ShowManager instance currently in use. Note that show are a client side feature, implemented
     * as as a higher level version of our internal channels system which allows multiple audio tracks
     * to be played simultaneously.
     *
     * @return ShowManager being used by MCJukebox
     */
    public static ShowManager getShowManager() {
        return MCJukebox.getInstance().getShowManager();
    }

}
