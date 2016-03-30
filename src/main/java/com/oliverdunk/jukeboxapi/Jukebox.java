package com.oliverdunk.jukeboxapi;

import com.oliverdunk.jukeboxapi.api.RequestHandler;
import com.oliverdunk.jukeboxapi.commands.JukeboxCommand;
import com.oliverdunk.jukeboxapi.listeners.RegionListener;
import com.oliverdunk.jukeboxapi.utils.LangUtils;
import com.oliverdunk.jukeboxapi.utils.MessageUtils;
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
    @Getter private LangUtils langUtils;

    /**
     * Called when the plugin is first loaded by Spigot.
     */
    public void onEnable(){
        this.instance = this;
        this.saveDefaultConfig();

        langUtils = new LangUtils(this);
        langUtils.load();
        MessageUtils.setLangUtils(langUtils);

        requestHandler = new RequestHandler(getConfig().getString("api_key"));
        id = getConfig().getString("server_id");
        regionUtils = new RegionUtils();

        //Only register region events if WorldGuard is installed
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            Bukkit.getPluginManager().registerEvents(new RegionListener(regionUtils), this);
        }

        Bukkit.getPluginCommand("jukebox").setExecutor(new JukeboxCommand(langUtils));
        this.getLogger().info(this.getName() + " has been loaded!");
    }

    /**
     * Called when the server is restarted or stopped.
     */
    public void onDisable(){
        regionUtils.save();
    }

}