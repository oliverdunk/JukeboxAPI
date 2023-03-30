package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.managers.shows.ShowManager;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class JukeboxCommandExecutor implements TabExecutor {

    private HashMap<String, JukeboxCommand> commands = new HashMap<>();

    public JukeboxCommandExecutor(RegionManager regionManager) {
        commands.put("music", new PlayCommand(ResourceType.MUSIC));
        commands.put("sound", new PlayCommand(ResourceType.SOUND_EFFECT));
//        commands.put("volume", new VolumeCommand()); //Temp disabled till webclient supports
        commands.put("stop", new StopCommand());
        commands.put("setkey", new SetKeyCommand());
        commands.put("region", new RegionCommand(regionManager));
        commands.put("show", new ShowCommand());
        commands.put("import", new ImportCommand(regionManager));
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        // Warn user an API key is needed, unless they have one or are attempting to add one
        boolean requireApiKey = args.length == 0 || !args[0].equalsIgnoreCase("setkey");
        if (MCJukebox.getInstance().getAPIKey() == null && requireApiKey) {
            commandSender.sendMessage(ChatColor.RED + "No API Key set. Type /jukebox setkey <apikey>.");
            commandSender.sendMessage(ChatColor.DARK_RED + "You can get this key from https://www.mcjukebox.net/admin");
            return true;
        }

        // Generate a client URL for users who don't specify an argument, or lack other permissions
        if (args.length == 0 || !commandSender.hasPermission("mcjukebox." + args[0].toLowerCase())) {
            // TODO: Warn user if we skipped the command due to permissions
            return URL(commandSender);
        }

        // Run the command if it exists, or show the help menu otherwise
        if (commands.containsKey(args[0])) {
            JukeboxCommand commandToExecute = commands.get(args[0]);
            args = Arrays.copyOfRange(args, 1, args.length);
            if(commandToExecute.executeWithSelectors(commandSender, args)) return true;
        }

        return help(commandSender);
    }

    private boolean URL(final CommandSender sender) {
        MessageUtils.sendMessage(sender, "user.openLoading");
        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!(sender instanceof Player)) return;
                String token = JukeboxAPI.getToken((Player) sender);
                MessageUtils.sendURL((Player) sender, token);
            }
        });
        return true;
    }

    private boolean help(CommandSender sender){
        sender.sendMessage(ChatColor.GREEN + "Jukebox Commands:");
        sender.sendMessage("/jukebox music <username/@show> <url> {options}");
        sender.sendMessage("/jukebox sound <username/@show> <url> {options}");
//        sender.sendMessage(ChatColor.AQUA + "/jukebox volume <username/@show> <volume>"); //Temp disabled till client alows new methods
        sender.sendMessage("/jukebox stop <username/@show>");
        sender.sendMessage("/jukebox stop <music/all> <username/@show> {options}");
        sender.sendMessage("/jukebox region add <id> <url/@show> [<volume>]");
        sender.sendMessage("/jukebox region remove <id>");
        sender.sendMessage("/jukebox region list <page>");
        sender.sendMessage("/jukebox show add/remove <username> <@show>");
        sender.sendMessage("/jukebox setkey <apikey>");
        sender.sendMessage("/jukebox import <src>");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Create an empty suggestion list. If no suggestions are added, return empty list.
        List<String> suggestions = new ArrayList<>();

        // Suggest the main sub commands
        if (args.length == 1) {
            suggestions.add("help");
            suggestions.addAll(commands.keySet());
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<String>());
        }

        // Suggest values for sub commands
        else if (args.length > 1) {
            Integer argumentIndex = args.length - 2;
            if (commands.get(args[0]) != null) {
                JukeboxCommand cmd = commands.get(args[0]);
                if (cmd.getSuggestions().get(argumentIndex) != null) {
                    suggestions.addAll(cmd.getSuggestions().get(argumentIndex).getSuggestions());
                }
            }
            return StringUtil.copyPartialMatches(args[args.length - 1], suggestions, new ArrayList<String>());
        }

        return suggestions;
    }
}
