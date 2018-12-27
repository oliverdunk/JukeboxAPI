package net.mcjukebox.shared.utils;

import net.mcjukebox.shared.api.Region;
import net.mcjukebox.shared.api.RegionProvider;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {

    private static RegionUtils instance;

    public static RegionUtils getInstance() {
        if (instance == null) {
            instance = new RegionUtils();
        }
        return instance;
    }

    public net.mcjukebox.shared.api.RegionProvider getProvider() {
        if (classExists("com.sk89q.worldguard.bukkit.WGBukkit")) {
            return new net.mcjukebox.shared.wg6.RegionProvider();
        } else if (classExists("com.sk89q.worldguard.bukkit.WorldGuardPlugin")) {
            return new net.mcjukebox.shared.wg7.RegionProvider();
        } else {
            return new RegionProvider() {
                public String getName() {
                    return null;
                }

                public List<Region> getApplicableRegions(Location location) {
                    return new ArrayList<Region>();
                }
            };
        }
    }

    private boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
