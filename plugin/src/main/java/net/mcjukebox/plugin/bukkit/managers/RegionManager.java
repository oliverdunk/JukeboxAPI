package net.mcjukebox.plugin.bukkit.managers;

import lombok.Getter;
import lombok.SneakyThrows;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.OldDataUtils;
import net.mcjukebox.plugin.bukkit.managers.objects.RegionExtended;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

public class RegionManager implements Listener {

    @Getter
    private HashMap<String, RegionExtended> regions = new HashMap<>();
    private String folder;

    public RegionManager(){
        folder = MCJukebox.getInstance().getDataFolder() + "";
        load();
    }


    private void load(){
        JSONObject region = null;
        try {
            region = new JSONObject(Files.readString(Path.of(folder + "/regions.json")));
        for (Object key : region.keySet()) {
            regions.put((String) key, new RegionExtended(region.getJSONObject((String) key).getString("URL"), region.getJSONObject((String) key).getInt("volume")));
        }
        } catch (IOException e) {
            MCJukebox.getInstance().getLogger().log(Level.SEVERE, "Couldn't load regions.json");
        }

        // Import from the "shared.data" file we accidentally created
        HashMap<String, String> sharedFile = OldDataUtils.loadObjectFromPath(folder + "/shared.data");
        if (sharedFile != null) {
            MCJukebox.getInstance().getLogger().info("Running migration of shared.data regions...");
            for (String key : sharedFile.keySet()) regions.put(key, new RegionExtended(sharedFile.get(key), 100));
            new File(folder + "/shared.data").delete();
            MCJukebox.getInstance().getLogger().info("Migration complete.");
        }
        //Import old regions, replaced by regions.js
        HashMap<String, String> regionFile = OldDataUtils.loadObjectFromPath(folder + "/regions.data");
        if (regionFile != null) {
            MCJukebox.getInstance().getLogger().info("Running migration of non extended region file...");
            for (String key : regionFile.keySet()) regions.put(key, new RegionExtended(regionFile.get(key), 100));
            new File(folder + "/regions.data").delete();
            MCJukebox.getInstance().getLogger().info("Migration complete.");
        }
    }

    public int importFromOA() {
        try {
            File configFile = new File("plugins/OpenAudioMc/config.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            ConfigurationSection regionConfig = config.getConfigurationSection("storage.regions");
            int added = 0;
            for (String region : regionConfig.getKeys(false)) {
                String url = regionConfig.getString(region + ".src");
                if (url.length() > 0 && !url.contains(" ")) {
                    regions.put(region.toLowerCase(), new RegionExtended(url, 100));
                    added++;
                }
            }
            return added;
        } catch (Exception ex) {
            return 0;
        }
    }

    public void save(){
        JSONObject data = new JSONObject(regions);

        try (FileWriter file = new FileWriter(folder + "/regions.json")) {
            file.write(data.toString(4));
            file.flush();

        } catch (IOException e) {
            MCJukebox.getInstance().getLogger().info("Error happened while trying to save File");
        }
    }



    public void addRegion(String ID, String URL, int volume){
        regions.put(ID.toLowerCase(), new RegionExtended(URL,volume));
    }

    public void removeRegion(String ID){
        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        HashMap<UUID, String> playersInRegion = MCJukebox.getInstance().getRegionListener().getPlayerInRegion();

        Iterator<UUID> keys = playersInRegion.keySet().iterator();

        while (keys.hasNext()) {
            UUID uuid = keys.next();
            String regionID = playersInRegion.get(uuid);

            if (regionID.equals(ID)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (regions.get(ID).getURL().charAt(0) == '@') {
                    showManager.getShow(regions.get(ID).getURL()).removeMember(player);
                } else {
                    JukeboxAPI.stopMusic(player);
                    keys.remove();
                }
            }
        }

        regions.remove(ID);
    }

    public HashMap<String, RegionExtended> getRegions() { return this.regions; }

    public boolean hasRegion(String ID){
        return regions.containsKey(ID);
    }

    public String getURL(String ID){
        return regions.get(ID).getURL();
    }

    public int getVolume(String ID){
        return regions.get(ID).getVolume();
    }

}
