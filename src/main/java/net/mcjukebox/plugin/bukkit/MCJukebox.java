package net.mcjukebox.plugin.bukkit;

import net.mcjukebox.plugin.bukkit.api.SocketHandler;
import net.mcjukebox.plugin.bukkit.commands.JukeboxCommand;
import net.mcjukebox.plugin.bukkit.listeners.RegionListener;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.DataUtils;
import net.mcjukebox.plugin.bukkit.managers.LangManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MCJukebox extends JavaPlugin {

    @Getter private static final boolean DEBUG = true;

    //Stores the main plugin instance for access from other classes.
    @Getter private static MCJukebox instance;
    @Getter private SocketHandler socketHandler;
    @Getter private RegionManager regionManager;
    @Getter private RegionListener regionListener;
    @Getter private LangManager langManager;
    @Getter private ShowManager showManager;

    /**
     * Called when the plugin is first loaded by Spigot.
     */
    public void onEnable(){
        this.instance = this;

        langManager = new LangManager();
        MessageUtils.setLangManager(langManager);

        socketHandler = new SocketHandler();
        regionManager = new RegionManager();
        showManager = new ShowManager();

        //Only register region events if WorldGuard is installed
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            regionListener = new RegionListener(regionManager);
            Bukkit.getPluginManager().registerEvents(regionListener, this);
        }

        Bukkit.getPluginCommand("jukebox").setExecutor(new JukeboxCommand(regionManager));
        this.getLogger().info(this.getName() + " has been loaded!");
    }

    public String getAPIKey() {
        return (String) DataUtils.loadObjectFromPath(getDataFolder() + "/api.key");
    }

    public void setAPIKey(String key) {
        DataUtils.saveObjectToPath(key, getDataFolder() + "/api.key");
        socketHandler.disconnect();
        socketHandler.attemptConnection();
    }

    /**
     * Called when the server is restarted or stopped.
     */
    public void onDisable(){
        socketHandler.disconnect();
        regionManager.save();
    }

}