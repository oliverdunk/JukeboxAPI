package com.oliverdunk.jukeboxapi.utils;

import com.oliverdunk.jukeboxapi.Jukebox;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpigotUtils {

	public void URL(CommandSender sender, LangUtils langUtils){
		String URL = langUtils.get("user.openDomain") + "?username=" + sender.getName() + "&server=" + Jukebox.getInstance().getId();
		TextComponent message = new TextComponent(langUtils.get("user.openClient"));
		message.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(langUtils.get("user.openHover")).create()));
		((Player) sender).spigot().sendMessage(message);
	}

}
