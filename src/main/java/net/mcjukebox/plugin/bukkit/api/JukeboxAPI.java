package net.mcjukebox.plugin.bukkit.api;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

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
        params.put("continueTrack", media.isContinueTrack());
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

       Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
            public void run() {
                MCJukebox.getInstance().getSocketHandler().emit("command/stopAll", params);
            }
        });
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

    /**
     * Get an unmodifiable set of players who are connected to the audio server.
     * <p>This set will be reset if the plugin is reloaded or restarted.
     * <p>This set is for information purposes only and cannot be modified.
     *
     * @return an unmodifiable set of players
     */
    public static Set<Player> getConnectedPlayers() {
        return Collections.unmodifiableSet(MCJukebox.getInstance().getSocketHandler().getConnectedPlayers());
    }

}
