package net.mcjukebox.plugin.bukkit.commands;

import lombok.AllArgsConstructor;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
public class RegionCommand extends JukeboxCommand {

    private RegionManager regionManager;

    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
        // region add <id> <url>
        if(args.length == 3 && args[0].equalsIgnoreCase("add")){
            MCJukebox.getInstance().getRegionManager().addRegion(args[1], args[2]);
            MessageUtils.sendMessage(dispatcher, "region.registered");
            return true;
        }

        // region remove <id>
        if(args.length == 2 && args[0].equalsIgnoreCase("remove")){
            if(MCJukebox.getInstance().getRegionManager().hasRegion(args[1])){
                MCJukebox.getInstance().getRegionManager().removeRegion(args[1]);
                MessageUtils.sendMessage(dispatcher, "region.unregistered");
            }else{
                MessageUtils.sendMessage(dispatcher, "region.notregistered");
            }
            return true;
        }

        // region list
        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
            dispatcher.sendMessage(ChatColor.GREEN + "Registered Regions (" + regionManager.getRegions().size() + "):");
            for(String region : regionManager.getRegions().keySet()) {
                dispatcher.sendMessage("");
                dispatcher.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + region);
                dispatcher.sendMessage(ChatColor.GOLD + "URL/Show: " + ChatColor.WHITE + regionManager.getRegions().get(region));
            }
            return true;
        }

        return false;
    }

}
