package com.oliverdunk.jukeboxapi.utils;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import java.io.*;

public class LangUtils {

	private Plugin plugin;
	private JSONObject config;

	public LangUtils(Plugin plugin){
		this.plugin = plugin;
	}

	/**
	 * Loads the language file from the data folder, if it exists.
	 */
	public void load(){
		try {
			final File langFile = new File(plugin.getDataFolder() + "/lang.json");
			if(langFile.exists()) {
				String JSON = DataUtils.getStringFromPath(langFile.getPath());
				config = new JSONObject(JSON.toString());
			}
			if(config == null) config = new JSONObject();
			addDefaults();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Attempts to save the language file using the JSON FileWriter
	 */
	public void save(){
		if(config == null) load();
		final File langFile = new File(plugin.getDataFolder() + "/lang.json");
		if(langFile.exists()) langFile.delete();
		DataUtils.saveStringToPath(config.toString(4), langFile.getPath());
	}

	/**
	 * Returns the value associated with a particular key.
	 *
	 * @param key Key to find
	 * @return Value from the config
	 */
	public String get(String key){
		String[] elements = key.split("\\.");
		JSONObject finalParent = config;

		for(int i = 0; i < elements.length - 1; i++){
			if(!finalParent.has(elements[i])) finalParent.put(elements[i], new JSONObject());
			finalParent = finalParent.getJSONObject(elements[i]);
		}

		if(elements.length == 0 && finalParent.has(key)) return finalParent.getString(key);
		else if(finalParent.has(elements[elements.length - 1])) return finalParent.getString(elements[elements.length - 1]);
		return ChatColor.RED + "Missing Key: " + key;
	}

	/**
	 * Adds all default configuration values to the JSON Object.
	 */
	private void addDefaults(){
		addDefault("region.registered", "&aRegion registered!");
		addDefault("region.unregistered", "&aRegion unregistered!");
		addDefault("region.notregistered", "&cThat region is not registered!");

		addDefault("user._comment", "Chat colours are not supported for these values.");
		addDefault("user.openClient", "Click here to launch our custom music client.");
		addDefault("user.openHover", "Launch client");
		addDefault("user.__comment", "If using a custom domain, please do not include '/client' below.");
		addDefault("user.openDomain", "https://mcjukebox.net/client");

		addDefault("command.notOnline", "&c[user] is not currently online.");
	}

	/**
	 * Adds a particular property to the JSON Object if it is not already present.
	 *
	 * @param key Key to associate the value with
	 * @param value Value to associate the key with
	 */
	private void addDefault(String key, String value){
		String[] elements = key.split("\\.");
		JSONObject finalParent = config;

		for(int i = 0; i < elements.length - 1; i++){
			if(!finalParent.has(elements[i])) finalParent.put(elements[i], new JSONObject());
			finalParent = finalParent.getJSONObject(elements[i]);
		}

		if(!finalParent.has(key) && elements.length == 0) finalParent.put(key, value);
		else if(!finalParent.has(elements[elements.length - 1])) finalParent.put(elements[elements.length - 1], value);
	}

}
