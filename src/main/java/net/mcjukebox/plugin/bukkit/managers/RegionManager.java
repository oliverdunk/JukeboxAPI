package net.mcjukebox.plugin.bukkit.managers;

import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class RegionManager implements Listener {

    @Getter
    private HashMap<String, String> regions;
    private String folder;

    public RegionManager(){
        folder = MCJukebox.getInstance().getDataFolder() + "";
        load();
    }

    private void load(){
        regions = DataUtils.loadObjectFromPath(folder + "/regions.data");
        if(regions == null) regions = new HashMap<>();
    }

    public void save(){
        DataUtils.saveObjectToPath(regions, folder + "/regions.data");
    }

    public void addRegion(String ID, String URL){
        regions.put(ID.toLowerCase(), URL);
    }

    public void removeRegion(String ID){
        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        HashMap<UUID, String> playersInRegion = MCJukebox.getInstance().getRegionListener().getPlayerInRegion();

        for (HashMap.Entry<UUID, String> entry: playersInRegion.entrySet()) {
            UUID uuid = entry.getKey();
            String regionID = entry.getValue();

            if (regionID.equals(ID)) {

                if (regions.get(ID).charAt(0) == '@') {
                    showManager.getShow(regions.get(ID)).removeMember(Bukkit.getPlayer(uuid));
                } else {
                    JukeboxAPI.stopMusic(Bukkit.getPlayer(uuid));
                }
            }

        }
        regions.remove(ID);
    }

    public boolean hasRegion(String ID){
        return regions.containsKey(ID);
    }

    public String getURL(String ID){
        return regions.get(ID);
    }

}
