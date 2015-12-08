package com.example.av.alarm_clock.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.av.alarm_clock.R;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Михаил on 08.12.2015.
 */
public class ConnectInstagramAsyncTask extends AsyncTask<String, Void, JSONObject> {
    public static final String SELF_INFO = "https://api.instagram.com/v1/users/self";
    private final String accessToken;
    private final String apiUrl;
    private final Context context;

    public ConnectInstagramAsyncTask(String accessToken, String apiUrl, Context context) {
        this.accessToken = accessToken;
        this.apiUrl = apiUrl;
        this.context = context;
    }

    @Override
    @NonNull
    protected JSONObject doInBackground(String... params) {
        JSONObject result = new JSONObject();
        try {
            URIBuilder uriBuilder = new URIBuilder(apiUrl);
            uriBuilder.addParameter("access_token", accessToken);
            URL url = uriBuilder.build().toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // TODO: Generalize the case
            connection.connect();

            int response = connection.getResponseCode();
            String message = connection.getResponseMessage();

            Log.d(this.getClass().toString(), "Code: " + response + ", message: " + message);
            result = dispatchResponse(response, message, connection);
        } catch (URISyntaxException e) {
            // Wrong API string
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // Wrong parameters? Wrong API string?
            e.printStackTrace();
        } catch (IOException e) {
            // Network error
            e.printStackTrace();
        }
        return result;
    }

    @NonNull
    private JSONObject dispatchResponse(int response, String message, HttpURLConnection connection) {
        if (response == HttpStatus.SC_OK) {
            String messageBody = getMessageBody(connection);
            try {
                JSONObject result = new JSONObject(messageBody);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        } else {
            return new JSONObject();
        }
    }

    private String getMessageBody(HttpURLConnection connection) {
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

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            if (result.has("data")) {
                JSONObject data = result.getJSONObject("data");
                String fullName = data.getString("full_name");
                Integer id = data.getInt("id");

                SharedPreferences sharedPreferences = context.getSharedPreferences(
                        context.getString(R.string.app_pref_file),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor prefereceEditor = sharedPreferences.edit();
                prefereceEditor.putString("full_name", fullName);
                prefereceEditor.putInt("id", id);
                prefereceEditor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
