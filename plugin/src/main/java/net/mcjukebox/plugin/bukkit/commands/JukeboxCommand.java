package net.mcjukebox.plugin.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class JukeboxCommand {

    private final String SELECTOR_REGEX = "@[ap]\\[r=([0-9]{1,4})\\]";
    protected HashMap<Integer, TabArgument> suggestions = new HashMap<>();

    public abstract boolean execute(CommandSender dispatcher, String[] args);

    public boolean executeWithSelectors(CommandSender dispatcher, String[] args) {
        int targetIndex = -1;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("@a")) targetIndex = i;
            if (args[i].equalsIgnoreCase("@p")) targetIndex = i;
            if (args[i].matches(SELECTOR_REGEX)) targetIndex = i;
        }

        // If all selectors in this command have been replaced
        if (targetIndex == -1) {
            return execute(dispatcher, args);
        }

        // We still have selectors to parse
        String selector = args[targetIndex];
        String[] newArgs = Arrays.copyOf(args, args.length);
        Location dispatchLocation = null;

        if (dispatcher instanceof BlockCommandSender) {
            dispatchLocation = ((BlockCommandSender) dispatcher).getBlock().getLocation();
        } else if (dispatcher instanceof Entity) {
            dispatchLocation = ((Entity) dispatcher).getLocation();
        }

        List<Player> targets = new ArrayList<Player>();

        if (selector.toLowerCase().startsWith("@a")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                targets.add(player);
            }
        }

        if (selector.toLowerCase().startsWith("@p")) {
            if (dispatchLocation == null) {
                dispatcher.sendMessage(ChatColor.RED + "@p cannot be used here.");
                return true;
            }
            Player nearestPlayer = getNearestPlayer(dispatchLocation);
            if (nearestPlayer != null) {
                targets.add(nearestPlayer);
            }
        }

        boolean failed = false;

        for (Player player : targets) {
            if (isInRange(dispatchLocation, player, selector)) {
                newArgs[targetIndex] = player.getName();
                boolean result = execute(dispatcher, newArgs);
                if (!result) failed = true;
            }
        }

        return !failed;
    }

    private Player getNearestPlayer(Location location) {
        double nearestDistance = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Entity entity : location.getWorld().getNearbyEntities(location, 1000, 1000, 1000)) {
            if (entity instanceof Player) {
                Player candidate = (Player) entity;
                if (candidate.getLocation().distanceSquared(location) < nearestDistance) {
                    nearestDistance = candidate.getLocation().distance(location);
                    nearestPlayer = candidate;
                }
            }
        }

        return nearestPlayer;
    }

    private boolean isInRange(Location from, Player target, String selector) {
        if (selector.length() == 2) return true;
        if (from.getWorld() != target.getWorld()) return false;

        Matcher matcher = Pattern.compile(SELECTOR_REGEX, Pattern.CASE_INSENSITIVE).matcher(selector);
        matcher.find();
        int radius = Integer.parseInt(matcher.group(1));

        return from.distance(target.getLocation()) <= radius;
    }

    protected JSONObject jsonFromArgs(String[] args, int startPoint) {
        StringBuilder json = new StringBuilder();
        for(int i = startPoint; i < args.length; i++) json.append(args[i]);

        try {
            return new JSONObject(json.toString());
        }catch(Exception ex) {
            return null;
        }
    }

    public HashMap<Integer, TabArgument> getSuggestions() {
        return this.suggestions;
    }
}
