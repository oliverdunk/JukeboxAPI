package com.oliverdunk.jukeboxapi.commands;

import com.oliverdunk.jukeboxapi.Jukebox;
import com.oliverdunk.jukeboxapi.api.JukeboxAPI;
import com.oliverdunk.jukeboxapi.api.ResourceType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JukeboxCommand implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        //If the user does not have permission to operate Jukebox, simply send them the URL
        if(!commandSender.hasPermission("jukeboxapi.admin")) return URL(commandSender);

        //Region commands
        if(args.length > 0 && args[0].equalsIgnoreCase("region")){
            //Region add command
            if(args.length == 4 && args[1].equalsIgnoreCase("add")){
                Jukebox.getInstance().getRegionUtils().addRegion(args[2], args[3]);
                commandSender.sendMessage(ChatColor.GREEN + "Region registered!");
                return true;
            }
            //Region remove command
            if(args.length == 3 && args[1].equalsIgnoreCase("remove")){
                if(Jukebox.getInstance().getRegionUtils().hasRegion(args[2])){
                    Jukebox.getInstance().getRegionUtils().removeRegion(args[2]);
                    commandSender.sendMessage(ChatColor.GREEN + "Region unregistered.");
                }else{
                    commandSender.sendMessage(ChatColor.RED + "That region is not registered.");
                }
                return true;
            }
        }

        //Audio commands
        if(args.length == 3){
            //Attempt to run the music or sound effect specified
            if(args[0].equalsIgnoreCase("music") | args[0].equalsIgnoreCase("sound")) return play(commandSender, args);
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
        sender.sendMessage("/jukebox music <username> <url>");
        sender.sendMessage("/jukebox sound <username> <url>");
        sender.sendMessage("/jukebox region add <id> <url>");
        sender.sendMessage("/jukebox region remove <id>");
        return true;
    }

    private boolean URL(CommandSender sender){
        if(sender instanceof Player) {
            String URL = "https://mcjukebox.net/client?username=" + sender.getName() + "&server=" + Jukebox.getInstance().getId();
            TextComponent message = new TextComponent("Click here to launch our custom music client.");
            message.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Launch client").create()));
            ((Player) sender).spigot().sendMessage(message);
        }else {
            help(sender);
        }
        return true;
    }

    private boolean play(CommandSender sender, String[] args){
        Player playFor = Bukkit.getPlayer(args[1]);
        if(playFor == null){
            sender.sendMessage(ChatColor.RED + args[1] + " is not currently online.");
            return true;
        }
        if(args[0].equalsIgnoreCase("music")) JukeboxAPI.play(playFor, args[2], ResourceType.MUSIC);
        else JukeboxAPI.play(playFor, args[2], ResourceType.SOUND_EFFECT);
        return true;
    }

    private boolean stop(CommandSender sender, String[] args){
        Player playFor = Bukkit.getPlayer(args[1]);
        if(playFor == null){
            sender.sendMessage(ChatColor.RED + args[1] + " is not currently online.");
            return true;
        }
        JukeboxAPI.stopMusic(playFor);
        return true;
    }

}
