package com.oliverdunk.jukeboxapi.api.models;

import lombok.Setter;

import com.oliverdunk.jukeboxapi.api.ResourceType;

/**
 * Represents a media object that can
 * be played on the audio client
 */
public class Media {

    //Type of resource, either SOUND or MUSIC
    @Setter private ResourceType type = ResourceType.SOUND_EFFECT;
    //URL where the media is located
    @Setter private String URL = "";
    //Volume, ranging from 0 to 100
    @Setter private int volume = 100;
    //Pan left to right, -100 to 100
    @Setter private int pan = 0;
    //If the music track should loop once complete (not applicable for sounds)
    @Setter private boolean looping = true;
    //Fade duration (seconds), currently only applicable to music (default set in admin panel)
    @Setter private int fadeDuration = -1;

    public Media(ResourceType type, String URL){
        setType(type);
        setURL(URL);
    }

    /**
     * Get the {@link ResourceType type of resource} this is
     *
     * @return the type of resource this is
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * Get the target URL where the media is located
     * <p>YouTube URLs are not supported at this time!
     *
     * @return the media's remote url
     */
    public String getURL() {
        return URL;
    }

    /**
     * Get the volume this media should be played at
     * <p>Range between 0 and 100
     *
     * @return an integer representing the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Get the pan this media should be played to
     * <p>Range between -100 and 100 (left to right)
     *
     * @return an integer representing the media's pan
     */
    public int getPan() {
        return pan;
    }

    /**
     * Should this media be played on a loop (repeatedly)
     * <p>Not supported for {@link ResourceType#SOUND_EFFECT} at this time.
     *
     * @return if the media is on a loop
     */
    public boolean isLooping() {
        return looping;
    }

    /**
     * Get how long the media should fade for, if not provided
     * then will use the default set in the <a href="https://mcjukebox.net/admin">Admin Panel</a>.
     * <p>Range between 0 and 30 (seconds)
     * <p>Not supported for {@link ResourceType#SOUND_EFFECT} at this time.
     *
     * @return an integer representing the fade duration in seconds
     */
    public int getFadeDuration() {
        return fadeDuration;
    }

}
