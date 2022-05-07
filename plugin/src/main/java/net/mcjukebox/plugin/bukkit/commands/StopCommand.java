package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.HashMap;

public class StopCommand extends JukeboxCommand {

    public StopCommand() {
        suggestions.put(0, new PlayerOrShowTabArgument(new String[] {"music", "all"}));
        suggestions.put(1, new PlayerOrShowTabArgument(new String[] {}));
    }

    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
        if (args.length == 0) return false;
        JSONObject options = new JSONObject();

        if (args.length >= 3) {
            options = jsonFromArgs(args, 2);

            if (options == null) {
                dispatcher.sendMessage(ChatColor.RED + "Unable to parse options as JSON.");
                return true;
            }
        }

        int fadeDuration = options.has("fadeDuration") ? options.getInt("fadeDuration") : -1;
        String channel = options.has("channel") ? options.getString("channel") : "default";
        String selection = args.length >= 2 ? args[0] : "music";
        int targetIndex = args.length == 1 ? 0 : 1;

        // Stop music in a show
        if (args[targetIndex].startsWith("@") && selection.equalsIgnoreCase("all")) {
            JukeboxAPI.getShowManager().getShow(args[targetIndex]).stopAll(fadeDuration);
            return true;
        }

        // Stop everything in a show
        if (args[targetIndex].startsWith("@") && selection.equalsIgnoreCase("music")) {
            JukeboxAPI.getShowManager().getShow(args[targetIndex]).stopMusic(fadeDuration);
            return true;
        }

        // We haven't encountered either show case, so assume a player is the target

        if (args.length == 1) {
            // We weren't provided a player name
            return false;
        }

        Player target = Bukkit.getPlayer(args[targetIndex]);

        if (target == null) {
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args[1]);
            MessageUtils.sendMessage(dispatcher, "command.notOnline", findAndReplace);
            return true;
        }

        // Stop music for a particular player
        if (selection.equalsIgnoreCase("all")) {
            JukeboxAPI.stopAll(target, channel, fadeDuration);
            return true;
        }

        // Stop everything for a particular player
        if (selection.equalsIgnoreCase("music")) {
            JukeboxAPI.stopMusic(target, channel, fadeDuration);
            return true;
        }

        return true;
    }

}
