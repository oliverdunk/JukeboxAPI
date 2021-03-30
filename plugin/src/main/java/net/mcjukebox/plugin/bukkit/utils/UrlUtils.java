package net.mcjukebox.plugin.bukkit.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

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
            uri = getUrlWithoutParameters(url);
            if (uri == null) {
                return false;
            }
        } catch (URISyntaxException e) {
            return false;
        }

        if(!uri.isAbsolute()) return false;

        if (!uri.getScheme().equals("http") && !uri.getScheme().equals("https")) return false;

        return true;
    }

    public static boolean isDirectMediaFile(String url) {
        URI uri;
        try {
            uri = getUrlWithoutParameters(url);
            if (uri == null) {
                return false;
            }
        } catch (URISyntaxException e) {
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

    private static URI getUrlWithoutParameters(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null, // Ignore the query part of the input url
                uri.getFragment());
    }
}
