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
    private static final String API_URL = "https://mcjukebox.net/api/v1/";
    //The API key which should be used to authenticate requests
    private String APIKey;

    public RequestHandler(String APIKey){
        this.APIKey = APIKey;
    }

    /**
     * Make a request to the Jukebox API.
     *
     * @param method The method of the API to call.
     * @param parameters Any parameters which will be passed as GET parameters to the API.
     * @return A JSONObject containing the response from the server.
     */
    protected APIResponse makeRequest(String method, Map<String, String> parameters){
        try {
            //Build URL from method and parameters
            StringBuilder URLString = new StringBuilder(API_URL + method + "?");
            for (String key : parameters.keySet()) {
                URLString.append(key).append("=").append(parameters.get(key)).append("&");
            }
            URLString.append("api_key=").append(APIKey);
            URL URL = new URL(URLString.toString());

            //Attempt a connection
            HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            InputStream inputStream = connection.getInputStream();

            //Get first line of response and return it as a JSONObject
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String result = new JSONObject(reader.readLine()).getString("status");

            //Close connections
            reader.close();
            connection.disconnect();

            return APIResponse.valueOf(result);

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
     * @param APIKey An API key collected from https://mcjukebox.net/admin/
     */
    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

}
