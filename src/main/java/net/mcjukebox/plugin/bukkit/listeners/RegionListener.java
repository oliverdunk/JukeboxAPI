package net.mcjukebox.plugin.bukkit.listeners;

import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class RegionListener implements Listener{

    private RegionManager utils;
    @Getter private HashMap<UUID, String> playerInRegion = new HashMap<UUID, String>();

    public RegionListener(RegionManager utils){
        this.utils = utils;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        //The from location has to be offset else the event will not be run
        onMove(new PlayerMoveEvent(event.getPlayer(), event.getPlayer().getLocation().add(1, 0, 0), event.getPlayer().getLocation()));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        onMove(new PlayerMoveEvent(event.getPlayer(), event.getFrom(), event.getTo()));
    }

    @EventHandler
    public void onMinecartMove(VehicleMoveEvent event) {
        if (event.getVehicle().getPassenger() != null && event.getVehicle().getPassenger() instanceof Player) {
            onMove(new PlayerMoveEvent((Player) event.getVehicle().getPassenger(), event.getFrom(), event.getTo()));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        //Only execute if the player moves an entire block
        if(!(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())) return;

        //Get all applicable regions which the player is moving into
        com.sk89q.worldguard.protection.managers.RegionManager regionManager = WGBukkit.getRegionManager(e.getTo().getWorld());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(e.getTo());

        ShowManager showManager = MCJukebox.getInstance().getShowManager();

        int highestPriority = -1;
        String highestRegion = null;
        for(ProtectedRegion region : regions.getRegions()){
            if(region.getPriority() > highestPriority && utils.hasRegion(region.getId())){
                highestPriority = region.getPriority();
                highestRegion = region.getId();
            }
        }

        if(highestRegion == null && utils.hasRegion("__global__")) {
            highestRegion = "__global__";
        }

        //In this case, there are no applicable regions so we need go no further
        if(highestRegion == null){
            if(playerInRegion.containsKey(e.getPlayer().getUniqueId())){
                String lastShow = utils.getURL(playerInRegion.get(e.getPlayer().getUniqueId()));
                playerInRegion.remove(e.getPlayer().getUniqueId());

                if (lastShow == null || lastShow.toCharArray()[0] != '@') {
                    //Region no longer exists, stop the music.
                    JukeboxAPI.stopMusic(e.getPlayer());
                    return;
                } else if(lastShow.toCharArray()[0] == '@') {
                    showManager.getShow(lastShow).removeMember(e.getPlayer());
                    return;
                }
            }
            return;
        }

        if(playerInRegion.containsKey(e.getPlayer().getUniqueId()) &&
                playerInRegion.get(e.getPlayer().getUniqueId()).equals(highestRegion)) return;

        if(playerInRegion.containsKey(e.getPlayer().getUniqueId()) &&
                utils.getURL(playerInRegion.get(e.getPlayer().getUniqueId())).equals(
                utils.getURL(highestRegion))) {
            // No need to restart the track, or re-add them to a show, but still update our records
            playerInRegion.put(e.getPlayer().getUniqueId(), highestRegion);
        }

        if(playerInRegion.containsKey(e.getPlayer().getUniqueId())) {
            String lastShow = utils.getURL(playerInRegion.get(e.getPlayer().getUniqueId()));
            if(lastShow.toCharArray()[0] == '@') {
                showManager.getShow(lastShow).removeMember(e.getPlayer());
            }
        }

        if(utils.getURL(highestRegion).toCharArray()[0] == '@') {
            if(playerInRegion.containsKey(e.getPlayer().getUniqueId())) JukeboxAPI.stopMusic(e.getPlayer());
            showManager.getShow(utils.getURL(highestRegion)).addMember(e.getPlayer(), true);
            playerInRegion.put(e.getPlayer().getUniqueId(), highestRegion);
            return;
        }

        Media media = new Media(ResourceType.MUSIC, utils.getURL(highestRegion));
        JukeboxAPI.play(e.getPlayer(), media);
        playerInRegion.put(e.getPlayer().getUniqueId(), highestRegion);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        if(playerInRegion.containsKey(event.getPlayer().getUniqueId())) {
            String lastAudio = utils.getURL(playerInRegion.get(event.getPlayer().getUniqueId()));

            if(lastAudio == null || lastAudio.toCharArray()[0] != '@') {
                JukeboxAPI.stopMusic(event.getPlayer());
            }

            playerInRegion.remove(event.getPlayer().getUniqueId());
        }

        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        if(showManager.inInShow(event.getPlayer().getUniqueId())) {
            for(Show show : showManager.getShowsByPlayer(event.getPlayer().getUniqueId())) {
                //Only run if they were added by a region
                if (!show.getMembers().get(event.getPlayer().getUniqueId())) return;
                show.removeMember(event.getPlayer());
            }
        }
    }

}
