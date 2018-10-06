package net.mcjukebox.plugin.bukkit.managers.skript.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.JukeboxAPI;
import net.mcjukebox.plugin.bukkit.managers.skript.SkriptManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class EffRequestToken extends Effect {

    public static String FORMAT = "mcjukebox requestToken %player%";

    private Expression<Player> playerExpression;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mcjukebox gettoken " + playerExpression.getSingle(e);
    }

    @Override
    protected TriggerItem walk(Event e) {
        debug(e, true);
        execute(e);
        return null;
    }

    @Override
    protected void execute(final Event e) {
        final Player player = playerExpression.getSingle(e);
        Bukkit.getScheduler().runTaskAsynchronously(MCJukebox.getInstance(), new Runnable() {
            @Override
            public void run() {
                String token = JukeboxAPI.getToken(player);
                SkriptManager.tokens.put(player.getUniqueId(), token);
                Bukkit.getScheduler().runTask(MCJukebox.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (getNext() != null) {
                            TriggerItem.walk(getNext(), e);
                        }
                    }
                });
            }
        });
    }

}
