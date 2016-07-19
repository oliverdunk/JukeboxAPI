package net.mcjukebox.plugin.bukkit.utils;

import net.mcjukebox.plugin.bukkit.managers.LangManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class SpigotUtils {

	public static void URL(Player player, LangManager langManager, String token){
		String URL = langManager.get("user.openDomain") + "?token=" + token;

		TextComponent message = new TextComponent(TextComponent.fromLegacyText(langManager.get("user.openClient")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(langManager.get("user.openHover"))));
		player.spigot().sendMessage(message);
	}

}
