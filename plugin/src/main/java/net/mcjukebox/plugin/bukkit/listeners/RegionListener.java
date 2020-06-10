package net.mcjukebox.plugin.bukkit.listeners;

import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.HashMap;
import java.util.UUID;

public class RegionListener implements Listener {

    private RegionManager utils;
    @Getter
    private HashMap<UUID, String> playerInRegion = new HashMap<>();

    public RegionListener(RegionManager utils) {
        this.utils = utils;
    }

    private void handleMovement(Player player, Location from, Location to) {
        //Only execute if the player moves an entire block
        if (from != null
                && from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) return;

        int highestPriority = -1;
        String highestRegion = null;
        for (IWrappedRegion region : WorldGuardWrapper.getInstance().getRegions(to)) {
            if (region.getPriority() > highestPriority && utils.hasRegion(region.getId())) {
                highestPriority = region.getPriority();
                highestRegion = region.getId();
            }
        }

        ShowManager showManager = MCJukebox.getInstance().getShowManager();

        if (highestRegion == null && utils.hasRegion("__global__")) {
            highestRegion = "__global__";
        }

        //In this case, there are no applicable shared so we need go no further
        if (highestRegion == null) {
            if (playerInRegion.containsKey(player.getUniqueId())) {
                String lastShow = utils.getURL(playerInRegion.get(player.getUniqueId()));
                playerInRegion.remove(player.getUniqueId());

                if (lastShow == null || lastShow.toCharArray()[0] != '@') {
                    //Region no longer exists, stop the music.
                    JukeboxAPI.stopMusic(player);
                    return;
                } else {
                    showManager.getShow(lastShow).removeMember(player);
                    return;
                }
            }
            return;
        }

        if (playerInRegion.containsKey(player.getUniqueId()) &&
                playerInRegion.get(player.getUniqueId()).equals(highestRegion)) return;

        if (playerInRegion.containsKey(player.getUniqueId()) &&
                utils.getURL(playerInRegion.get(player.getUniqueId())).equals(utils.getURL(highestRegion))) {
            // No need to restart the track, or re-add them to a show, but still update our records
            playerInRegion.put(player.getUniqueId(), highestRegion);
            return;
        }

        if (playerInRegion.containsKey(player.getUniqueId())) {
            String lastShow = utils.getURL(playerInRegion.get(player.getUniqueId()));
            if (lastShow.toCharArray()[0] == '@') {
                showManager.getShow(lastShow).removeMember(player);
            }
        }

        if (utils.getURL(highestRegion).toCharArray()[0] == '@') {
            if (playerInRegion.containsKey(player.getUniqueId())) JukeboxAPI.stopMusic(player);
            showManager.getShow(utils.getURL(highestRegion)).addMember(player, true);
            playerInRegion.put(player.getUniqueId(), highestRegion);
            return;
        }

        Media media = new Media(ResourceType.MUSIC, utils.getURL(highestRegion));
        JukeboxAPI.play(player, media);
        playerInRegion.put(player.getUniqueId(), highestRegion);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        //The from location has to be offset else the event will not be run
        handleMovement(event.getPlayer(), null, event.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }
        handleMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecartMove(VehicleMoveEvent event) {
        if (event.getVehicle().getPassenger() == null || !(event.getVehicle().getPassenger() instanceof Player)) {
            return;
        }
        handleMovement((Player) event.getVehicle().getPassenger(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }
        handleMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        if (playerInRegion.containsKey(event.getPlayer().getUniqueId())) {
            String lastAudio = utils.getURL(playerInRegion.get(event.getPlayer().getUniqueId()));

            if (lastAudio == null || lastAudio.toCharArray()[0] != '@') {
                JukeboxAPI.stopMusic(event.getPlayer());
            }

            playerInRegion.remove(event.getPlayer().getUniqueId());
        }

        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        if (showManager.inInShow(event.getPlayer().getUniqueId())) {
            for (Show show : showManager.getShowsByPlayer(event.getPlayer().getUniqueId())) {
                //Only run if they were added by a region
                if (!show.getMembers().get(event.getPlayer().getUniqueId())) return;
                show.removeMember(event.getPlayer());
            }
        }
    }

}
