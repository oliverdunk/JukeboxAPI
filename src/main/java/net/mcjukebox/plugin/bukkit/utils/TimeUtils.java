package net.mcjukebox.plugin.bukkit.utils;

import net.mcjukebox.plugin.bukkit.MCJukebox;

public class TimeUtils {

    private static boolean hasUpdated = false;
    private static long offset = 0;

    public boolean hasUpdated() {
        return hasUpdated;
    }

    public void updateOffset(long roundTripTime, long serverTime) {
        long offsetFromServer = roundTripTime / 2;
        long estimatedTime = serverTime + offsetFromServer;

        offset = estimatedTime - System.currentTimeMillis();
        hasUpdated = true;

        MCJukebox.getInstance().getLogger().info("Predicted time offset: " + offset + "ms.");
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis() + offset;
    }

}
