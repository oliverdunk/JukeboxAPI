package com.oliverdunk.jukeboxapi.commands;

import com.oliverdunk.jukeboxapi.Jukebox;
import com.oliverdunk.jukeboxapi.api.JukeboxAPI;
import com.oliverdunk.jukeboxapi.api.ResourceType;
import com.oliverdunk.jukeboxapi.api.models.Media;
import com.oliverdunk.jukeboxapi.utils.LangUtils;
import com.oliverdunk.jukeboxapi.utils.MessageUtils;
import com.oliverdunk.jukeboxapi.utils.RegionUtils;
import com.oliverdunk.jukeboxapi.utils.SpigotUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@AllArgsConstructor
public class JukeboxCommand implements CommandExecutor {

    private LangUtils langUtils;
    private RegionUtils regionUtils;

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        //If the user does not have permission to operate Jukebox, simply send them the URL
        if(!commandSender.hasPermission("jukeboxapi.admin")) return URL(commandSender);

        //Region commands
        if(args.length > 0 && args[0].equalsIgnoreCase("region")){

            //Region add command
            if(args.length == 4 && args[1].equalsIgnoreCase("add")){
                Jukebox.getInstance().getRegionUtils().addRegion(args[2], args[3]);
                MessageUtils.sendMessage(commandSender, "region.registered");
                return true;
            }

            //Region remove command
            if(args.length == 3 && args[1].equalsIgnoreCase("remove")){
                if(Jukebox.getInstance().getRegionUtils().hasRegion(args[2])){
                    Jukebox.getInstance().getRegionUtils().removeRegion(args[2]);
                    MessageUtils.sendMessage(commandSender, "region.unregistered");
                }else{
                    MessageUtils.sendMessage(commandSender, "region.notregistered");
                }
                return true;
            }

            //Region list command
            if(args.length == 2 && args[1].equalsIgnoreCase("list")) {
                commandSender.sendMessage(ChatColor.GREEN + "Registered Regions (" + regionUtils.getRegions().size() + "):");
                for(String region : regionUtils.getRegions().keySet()) {
                    commandSender.sendMessage("");
                    commandSender.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + region);
                    commandSender.sendMessage(ChatColor.GOLD + "URL: " + ChatColor.WHITE + regionUtils.getRegions().get(region));
                }
                return true;
            }
        }

        //Audio commands
        if(args.length == 5) {
            if(args[0].equalsIgnoreCase("music")) {
                try{
                    int fade = Integer.parseInt(args[3]);
                    boolean looping = Boolean.parseBoolean(args[4].toLowerCase());
                    return play(commandSender, args, fade, looping);
                }catch(Exception ex){
                    commandSender.sendMessage(ChatColor.RED + "Invalid fade duration.");
                    return true;
                }
            }
        }
        if(args.length == 4){
            if(args[0].equalsIgnoreCase("music")) {
                try{
                    int fade = Integer.parseInt(args[3]);
                    return play(commandSender, args, fade, false);
                }catch(Exception ex){
                    commandSender.sendMessage(ChatColor.RED + "Invalid fade duration.");
                    return true;
                }
            }
        }
        if(args.length == 3){
            //Attempt to run the music or sound effect specified
            if(args[0].equalsIgnoreCase("music") | args[0].equalsIgnoreCase("sound")) return play(commandSender, args, -1, true);
            else return help(commandSender);
        }else if(args.length == 2){
            //Attempt to stop music for the specified player
            if(args[0].equalsIgnoreCase("stop")) return stop(commandSender, args);
            else return help(commandSender);
        }

        //Unknown command - either default to the URL or send an error message
        else if(args.length == 0) return URL(commandSender);
        else return help(commandSender);
    }

    private boolean help(CommandSender sender){
        sender.sendMessage(ChatColor.GREEN + "Jukebox Commands:");
        sender.sendMessage("/jukebox music <username> <url> <fadeDuration> <looping>");
        sender.sendMessage("/jukebox sound <username> <url>");
        sender.sendMessage("/jukebox region add <id> <url>");
        sender.sendMessage("/jukebox region remove <id>");
        sender.sendMessage("/jukebox region list");
        sender.sendMessage("/jukebox stop <username>");
        return true;
    }

    private boolean isSpigot(){
        try {
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private boolean URL(CommandSender sender){
        if(sender instanceof Player) {
            if(isSpigot()){
                new SpigotUtils().URL(sender, langUtils);
            }else{
                String URL = langUtils.get("user.openDomain") + "?username=" + sender.getName() + "&server=" + Jukebox.getInstance().getId();
                sender.sendMessage(ChatColor.GOLD + langUtils.get("user.openClient"));
                sender.sendMessage(ChatColor.GOLD + URL);
            }
        }else {
            help(sender);
        }
        return true;
    }

    private boolean play(CommandSender sender, String[] args, int fadeDuration, boolean looping){
        Media media;
        if(args[0].equalsIgnoreCase("music")) media = new Media(ResourceType.MUSIC, args[2]);
        else media = new Media(ResourceType.SOUND_EFFECT, args[2]);
        media.setFadeDuration(fadeDuration);
        media.setLooping(looping);

        if(args[1].equalsIgnoreCase("@a")) {
            for(Player player : Bukkit.getOnlinePlayers()) JukeboxAPI.play(player, media);
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
        Player playFor = Bukkit.getPlayer(args[1]);
        if(playFor == null){
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args[1]);
            MessageUtils.sendMessage(sender, "command.notOnline", findAndReplace);
            return true;
        }
        JukeboxAPI.stopMusic(playFor);
        return true;
    }

}
