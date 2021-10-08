package net.mcjukebox.plugin.bukkit.managers.objects;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegionExtended {

    private String URL;
    private int volume;

    public RegionExtended(String URL, int volume){
        this.URL = URL;
        this.volume = volume;
    }
}
