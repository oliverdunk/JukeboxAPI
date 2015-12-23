package com.oliverdunk.jukeboxapi.api;

import com.oliverdunk.jukeboxapi.Jukebox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class JukeboxAPI {

    /**
     * Requests that an audio file is played to the player.
     * @param player The player for which the song should be played.
     * @param URL The URL of the audio file (needs to be web compatible).
     * @param type The ResourceType - only one song can be played at a time, but multiple sound effects can play
     */
    public static void play(Player player, String URL, final ResourceType type){
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", player.getName());
        parameters.put("url", URL);
        Bukkit.getScheduler().runTaskAsynchronously(Jukebox.getInstance(), new Runnable() {
            public void run() {
                if(type == ResourceType.MUSIC) RequestHandler.makeRequest("music", parameters);
                else if(type == ResourceType.SOUND_EFFECT) RequestHandler.makeRequest("sound", parameters);
            }
        });
    }

}
