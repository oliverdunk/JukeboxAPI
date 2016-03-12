package com.oliverdunk.jukeboxapi.utils;

import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Optional;

public class MessageUtils {

	@Setter private static LangUtils langUtils;

	/**
	 * Sends a message to the given player.
	 *
	 * @param player Player who the message should be sent to
	 * @param key Key which should be used to lookup the message
	 */
	public static void sendMessage(CommandSender player, String key){
		sendMessage(player, key, Optional.<HashMap<String,String>>empty());
	}

	/**
	 * Sends a message to the given player, replacing particular keys.
	 *
	 * @param player Player who the message should be sent to
	 * @param key Key which should be used to lookup the message
	 * @param findAndReplace Optional list of keys which should be replaced with the corresponding values
	 */
	public static void sendMessage(CommandSender player, String key, Optional<HashMap<String, String>> findAndReplace){
		String message = langUtils.get(key);

		//Replace any values in the find and replace HashMap, if it is present
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (findAndReplace.isPresent()) {
			HashMap<String, String> findReplace = findAndReplace.get();
			for (String find : findReplace.keySet()) message = message.replace("[" + find + "]", findReplace.get(find));
		}

		player.sendMessage(message);
	}

}
