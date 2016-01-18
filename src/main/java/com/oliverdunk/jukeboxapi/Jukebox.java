package com.oliverdunk.jukeboxapi;

import com.oliverdunk.jukeboxapi.api.RequestHandler;
import com.oliverdunk.jukeboxapi.commands.JukeboxCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Jukebox extends JavaPlugin {

    //Stores the main plugin instance for access from other classes.
    private static Jukebox instance;
    public static String id;

    /**
     * Called when the plugin is first loaded by Spigot.
     */
    public void onEnable(){
        this.instance = this;
        this.saveDefaultConfig();
        //TODO: Add method to API for checking API_KEY
        RequestHandler.setAPI_Key(getConfig().getString("api_key"));
        id = getConfig().getString("server_id");

        Bukkit.getPluginCommand("jukebox").setExecutor(new JukeboxCommand());
        this.getLogger().info(this.getName() + " has been loaded!");
    }

    /**
     * Gets the current plugin instance.
     * @return An instance of the Jukebox class.
     */
    public static Jukebox getInstance(){
        return instance;
    }

}
