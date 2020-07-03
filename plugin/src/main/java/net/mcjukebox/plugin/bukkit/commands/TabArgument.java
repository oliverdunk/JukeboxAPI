package net.mcjukebox.plugin.bukkit.commands;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.managers.shows.Show;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

abstract class TabArgument {

    protected List<String> suggestions = new ArrayList<>();

    public List<String> getSuggestions() {
        return this.suggestions;
    }
}

class StringTabArgument extends TabArgument {

    public StringTabArgument(String[] customSuggestions) {
        suggestions.addAll(Arrays.asList(customSuggestions));
    }
}

class RegionTabArgument extends TabArgument {

    public RegionTabArgument() {
        HashMap<String, String> regions = MCJukebox.getInstance().getRegionManager().getRegions();
        suggestions.addAll(regions.keySet());
    }
}

class PlayerTabArgument extends TabArgument {

    public PlayerTabArgument() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            suggestions.add(player.getName());
        }
    }
}

class ShowTabArgument extends TabArgument {

    public ShowTabArgument() {
        HashMap<String, Show> shows = MCJukebox.getInstance().getShowManager().getShows();
        for (String show : shows.keySet()) {
            suggestions.add("@" + show);
        }
    }
}

class PlayerOrShowTabArgument extends TabArgument {

    public PlayerOrShowTabArgument(String[] customSuggestions) {
        suggestions.addAll(Arrays.asList(customSuggestions));
        HashMap<String, Show> shows = MCJukebox.getInstance().getShowManager().getShows();
        for (String show : shows.keySet()) {
            suggestions.add("@" + show);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            suggestions.add(player.getName());
        }
    }
}