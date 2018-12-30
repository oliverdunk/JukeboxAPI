package net.mcjukebox.shared.wg7;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mcjukebox.shared.api.Region;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RegionProvider implements net.mcjukebox.shared.api.RegionProvider {

    public String getName() {
        return "wg7";
    }

    public List<Region> getApplicableRegions(Location location) {
        ArrayList regionList = new ArrayList<Region>();

        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();

        World world = BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = platform.getRegionContainer().get(world);
        org.bukkit.util.Vector bukkitVector = location.toVector();
        BlockVector3 vector = BlockVector3.at(bukkitVector.getX(), bukkitVector.getY(), bukkitVector.getZ());
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(vector).getRegions();

        for (ProtectedRegion region : regions) {
            regionList.add(new Region(region.getId(), region.getPriority()));
        }

        return regionList;
    }

}
