package com.oliverdunk.jukeboxapi.api.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class Media {

    //Type of resource, either SOUND or MUSIC
    @Getter @Setter private ResourceType type = ResourceType.SOUND_EFFECT;
    //URL where the media is located
    @Getter @Setter private String URL = "";
    //Volume, ranging from 0 to 100
    @Getter @Setter private int volume = 100;
    //Pan left to right, -100 to 100
    @Getter @Setter private int pan = 0;

    public Media(ResourceType type, String URL){
        setType(type);
        setURL(URL);
    }

}
