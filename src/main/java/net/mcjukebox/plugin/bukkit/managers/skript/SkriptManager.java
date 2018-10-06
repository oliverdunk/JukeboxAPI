package net.mcjukebox.plugin.bukkit.managers.skript;

import ch.njol.skript.Skript;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.skript.effects.EffRequestToken;
import net.mcjukebox.plugin.bukkit.managers.skript.expressions.ExprGetToken;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkriptManager {

    public static Map<UUID, String> tokens = new HashMap<UUID, String>();

    public SkriptManager() {
        Skript.registerEffect(EffRequestToken.class, EffRequestToken.FORMAT);
        ExprGetToken.register(ExprGetToken.class, String.class, "mcjukebox_token", "player");
        MCJukebox.getInstance().getLogger().info("Skript integration enabled!");
    }

}
