package net.mcjukebox.plugin.bukkit.managers;

import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.utils.DataUtils;
import org.bukkit.event.Listener;

import java.util.HashMap;

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
        regions.remove(ID);
    }

    public boolean hasRegion(String ID){
        return regions.containsKey(ID);
    }

    public String getURL(String ID){
        return regions.get(ID);
    }

}
