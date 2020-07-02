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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JukeboxCommandExecutor implements TabExecutor {

    private HashMap<String, JukeboxCommand> commands = new HashMap<String, JukeboxCommand>();

    public JukeboxCommandExecutor(RegionManager regionManager) {
        commands.put("music", new PlayCommand(ResourceType.MUSIC));
        commands.put("sound", new PlayCommand(ResourceType.SOUND_EFFECT));
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
        sender.sendMessage("/jukebox stop <username/@show>");
        sender.sendMessage("/jukebox stop <music/all> <username/@show> {options}");
        sender.sendMessage("/jukebox region add <id> <url/@show>");
        sender.sendMessage("/jukebox region remove <id>");
        sender.sendMessage("/jukebox region list <page>");
        sender.sendMessage("/jukebox show add/remove <username> <@show>");
        sender.sendMessage("/jukebox setkey <apikey>");
        sender.sendMessage("/jukebox import <src>");
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Create an empty suggestion list. If no suggestions are added, player gets no suggestions.
        List<String> suggestions = new ArrayList<>();

        // Suggest the main sub commands
        if (args.length == 1) {
            suggestions.add("help");
            for (String cmd : commands.keySet()) {
                suggestions.add(cmd);
            }
            return suggestions;
        }

        // Suggest values for sub commands
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("music") || args[0].equalsIgnoreCase("sound") || args[0].equalsIgnoreCase("stop")) {
                for (String suggestion : getOnlinePlayers()) {
                    suggestions.add(suggestion);
                }
                for (String suggestion : getExistingShows()) {
                    suggestions.add(suggestion);
                }

                if (args[0].equalsIgnoreCase("stop")) {
                    suggestions.add("music");
                    suggestions.add("all");
                }
            }

            else if (args[0].equals("region")) {
                suggestions.add("add");
                suggestions.add("remove");
                suggestions.add("list");
            }

            else if (args[0].equals("show")) {
                suggestions.add("add");
                suggestions.add("remove");
            }
            return suggestions;
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("stop")) {
                if (args[1].equalsIgnoreCase("music") || args[1].equalsIgnoreCase("all")) {
                    for (String suggestion : getOnlinePlayers()) {
                        suggestions.add(suggestion);
                    }
                    for (String suggestion : getExistingShows()) {
                        suggestions.add(suggestion);
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("region")) {
                // Suggesting registered regions for removal. Didn't find a way to get existing regions from WorldGuard,
                // so no suggestions on region add
                if (args[1].equalsIgnoreCase("remove")) {
                    for (String suggestion : getExistingRegions()) {
                        suggestions.add(suggestion);
                    }
                }
            }

            else if (args[0].equalsIgnoreCase("show")) {
                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
                    for (String suggestion : getOnlinePlayers()) {
                        suggestions.add(suggestion);
                    }
                }
            }
            return suggestions;
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("region") && args[1].equalsIgnoreCase("add")) {
                for (String suggestion : getExistingShows()) {
                    suggestions.add(suggestion);
                }
            }

            else if (args[0].equalsIgnoreCase("show")) {
                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
                    for (String suggestion : getExistingShows()) {
                        suggestions.add(suggestion);
                    }
                }
            }
        }

        return suggestions;
    }

    private List<String> getOnlinePlayers() {
        List<String> suggestions = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            suggestions.add(player.getName());
        }
        return suggestions;
    }

    private List<String> getExistingShows() {
        List<String> suggestions = new ArrayList<>();
        HashMap<String, Show> shows = MCJukebox.getInstance().getShowManager().getShows();
        for (String show : shows.keySet()) {
            suggestions.add("@" + show);
        }
        return suggestions;
    }

    private List<String> getExistingRegions() {
        List<String> suggestions = new ArrayList<>();
        HashMap<String, String> regions = MCJukebox.getInstance().getRegionManager().getRegions();
        for (String region : regions.keySet()) {
            suggestions.add(region);
        }
        return suggestions;
    }
}
