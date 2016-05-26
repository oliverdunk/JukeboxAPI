package com.oliverdunk.jukeboxapi.api;

import com.oliverdunk.jukeboxapi.Jukebox;
import com.oliverdunk.jukeboxapi.api.models.Media;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class JukeboxAPI {

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
        parameters.put("looping", media.isLooping() + "");
        if(media.getFadeDuration() != -1) parameters.put("fadeDuration", media.getFadeDuration() + "");
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
