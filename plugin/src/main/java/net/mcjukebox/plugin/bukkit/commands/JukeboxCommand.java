package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import net.mcjukebox.plugin.bukkit.api.models.Media;
import net.mcjukebox.plugin.bukkit.managers.RegionManager;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.HashMap;

@AllArgsConstructor
public class JukeboxCommand implements CommandExecutor {

    private RegionManager regionManager;

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        //If the user does not have permission to operate MCJukebox, simply send them the URL
        if(!commandSender.hasPermission("jukeboxapi.admin")) return URL(commandSender);

        if(args.length == 2 && args[0].equalsIgnoreCase("setkey")) {
            MCJukebox.getInstance().getSocketHandler().getKeyHandler().tryKey(commandSender, args[1]);
            commandSender.sendMessage(ChatColor.GREEN + "Trying key...");
            return true;
        }

        if(MCJukebox.getInstance().getAPIKey() == null) {
            commandSender.sendMessage(ChatColor.RED + "No API Key set. Type /jukebox setkey <apikey>.");
            commandSender.sendMessage(ChatColor.DARK_RED + "You can get this key from https://www.mcjukebox.net/admin");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            if (args[1].equalsIgnoreCase("oa")) {
                int imported = regionManager.importFromOA();
                commandSender.sendMessage("" + ChatColor.GREEN + imported + " region(s) imported.");
                return true;
            } else {
                commandSender.sendMessage(ChatColor.RED + "Unknown import source.");
                return true;
            }
        }

        //Region commands
        if(args.length > 0 && args[0].equalsIgnoreCase("region")){

            //Region add command
            if(args.length == 4 && args[1].equalsIgnoreCase("add")){
                MCJukebox.getInstance().getRegionManager().addRegion(args[2], args[3]);
                MessageUtils.sendMessage(commandSender, "region.registered");
                return true;
            }

            //Region remove command
            if(args.length == 3 && args[1].equalsIgnoreCase("remove")){
                if(MCJukebox.getInstance().getRegionManager().hasRegion(args[2])){
                    MCJukebox.getInstance().getRegionManager().removeRegion(args[2]);
                    MessageUtils.sendMessage(commandSender, "region.unregistered");
                }else{
                    MessageUtils.sendMessage(commandSender, "region.notregistered");
                }
                return true;
            }

            //Region list command
            if(args.length == 2 && args[1].equalsIgnoreCase("list")) {
                commandSender.sendMessage(ChatColor.GREEN + "Registered Regions (" + regionManager.getRegions().size() + "):");
                for(String region : regionManager.getRegions().keySet()) {
                    commandSender.sendMessage("");
                    commandSender.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + region);
                    commandSender.sendMessage(ChatColor.GOLD + "URL/Show: " + ChatColor.WHITE + regionManager.getRegions().get(region));
                }
                return true;
            }
        }

        if(args.length == 4 && args[0].equalsIgnoreCase("show")){

            if(Bukkit.getPlayer(args[2]) == null) {
                MessageUtils.sendMessage(commandSender, "command.notOnline");
                return true;
            }

            Show show = MCJukebox.getInstance().getShowManager().getShow(args[3]);
            if(args[1].equalsIgnoreCase("add")) show.addMember(Bukkit.getPlayer(args[2]), false);
            else if(args[1].equalsIgnoreCase("remove")) show.removeMember(Bukkit.getPlayer(args[2]));
            else return help(commandSender);

            return true;
        }

        //Audio commands
        if(args.length >= 4){
            if(args[0].equalsIgnoreCase("stop")) return stop(commandSender, args);
            if(!args[0].equalsIgnoreCase("music") && !args[0].equalsIgnoreCase("sound")) return help(commandSender);

            JSONObject options = JSONFromArgs(args, 3);
            if(options == null) {
                commandSender.sendMessage(ChatColor.RED + "Unable to parse options as JSON.");
                return true;
            }

            return play(commandSender, args, options);
        }
        else if(args.length == 3){
            if(args[0].equalsIgnoreCase("stop")) return stop(commandSender, args);
            //Attempt to run the music or sound effect specified
            if(args[0].equalsIgnoreCase("music") | args[0].equalsIgnoreCase("sound")) return play(commandSender, args, null);
            else return help(commandSender);
        }else if(args.length == 2){
            //Attempt to stop music for the specified player
            if(args[0].equalsIgnoreCase("stop")) return stop(commandSender, args);
            else return help(commandSender);
        }
        else if(args.length == 0) return URL(commandSender);
        else return help(commandSender);
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
        sender.sendMessage("/jukebox region list");
        sender.sendMessage("/jukebox show add/remove <username> <@show>");
        sender.sendMessage("/jukebox setkey <apikey>");
        sender.sendMessage("/jukebox import <src>");
        return true;
    }

    private boolean play(CommandSender sender, String[] args, JSONObject options){
        Media media = new Media(ResourceType.SOUND_EFFECT, args[2]);
        if(args[0].equalsIgnoreCase("music")) media.setType(ResourceType.MUSIC);
        if(options != null) media.loadOptions(options);

        if(args[1].equalsIgnoreCase("@a")) {
            for(Player player : Bukkit.getOnlinePlayers()) JukeboxAPI.play(player, media);
            return true;
        }

        if(args[1].toCharArray()[0] == '@') {
            Show show = MCJukebox.getInstance().getShowManager().getShow(args[1]);
            show.play(media);
            return true;
        }

        Player playFor = Bukkit.getPlayer(args[1]);
        if(playFor == null){
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args[1]);
            MessageUtils.sendMessage(sender, "command.notOnline", findAndReplace);
            return true;
        }

        JukeboxAPI.play(playFor, media);
        return true;
    }

    private boolean stop(CommandSender sender, String[] args){
        String scope = args.length >= 3 ? args[1] : "music";
        JSONObject params = new JSONObject();

        if(args.length >= 4) {
            params = JSONFromArgs(args, 3);
            if(params == null) {
                sender.sendMessage(ChatColor.RED + "Unable to parse options as JSON.");
                return true;
            }
        }

        int fadeDuration = -1;
        String channel = "default";

        if(params.has("fadeDuration") && params.get("fadeDuration") instanceof Integer)
            fadeDuration = params.getInt("fadeDuration");
        if(params.has("channel") && params.get("channel") instanceof String)
            channel = params.getString("channel");

        if(args[args.length >= 3 ? 2 : 1].equalsIgnoreCase("@a")) {
            if(scope.equalsIgnoreCase("music")) for (Player player : Bukkit.getOnlinePlayers()) JukeboxAPI.stopMusic(player, channel, fadeDuration);
            else for (Player player : Bukkit.getOnlinePlayers()) JukeboxAPI.stopAll(player, channel, fadeDuration);
            return true;
        }

        if(args[args.length >= 3 ? 2 : 1].toCharArray()[0] == '@') {
            Show show = MCJukebox.getInstance().getShowManager().getShow(args[args.length >= 3 ? 2 : 1]);

            if(scope.equalsIgnoreCase("music")) show.stopMusic(fadeDuration);
            else show.stopAll(fadeDuration);
            return true;
        }

        Player playFor = Bukkit.getPlayer(args[args.length >= 3 ? 2 : 1]);
        if(playFor == null){
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args[1]);
            MessageUtils.sendMessage(sender, "command.notOnline", findAndReplace);
            return true;
        }

        if(scope.equalsIgnoreCase("music")) JukeboxAPI.stopMusic(playFor, channel, fadeDuration);
        else JukeboxAPI.stopAll(playFor, channel, fadeDuration);

        return true;
    }

    private JSONObject JSONFromArgs(String[] args, int startPoint) {
        StringBuilder JSON = new StringBuilder();
        for(int i = startPoint; i < args.length; i++) JSON.append(args[i]);

        try {
            return new JSONObject(JSON.toString());
        }catch(Exception ex) {
            return null;
        }
    }

}
