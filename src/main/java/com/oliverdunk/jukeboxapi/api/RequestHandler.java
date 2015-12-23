package com.oliverdunk.jukeboxapi.api;

import com.oliverdunk.jukeboxapi.Jukebox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHandler {

    //The URL which requests should be made to
    private static final String API_URL = "https://oliverdunk.com/jukebox/api/v1/";
    //The API key which should be used to authenticate requests
    private static String API_Key;

    /**
     * Make a request to the Jukebox API.
     * @param method The method of the API to call.
     * @param parameters Any parameters which will be passed as GET parameters to the API.
     * @return A JSONObject containing the response from the server.
     */
    protected static APIResponse makeRequest(String method, Map<String, String> parameters){
        try {
            //Build URL from method and parameters
            StringBuilder URLString = new StringBuilder(API_URL + method + "?");
            for (String key : parameters.keySet()) URLString.append(key + "=" + parameters.get(key) + "&");
            URLString.append("api_key=" + API_Key);
            URL URL = new URL(URLString.toString());

            //Attempt a connection
            HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            InputStream inputStream = connection.getInputStream();

            //Get first line of response and return it as a JSONObject
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return APIResponse.valueOf(new JSONObject(reader.readLine()).getString("status"));

            //Handle possible exceptions
        }catch(MalformedURLException exception) {
            Jukebox.getInstance().getLogger().info("The API URL in use is malformed.");
            return APIResponse.FAILURE;
        }catch(IOException exception){
            exception.printStackTrace();
            Jukebox.getInstance().getLogger().info("Unable to connect to the Jukebox API.");
            return APIResponse.FAILURE;
        }
    }

    /**
     * Sets the API key which should be used for authenticating API calls.
     * @param API_Key An API key collected from https://www.oliverdunk.com/jukebox/
     */
    public static void setAPI_Key(String API_Key) {
        RequestHandler.API_Key = API_Key;
    }
}
