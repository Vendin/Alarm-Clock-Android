package com.example.av.alarm_clock.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mikrut on 13.12.15.
 */
public class Requester {
    private final String accessToken;
    private Context context;
    private static final String API_URL = "https://api.instagram.com/v1/";

    public Requester(Context context) {
        this.context = context;
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

    public LinkedHashSet<String> getFolloweeIDs() {
        final String followeeURL = "users/self/follows";
        final String followeeMethod = "GET";

        LinkedHashSet<String> ids = new LinkedHashSet<>();
        try {
            JSONObject result = getJSONResponse(API_URL + followeeURL, followeeMethod);
            if (result != null) {
                JSONArray data = result.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject followee = data.getJSONObject(i);
                    String followeeID = followee.getString("id");
                    ids.add(followeeID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ids;
    }

    public LinkedList<ImageFile> getFolloweePhotos(String fweeID, int photoNum) {
        final String urlTemplate = "users/%s/media/recent";
        final String method = "GET";

        LinkedList<ImageFile> imageFiles = new LinkedList<>();
        try {
            JSONObject result = getJSONResponse(API_URL + String.format(urlTemplate, fweeID),
                    method);
            if (result != null) {
                Log.d(this.getClass().getCanonicalName(), result.toString());

                JSONArray data = result.getJSONArray("data");
                for (int i = 0; i < data.length() && imageFiles.size() < photoNum; i++) {
                    JSONObject imageObject = data.getJSONObject(i);
                    ImageFile imageFile = ImageFile.fromJSONObject(imageObject);
                    if (imageFile != null) {
                        imageFile.setFriendly(true);
                        imageFiles.add(imageFile);
                    }
                }
            }
        } catch (IOException | JSONException | URISyntaxException e) {
            Log.e(Requester.class.getCanonicalName(), e.getLocalizedMessage());
        }

        return imageFiles;
    }

    public LinkedList<ImageFile> getOtherPhotos(int neededNum, int maxTryNum) {
        final String othersUrl = "media/search";
        final String method = "GET";

        final double moscowLat = 55.7522200;
        final double moscowLng = 37.6155600;

        LinkedList<ImageFile> imageFiles = new LinkedList<>();
        try {
            URIBuilder uriBuilder = new URIBuilder(API_URL + othersUrl);
            uriBuilder.addParameter("lat", String.valueOf(moscowLat));
            uriBuilder.addParameter("lng", String.valueOf(moscowLng));
            uriBuilder.addParameter("access_token", accessToken);

            JSONObject response = getJSONResponse(uriBuilder.build().toURL(), method);
            if (response != null) {
                Log.d(this.getClass().getCanonicalName(), response.toString());

                JSONArray data = response.getJSONArray("data");
                for (int i = 0; i < maxTryNum && i < data.length() && imageFiles.size() < neededNum; i++) {
                    JSONObject imageObject = data.getJSONObject(i);
                    ImageFile imageFile = ImageFile.fromJSONObject(imageObject);
                    if (imageFile != null) {
                        imageFile.setFriendly(false);
                        imageFiles.add(imageFile);
                    }
                }
            } else {
                Log.e(this.getClass().getCanonicalName(), "Received null response");
            }
        } catch (IOException | JSONException | URISyntaxException e) {
            Log.e(Requester.class.getCanonicalName(), e.getLocalizedMessage());
        }

        return imageFiles;
    }

    public boolean downloadPhoto(ImageFile photo) {
        boolean downloaded = false;
        try {
            final String uri = photo.getPhotoURL();
            final String extension = uri.substring(uri.lastIndexOf(".") + 1);;

            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            File outputFile = new File(ImageFile.getImagesFolder(context),
                    photo.getMediaId() + "." + extension);
            if (outputFile.exists())
                outputFile.delete();
            outputFile.createNewFile();
            OutputStream output = new FileOutputStream(outputFile);

            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            downloaded = true;
            photo.setFilename(outputFile.getName());

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return downloaded;
    }

    private JSONObject getJSONResponse(URL url, String method) throws IOException, JSONException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.connect();

        int response = connection.getResponseCode();
        if (response == HttpURLConnection.HTTP_OK) {
            String messageBody = getMessageBody(connection);
            JSONObject result = new JSONObject(messageBody);
            return result;
        } else {
            Log.e(Requester.class.getCanonicalName(), connection.getResponseMessage());
            Log.e(Requester.class.getCanonicalName(), getMessageBody(connection));
        }
        return null;
    }

    private JSONObject getJSONResponse(String location, String method) throws IOException, JSONException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(location);
        uriBuilder.addParameter("access_token", accessToken);
        return getJSONResponse(uriBuilder.build().toURL(), method);
    }
}
