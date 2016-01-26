package com.oliverdunk.jukeboxapi;

import com.oliverdunk.jukeboxapi.api.RequestHandler;
import com.oliverdunk.jukeboxapi.commands.JukeboxCommand;
import com.oliverdunk.jukeboxapi.listeners.RegionListener;
import com.oliverdunk.jukeboxapi.utils.RegionUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Jukebox extends JavaPlugin {

    //Stores the main plugin instance for access from other classes.
    @Getter private static Jukebox instance;
    @Getter private String id;
    @Getter private RequestHandler requestHandler;
    @Getter private RegionUtils regionUtils;

    /**
     * Called when the plugin is first loaded by Spigot.
     */
    public void onEnable(){
        this.instance = this;
        this.saveDefaultConfig();
        //TODO: Add method to API for checking API_KEY
        requestHandler = new RequestHandler(getConfig().getString("api_key"));
        id = getConfig().getString("server_id");
        regionUtils = new RegionUtils();

        //Only register region events if WorldGuard is installed
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            Bukkit.getPluginManager().registerEvents(new RegionListener(regionUtils), this);
        }

        Bukkit.getPluginCommand("jukebox").setExecutor(new JukeboxCommand());
        this.getLogger().info(this.getName() + " has been loaded!");
    }

    /**
     * Called when the server is restarted or stopped.
     */
    public void onDisable(){
        regionUtils.save();
    }

}