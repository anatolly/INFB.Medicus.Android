package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Artemiy Terekhov on 01.06.2015.
 */
public class ServiceSet implements Parcelable {

    public static final Creator<ServiceSet> CREATOR = new Creator<ServiceSet>() {
        @Override
        public ServiceSet createFromParcel(Parcel source) {
            return new ServiceSet(source);
        }

        @Override
        public ServiceSet[] newArray(int size) {
            return new ServiceSet[size];
        }
    };

    private String id;
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceSet() {
    }

    public ServiceSet(Parcel source) {
        id = source.readString();
        uri = source.readString();
    }

    public ServiceSet(JSONObject object) {
        if (object == null)
            return;

        if (object.has("id")) {
            this.id = object.optString("id");
        }

        if (object.has("uri")) {
            this.uri = object.optString("uri");
        }
    }

    public JSONObject toJson() throws JSONException {
        JSONObject target = new JSONObject();
        target.put("id", id);
        target.put("uri", uri);

        return target;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uri);
    }
}
