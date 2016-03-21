package com.oliverdunk.jukeboxapi.listeners;

import com.oliverdunk.jukeboxapi.api.JukeboxAPI;
import com.oliverdunk.jukeboxapi.api.ResourceType;
import com.oliverdunk.jukeboxapi.api.models.Media;
import com.oliverdunk.jukeboxapi.utils.RegionUtils;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class RegionListener implements Listener{

    private RegionUtils utils;
    private HashMap<String, String> playing = new HashMap<String, String>();

    public RegionListener(RegionUtils utils){
        this.utils = utils;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        //Only execute if the player moves an entire block
        if(!(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())) return;

        //Get all applicable regions which the player is moving into
        ApplicableRegionSet regions = WGBukkit.getRegionManager(e.getTo().getWorld()).getApplicableRegions(e.getTo());

        int highestPriority = -1;
        ProtectedRegion highestRegion = null;
        for(ProtectedRegion region : regions.getRegions()){
            if(region.getPriority() > highestPriority && utils.hasRegion(region.getId())){
                highestPriority = region.getPriority();
                highestRegion = region;
            }
        }

        //In this case, there are no applicable regions so we need go no further
        if(highestRegion == null){
            if(playing.containsKey(e.getPlayer().getUniqueId().toString())){
                JukeboxAPI.stopMusic(e.getPlayer());
                playing.remove(e.getPlayer().getUniqueId().toString());
            }
            return;
        }

        if(playing.containsKey(e.getPlayer().getUniqueId().toString()) &&
                playing.get(e.getPlayer().getUniqueId().toString()).equals(highestRegion.getId())) return;

        if(playing.containsKey(e.getPlayer().getUniqueId().toString()) &&
                utils.getURL(playing.get(e.getPlayer().getUniqueId().toString())).equals(
                utils.getURL(highestRegion.getId()))) return;

        playing.put(e.getPlayer().getUniqueId().toString(), highestRegion.getId());

        Media media = new Media(ResourceType.MUSIC, utils.getURL(highestRegion.getId()));
        JukeboxAPI.play(e.getPlayer(), media);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        if(!playing.containsKey(event.getPlayer().getUniqueId().toString())) return;
        JukeboxAPI.stopMusic(event.getPlayer());
        playing.remove(event.getPlayer().getUniqueId().toString());
    }

}
