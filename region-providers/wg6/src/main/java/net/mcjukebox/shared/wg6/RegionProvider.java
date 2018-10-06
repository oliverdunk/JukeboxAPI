package net.mcjukebox.shared.wg6;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mcjukebox.shared.api.Region;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionProvider implements net.mcjukebox.shared.api.RegionProvider {

    public List<Region> getApplicableRegions(Location location) {
        ArrayList regionList = new ArrayList<Region>();

        RegionManager regionManager = WGBukkit.getRegionManager(location.getWorld());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

        for (ProtectedRegion region : regions) {
            regionList.add(new Region(region.getId(), region.getPriority()));
        }

        return regionList;
    }

}
