package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class VolumeCommand extends JukeboxCommand {
//TODO: Enable when needed again!!!

//    public VolumeCommand(){
//        suggestions.put(0, new PlayerOrShowTabArgument(new String[] {}));
//        suggestions.put(1, new StringTabArgument(new String[] {"25","50","75","100"}));
//    }
    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
//        if (args.length <= 1) return false;
//        System.out.println(String.join(",",args));
//
//        if (args[0].startsWith("@")) {
//            JukeboxAPI.getShowManager().getShow(args[0]).changeVolume(Integer.parseInt(args[1]));
//        } else {
//            Player player = Bukkit.getPlayer(args[0]);
//            if (player != null) {
//                if(args.length == 3) {
//                    JukeboxAPI.changeVolume(player, Integer.parseInt(args[1]), args[2]);
//                } else {
//                    JukeboxAPI.changeVolume(player, Integer.parseInt(args[1]));
//
//                }
//            } else {
//                HashMap<String, String> findAndReplace = new HashMap<String, String>();
//                findAndReplace.put("user", args[1]);
//                MessageUtils.sendMessage(dispatcher, "command.notOnline", findAndReplace);
//            }
//        }
        return true;
    }
}
