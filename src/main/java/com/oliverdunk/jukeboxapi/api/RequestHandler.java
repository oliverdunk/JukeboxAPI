package com.oliverdunk.jukeboxapi.api;

import com.oliverdunk.jukeboxapi.Jukebox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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
    protected static JSONObject makeRequest(String method, HashMap<String, String> parameters){
        try {
            //Build URL from method and parameters
            StringBuilder URLString = new StringBuilder(API_URL + method + "?");
            for (String key : parameters.keySet()) URLString.append(key + "=" + parameters.get(key) + "&");
            URLString.append("api_key=" + API_Key);
            URL URL = new URL(URLString.toString());

            //Attempt a connection
            HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
            InputStream inputStream = connection.getInputStream();

            //Get first line of response and return it as a JSONObject
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return new JSONObject(reader.readLine());
        }catch(Exception ex){
            //Something went wrong - throw the exception and return a failure.
            Jukebox.getInstance().getLogger().info("An error occurred while making a request to Jukebox's server...");
            ex.printStackTrace();
            return new JSONObject().put("status", "FAILURE");
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
