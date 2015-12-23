package com.oliverdunk.jukeboxapi.api;

import com.oliverdunk.jukeboxapi.Jukebox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class JukeboxAPI {

    /**
     * Requests that a song is played to the player. This will cause any other songs currently playing to stop. If this
     * is not your intended behaviour, use the playSoundEffect method instead.
     * @param player The player for which the song should be played.
     * @param URL The URL of the audio file (needs to be web compatible).
     */
    public static void playSong(Player player, String URL){
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", player.getName());
        parameters.put("url", URL);
        Bukkit.getScheduler().runTaskAsynchronously(Jukebox.getInstance(), new Runnable() {
            public void run() {
                RequestHandler.makeRequest("music", parameters);
            }
        });
    }

    /**
     * Requests that a sound effect is played to the player. This will play on top of any currently playing.
     * If this is not your intended behaviour, use the playSong method instead.
     * @param player The player for which the sound effect should be played.
     * @param URL The URL of the audio file (needs to be web compatible).
     */
    public static void playSoundEffect(Player player, String URL){
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", player.getName());
        parameters.put("url", URL);
        Bukkit.getScheduler().runTaskAsynchronously(Jukebox.getInstance(), new Runnable() {
            public void run() {
                RequestHandler.makeRequest("sound", parameters);
            }
        });
    }

}
