package net.mcjukebox.plugin.bukkit.commands;

import lombok.AllArgsConstructor;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.HashMap;

public class RegionCommand extends JukeboxCommand {

    private static final int REGIONS_PER_PAGE = 5;
    private RegionManager regionManager;

    public RegionCommand(RegionManager regionManager) {
        this.regionManager = regionManager;
        suggestions.put(0, new PlayerOrShowTabArgument(new String[] {"add", "remove", "list"}));
        suggestions.put(1, new RegionTabArgument());
        suggestions.put(2, new ShowTabArgument());
    }

    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
        // region add <id> <url>
        if (args.length == 3 && args[0].equalsIgnoreCase("add")){
            MCJukebox.getInstance().getRegionManager().addRegion(args[1], args[2]);
            MessageUtils.sendMessage(dispatcher, "region.registered");
            return true;
        }

        // region remove <id>
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")){
            if(MCJukebox.getInstance().getRegionManager().hasRegion(args[1])){
                MCJukebox.getInstance().getRegionManager().removeRegion(args[1]);
                MessageUtils.sendMessage(dispatcher, "region.unregistered");
            }else{
                MessageUtils.sendMessage(dispatcher, "region.notregistered");
            }
            return true;
        }

        // region list
        if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("list")) {
            ArrayList<String> regions = new ArrayList<String>(regionManager.getRegions().keySet());

            int pageCount = (regions.size() - 1) / REGIONS_PER_PAGE + 1;

            int page = 1;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {}
                    
                if (page > pageCount) {
                    return false;
                }
            }

            dispatcher.sendMessage(ChatColor.GREEN + "Registered Regions (Page " + page + "/" + pageCount + "):");
            dispatcher.sendMessage("");

            for (int i = (page-1) * REGIONS_PER_PAGE; i < page * REGIONS_PER_PAGE && i < regions.size(); i++) {
                String region = regions.get(i);
                dispatcher.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + region);
                dispatcher.sendMessage(ChatColor.GOLD + "URL/Show: " + ChatColor.WHITE + regionManager.getRegions().get(region));

                if (i != regions.size() - 1) {
                    dispatcher.sendMessage("");
                }
            }

            if (page < pageCount) {
                dispatcher.sendMessage(ChatColor.GRAY + "Type '/jukebox region list " + (page + 1) + "' to see more...");
            }

            return true;
        }

        return false;
    }

}
