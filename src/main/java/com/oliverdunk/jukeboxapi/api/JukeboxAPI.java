package com.oliverdunk.jukeboxapi.api;

import com.oliverdunk.jukeboxapi.Jukebox;
import com.oliverdunk.jukeboxapi.api.models.Media;
import com.oliverdunk.jukeboxapi.api.models.ResourceType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class JukeboxAPI {

    /**
     * Requests that an audio file is played to the player.
     *
     * @param player The player for which the song should be played
     * @param URL The URL of the audio file (needs to be web compatible)
     * @param type The ResourceType - only one song can be played at a time, but multiple sound effects can play
     * @deprecated Use the Media parameter instead, as this is more future proof
     */
    @Deprecated
    public static void play(Player player, String URL, final ResourceType type){
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", player.getName());
        parameters.put("url", URL);
        Bukkit.getScheduler().runTaskAsynchronously(Jukebox.getInstance(), new Runnable() {
            public void run() {
                if(type == ResourceType.MUSIC) Jukebox.getInstance().getRequestHandler().makeRequest("music", parameters);
                else if(type == ResourceType.SOUND_EFFECT) Jukebox.getInstance().getRequestHandler().makeRequest("sound", parameters);
            }
        });
    }

    /**
     * Requests that an audio file is played to the player.
     *
     * @param player The player for which the song should be played
     * @param media The file which should be played
     */
    public static void play(Player player, final Media media){
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", player.getName());
        parameters.put("url", media.getURL());
        parameters.put("pan", media.getPan() + "");
        parameters.put("volume", media.getVolume() + "");
        Bukkit.getScheduler().runTaskAsynchronously(Jukebox.getInstance(), new Runnable() {
            public void run() {
                if(media.getType() == ResourceType.MUSIC) Jukebox.getInstance().getRequestHandler().makeRequest("music", parameters);
                else if(media.getType() == ResourceType.SOUND_EFFECT) Jukebox.getInstance().getRequestHandler().makeRequest("sound", parameters);
            }
        });
    }

    /**
     * Stops the current music track.
     *
     * @param player The player to stop the music for.
     */
    public static void stopMusic(Player player){
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", player.getName());
        Bukkit.getScheduler().runTaskAsynchronously(Jukebox.getInstance(), new Runnable() {
            public void run() {
                Jukebox.getInstance().getRequestHandler().makeRequest("stopMusic", parameters);
            }
        });
    }

}
