package net.mcjukebox.shared.api;

import org.bukkit.Location;

import java.util.List;

public interface RegionProvider {

    public String getName();
    public List<Region> getApplicableRegions(Location location);

}
