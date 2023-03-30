package net.mcjukebox.plugin.bukkit.managers.objects;

import lombok.Data;

@Data
public class RegionExtended {

    private String URL;
    private int volume;

    public RegionExtended(String URL, int volume){
        this.URL = URL;
        this.volume = volume;
    }
}
