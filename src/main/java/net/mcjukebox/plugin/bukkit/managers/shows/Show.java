package net.mcjukebox.plugin.bukkit.managers.shows;

import lombok.Getter;
import lombok.Setter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Show {

    @Getter private HashMap<UUID, Boolean> members = new HashMap<UUID, Boolean>();
    @Getter private Media currentTrack;
    @Getter @Setter private String channel = "default";

    public void addMember(Player player, boolean addedByRegion) {
        if(members.containsKey(player.getUniqueId())) return;
        members.put(player.getUniqueId(), addedByRegion);

        if(currentTrack != null) JukeboxAPI.play(player, currentTrack);
    }

    public void removeMember(Player player) {
        if(!members.containsKey(player.getUniqueId())) return;
        members.remove(player.getUniqueId());
        JukeboxAPI.stopAll(player, channel, -1);
    }

    public void play(Media media) {
        media.setStartTime(MCJukebox.getInstance().getTimeUtils().currentTimeMillis());
        media.setChannel(channel);

        if(media.getType() == ResourceType.MUSIC) {
            currentTrack = media;
        }

        for(UUID UUID : members.keySet()) {
            if(Bukkit.getPlayer(UUID) == null) continue;
            JukeboxAPI.play(Bukkit.getPlayer(UUID), media);
        }
    }

    public void stopMusic() {
        stopMusic(-1);
    }

    public void stopMusic(int fadeDuration) {
        if(currentTrack == null) return;
        for(UUID UUID : members.keySet()) {
            if(Bukkit.getPlayer(UUID) == null) continue;
            JukeboxAPI.stopMusic(Bukkit.getPlayer(UUID), channel, fadeDuration);
        }
        currentTrack = null;
    }

    public void stopAll() {
        stopAll(-1);
    }

    public void stopAll(int fadeDuration) {
        for(UUID UUID : members.keySet()) {
            if(Bukkit.getPlayer(UUID) == null) continue;
            JukeboxAPI.stopAll(Bukkit.getPlayer(UUID), channel, fadeDuration);
        }
    }

}
