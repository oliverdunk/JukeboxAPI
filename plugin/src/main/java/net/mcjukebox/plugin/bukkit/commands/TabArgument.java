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

    @Override
    public List<String> getSuggestions() {
        HashMap<String, String> regions = MCJukebox.getInstance().getRegionManager().getRegions();
        suggestions.addAll(regions.keySet());
        return this.suggestions;
    }
}

class PlayerTabArgument extends TabArgument {

    @Override
    public List<String> getSuggestions() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            suggestions.add(player.getName());
        }
        return this.suggestions;
    }
}

class ShowTabArgument extends TabArgument {

    @Override
    public List<String> getSuggestions() {
        HashMap<String, Show> shows = MCJukebox.getInstance().getShowManager().getShows();
        for (String show : shows.keySet()) {
            suggestions.add("@" + show);
        }
        return this.suggestions;
    }
}

class PlayerOrShowTabArgument extends TabArgument {

    public PlayerOrShowTabArgument(String[] customSuggestions) {
        suggestions.addAll(Arrays.asList(customSuggestions));
    }

    @Override
    public List<String> getSuggestions() {
        HashMap<String, Show> shows = MCJukebox.getInstance().getShowManager().getShows();
        for (String show : shows.keySet()) {
            suggestions.add("@" + show);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            suggestions.add(player.getName());
        }
        return this.suggestions;
    }
}