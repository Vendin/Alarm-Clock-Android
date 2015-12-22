package com.example.av.alarm_clock.api;

import com.example.av.alarm_clock.models.ImageFile;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Михаил on 19.12.2015.
 */
public class RemoteImage extends ImageFile {
    private String photoURL;
    private String creatorID;

    @Nullable
    public static RemoteImage fromJSONObject(JSONObject imageObject) throws JSONException {
        String type = imageObject.getString("type");

        RemoteImage imageFile = null;
        if (type.equals("image")) {
            String imageMediaID = imageObject.getString("id");
            String imageLink    = imageObject.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
            String creatorID    = imageObject.getJSONObject("user").getString("id");

            imageFile = new RemoteImage();
            imageFile.setMediaId(imageMediaID);
            imageFile.setPhotoURL(imageLink);
            imageFile.setCreatorID(creatorID);
        }

        return imageFile;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }
}
