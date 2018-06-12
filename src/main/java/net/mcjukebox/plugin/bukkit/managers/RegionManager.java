package net.mcjukebox.plugin.bukkit.managers;

import lombok.Getter;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.DataUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionManager implements Listener {

    @Getter
    private HashMap<String, String> regions;
    private String folder;

    public RegionManager(){
        folder = MCJukebox.getInstance().getDataFolder() + "";
        load();
    }

    private void load(){
        regions = DataUtils.loadObjectFromPath(folder + "/regions.data");
        if(regions == null) regions = new HashMap<>();
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
                    regions.put(region.toLowerCase(), url);
                    added++;
                }
            }
            return added;
        } catch (Exception ex) {
            return 0;
        }
    }

    public void save(){
        DataUtils.saveObjectToPath(regions, folder + "/regions.data");
    }

    public void addRegion(String ID, String URL){
        regions.put(ID.toLowerCase(), URL);
    }

    public void removeRegion(String ID){
        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        HashMap<UUID, String> playersInRegion = MCJukebox.getInstance().getRegionListener().getPlayerInRegion();

        for (Map.Entry<UUID, String> entry : playersInRegion.entrySet()) {
            UUID uuid = entry.getKey();
            String regionID = entry.getValue();

            if (regionID.equals(ID)) {
                if (regions.get(ID).charAt(0) == '@') {
                    showManager.getShow(regions.get(ID)).removeMember(Bukkit.getPlayer(uuid));
                } else {
                    JukeboxAPI.stopMusic(Bukkit.getPlayer(uuid));
                    playersInRegion.remove(uuid);
                }
            }
        }
        regions.remove(ID);
    }

    public boolean hasRegion(String ID){
        return regions.containsKey(ID);
    }

    public String getURL(String ID){
        return regions.get(ID);
    }

}
