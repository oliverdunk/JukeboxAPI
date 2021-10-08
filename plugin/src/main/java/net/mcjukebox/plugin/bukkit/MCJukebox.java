package net.mcjukebox.plugin.bukkit;

import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.mcjukebox.plugin.bukkit.managers.skript.SkriptManager;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowSyncTask;
import net.mcjukebox.plugin.bukkit.sockets.SocketHandler;
import net.mcjukebox.plugin.bukkit.commands.JukeboxCommandExecutor;
import net.mcjukebox.plugin.bukkit.listeners.RegionListener;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.DataUtils;
import net.mcjukebox.plugin.bukkit.managers.LangManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import lombok.Getter;
import net.mcjukebox.plugin.bukkit.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MCJukebox extends JavaPlugin {

    @Getter private static final boolean DEBUG = true;

    //Stores the main plugin instance for access from other classes.
    @Getter private static MCJukebox instance;
    @Getter private SocketHandler socketHandler;
    @Getter private RegionManager regionManager;
    @Getter private SkriptManager skriptManager;
    @Getter private RegionListener regionListener;
    @Getter private LangManager langManager;
    @Getter private ShowManager showManager;
    @Getter private TimeUtils timeUtils;


    private BukkitAudiences adventure;

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    /**
     * Called when the plugin is first loaded by Spigot.
     */
    public void onEnable(){
        this.instance = this;
        this.adventure = BukkitAudiences.create(this);

        langManager = new LangManager();
        MessageUtils.setLangManager(langManager);

        socketHandler = new SocketHandler();
        regionManager = new RegionManager();

        showManager = new ShowManager();
        // This needs to be run synchronously so it can identify when the main thread is busy
        Bukkit.getScheduler().runTaskTimer(this, new ShowSyncTask(), 1, 1);

        timeUtils = new TimeUtils();

        // Only register region events if WorldGuard is installed
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            regionListener = new RegionListener(regionManager);
            Bukkit.getPluginManager().registerEvents(regionListener, this);
        }

        if (Bukkit.getPluginManager().getPlugin("Skript") != null) {
            skriptManager = new SkriptManager();
        }

        Bukkit.getPluginCommand("jukebox").setExecutor(new JukeboxCommandExecutor(regionManager));
        this.getLogger().info(this.getName() + " has been loaded!");
    }

    public String getAPIKey() {
        return (String) DataUtils.loadObjectFromPath(getDataFolder() + "/api.key");
    }

    /**
     * Called when the server is restarted or stopped.
     */
    public void onDisable(){
        socketHandler.disconnect();
        regionManager.save();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

}