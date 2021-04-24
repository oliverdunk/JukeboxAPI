package net.mcjukebox.plugin.bukkit.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {
    private static String[] supportedFiles = {
            "mp3",
            "ogg",
            "wav",
            "webm",
            "flac",
            "adts"
    };

    public static boolean isValidURI(String url)
    {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return false;
        }

        if(!uri.isAbsolute() || (!uri.getScheme().equals("http") && !uri.getScheme().equals("https"))) {
            return false;
        }

        return true;
    }

    public static boolean isDirectMediaFile(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return false;
        }

        if (uri.getPath() == null) {
            return false;
        }

        String path = uri.getPath();
        for (String fileType: supportedFiles) {
            if (path.endsWith("."+fileType)) {
                return true;
            }
        }
        return false;
    }
}
