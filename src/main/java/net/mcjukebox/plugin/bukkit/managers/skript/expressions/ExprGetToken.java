package net.mcjukebox.plugin.bukkit.managers.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.mcjukebox.plugin.bukkit.managers.skript.SkriptManager;
import org.bukkit.entity.Player;

public class ExprGetToken extends SimplePropertyExpression<Player, String> {

    @Override
    protected String getPropertyName() {
        return "mcjukebox_token";
    }

    @Override
    public String convert(Player player) {
        return SkriptManager.tokens.get(player.getUniqueId());
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

}
