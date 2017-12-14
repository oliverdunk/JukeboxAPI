package net.mcjukebox.plugin.bukkit.utils;

import lombok.Setter;
import net.mcjukebox.plugin.bukkit.managers.LangManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MessageUtils {

	@Setter private static LangManager langManager;

	/**
	 * Sends a message to the given player.
	 *
	 * @param player Player who the message should be sent to
	 * @param key Key which should be used to lookup the message
	 */
	public static void sendMessage(CommandSender player, String key){
		sendMessage(player, key, null);
	}

	/**
	 * Sends a message to the given player, replacing particular keys.
	 *
	 * @param player Player who the message should be sent to
	 * @param key Key which should be used to lookup the message
	 * @param findAndReplace Optional list of keys which should be replaced with the corresponding values
	 */
	public static void sendMessage(CommandSender player, String key, HashMap<String, String> findAndReplace){
		String message = langManager.get(key);

		//Don't send message if the localisation is blank
		if(message.trim().equalsIgnoreCase("")) return;

		//Replace any values in the find and replace HashMap, if it is present
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (findAndReplace != null) {
			for (String find : findAndReplace.keySet()) message = message.replace("[" + find + "]", findAndReplace.get(find));
		}

		player.sendMessage(message);
	}

	public static void sendURL(Player player, String token){
		if(isSpigot()){
			new SpigotUtils().URL(player, langManager, token);
		}else{
			String URL = langManager.get("user.openDomain") + "?token=" + token;
			player.sendMessage(ChatColor.GOLD + langManager.get("user.openClient"));
			player.sendMessage(ChatColor.GOLD + URL);
		}
	}

	private static boolean isSpigot(){
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
