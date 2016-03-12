package com.oliverdunk.jukeboxapi.utils;

import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class MessageUtils {

	@Setter private static LangUtils langUtils;

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
		String message = langUtils.get(key);

		//Replace any values in the find and replace HashMap, if it is present
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (findAndReplace != null) {
			for (String find : findAndReplace.keySet()) message = message.replace("[" + find + "]", findAndReplace.get(find));
		}

		player.sendMessage(message);
	}

}
