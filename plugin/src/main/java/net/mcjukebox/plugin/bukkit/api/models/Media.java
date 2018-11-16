package net.mcjukebox.plugin.bukkit.api.models;

import net.mcjukebox.plugin.bukkit.MCJukebox;
import net.mcjukebox.plugin.bukkit.api.ResourceType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public class Media {

    @Getter @Setter private ResourceType type = ResourceType.SOUND_EFFECT;
    @Getter @Setter private String URL = "";

    //The following options are only supported for the MUSIC resource type
    @Getter @Setter private int volume = 100;
    @Getter @Setter private boolean looping = true;
    @Getter @Setter private int fadeDuration = -1;
    @Getter @Setter private long startTime = -1;
    @Getter @Setter private String channel = "default";

    public Media(ResourceType type, String URL) {
        setType(type);
        setURL(URL);
    }

    public Media(ResourceType type, String URL, JSONObject options){
        setType(type);
        setURL(URL);

        loadOptions(options);
    }

    public void loadOptions(JSONObject options) {
        if(options.has("volume")) {
            if(options.get("volume") instanceof Integer) {
                if (options.getInt("volume") <= 100 && options.getInt("volume") >= 0) {
                    volume = options.getInt("volume");
                }
            }
        }

        if(options.has("looping")) {
            if(options.get("looping") instanceof Boolean) {
                looping = options.getBoolean("looping");
            }
        }

        if(options.has("fadeDuration")) {
            if(options.get("fadeDuration") instanceof Integer) {
                if (options.getInt("fadeDuration") <= 30 && options.getInt("fadeDuration") >= 0) {
                    fadeDuration = options.getInt("fadeDuration");
                }
            }
        }

        if(options.has("startTime")) {
            if(options.get("startTime") instanceof String && options.getString("startTime").equalsIgnoreCase("now")) {
                startTime = MCJukebox.getInstance().getTimeUtils().currentTimeMillis();
            } else if(options.get("startTime") instanceof Long || options.get("startTime") instanceof Integer) {
                startTime = options.getLong("startTime");
            }
        }

        if(options.has("channel")) {
            if(options.get("channel") instanceof String) {
                this.channel = options.getString("channel");
            }
        }
    }

}
