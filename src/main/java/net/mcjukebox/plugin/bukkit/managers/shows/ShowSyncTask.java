package net.mcjukebox.plugin.bukkit.managers.shows;

import net.mcjukebox.plugin.bukkit.MCJukebox;

/**
 * This task identifies when the server freezes and jumps back in show music tracks to compensate. The task is run
 * every tick but only affects shows with exclamation marks at the start of their name.
 */
public class ShowSyncTask implements Runnable {

    private long lastSync;
    private long expectedTime;

    public ShowSyncTask() {
        expectedTime = System.currentTimeMillis();
        lastSync = 0;
    }

    @Override
    public void run() {
        // Update expected time as a tick has passed
        expectedTime = expectedTime + (1000 / 20);

        // If the server froze for more than a second
        if (System.currentTimeMillis() >= expectedTime + 1000) {
            // and we haven't jumped back in the track for the last three seconds
            if (System.currentTimeMillis() - 3000 > lastSync) {
                jumpBack(System.currentTimeMillis() - expectedTime);
                lastSync = System.currentTimeMillis();
                expectedTime = System.currentTimeMillis();
            }
        }
    }

    private void jumpBack(long offset) {
        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        for (String showName : showManager.getShows().keySet()) {
            if (showName.startsWith("!")) {
                Show show = showManager.getShow(showName);
                show.jumpBack(offset);
            }
        }
    }

}
