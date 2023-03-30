package net.mcjukebox.plugin.bukkit.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.LangManager;
import org.bukkit.entity.Player;

public class SpigotUtils {

	public static void URL(Player player, LangManager langManager, String token){
		String URL = langManager.get("user.openDomain") + "?token=" + token;
		TextComponent message = Component.text()
				.append(LegacyComponentSerializer.legacy('&').deserialize(langManager.get("user.openClient")))
				.clickEvent(ClickEvent.openUrl(URL))
				.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacy('&').deserialize(langManager.get("user.openClient")))).build();
		MCJukebox.getInstance().adventure().player(player).sendMessage(message);
	}

}
