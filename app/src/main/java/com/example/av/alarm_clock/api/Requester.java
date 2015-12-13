package com.example.av.alarm_clock.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.av.alarm_clock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mikrut on 13.12.15.
 */
public class Requester {
    private final String accessToken;
    private static final String API_URL = "https://api.instagram.com/v1/";

    public Requester(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getString(R.string.app_pref_file),
                Context.MODE_PRIVATE
        );
        accessToken = sp.getString("access_token", null);
    }

    public static String getMessageBody(HttpURLConnection connection) {
        StringBuilder responseBuilder = new StringBuilder();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }

    public HashSet<Integer> getFolloweeIDs() {
        final String followeeURL = "/users/self/follows";
        final String followeeMethod = "GET";

        HashSet<Integer> ids = new HashSet<>();
        try {
            JSONObject result = getJSONResponse(API_URL + followeeURL, followeeMethod);
            if (result != null) {
                JSONArray data = result.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject followee = data.getJSONObject(i);
                    int followeeID = followee.getInt("id");
                    ids.add(followeeID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ids;
    }

    @Nullable
    private JSONObject getJSONResponse(String location, String method) throws IOException, JSONException{
        URL url = new URL(location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.connect();

        int response = connection.getResponseCode();
        if (response == HttpURLConnection.HTTP_OK) {
            String messageBody = getMessageBody(connection);
            JSONObject result = new JSONObject(messageBody);
            return result;
        }
        return null;
    }
}
