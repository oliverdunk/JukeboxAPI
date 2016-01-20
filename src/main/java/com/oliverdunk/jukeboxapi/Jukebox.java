package com.oliverdunk.jukeboxapi;

import com.oliverdunk.jukeboxapi.api.RequestHandler;
import com.oliverdunk.jukeboxapi.commands.JukeboxCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Jukebox extends JavaPlugin {

    //Stores the main plugin instance for access from other classes.
    @Getter private static Jukebox instance;
    @Getter private String id;
    @Getter private RequestHandler requestHandler;

    /**
     * Called when the plugin is first loaded by Spigot.
     */
    public void onEnable(){
        this.instance = this;
        this.saveDefaultConfig();
        //TODO: Add method to API for checking API_KEY
        requestHandler = new RequestHandler(getConfig().getString("api_key"));
        id = getConfig().getString("server_id");

        Bukkit.getPluginCommand("jukebox").setExecutor(new JukeboxCommand());
        this.getLogger().info(this.getName() + " has been loaded!");
    }

}
