package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import net.mcjukebox.plugin.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ShowCommand extends JukeboxCommand {

    public ShowCommand() {
        suggestions = new HashMap<Integer, TabArgument>() {{
            put(0, new StringTabArgument(new String[] {"add", "remove"}));
            put(1, new PlayerTabArgument());
            put(2, new ShowTabArgument());
        }};
    }

    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
        if (args.length != 3) return false;

        if (Bukkit.getPlayer(args[1]) == null) {
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args[1]);
            MessageUtils.sendMessage(dispatcher, "command.notOnline", findAndReplace);
            return true;
        }

        Show show = MCJukebox.getInstance().getShowManager().getShow(args[2]);
        Player target = Bukkit.getPlayer(args[1]);

        switch (args[0]) {
            case "add":
                show.addMember(target, false);
                return true;
            case "remove":
                show.removeMember(target);
                return true;
            default:
                return false;
        }
    }

}
