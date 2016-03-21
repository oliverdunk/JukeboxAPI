package com.oliverdunk.jukeboxapi.utils;

import org.bukkit.event.Listener;

import java.util.HashMap;

public class RegionUtils implements Listener {

    private HashMap<String, String> regions;

    public RegionUtils(){
        load();
    }

    private void load(){
        regions = DataUtils.loadObjectFromPath("plugins/JukeboxAPI/regions.data");
        if(regions == null) regions = new HashMap<>();
    }

    public void save(){
        DataUtils.saveObjectToPath(regions, "plugins/JukeboxAPI/regions.data");
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
