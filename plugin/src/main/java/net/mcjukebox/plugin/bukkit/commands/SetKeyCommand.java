package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetKeyCommand extends JukeboxCommand {

    @Override
    public boolean execute(CommandSender dispatcher, String[] args) {
        // We only need a single argument, which is the key to try
        if (args.length != 1) return false;

        MCJukebox.getInstance().getSocketHandler().getKeyHandler().tryKey(dispatcher, args[0]);
        dispatcher.sendMessage(ChatColor.GREEN + "Trying key...");
        return true;
    }

}
