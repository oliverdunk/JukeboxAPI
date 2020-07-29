package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ImportCommand extends JukeboxCommand {

    private RegionManager regionManager;

    public ImportCommand(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
        if (args.length != 1) return false;

        int imported = -1;

        // Import using the correct importer
        switch (args[0]) {
            case "oa":
                imported = regionManager.importFromOA();
                break;
        }

        if (imported >= 0) {
            // At the very least, we found an importer to run
            dispatcher.sendMessage("" + ChatColor.GREEN + imported + " region(s) imported.");
        } else {
            // We never found a compatible importer
            dispatcher.sendMessage(ChatColor.RED + "Unknown import source.");
        }

        return true;
    }

}
