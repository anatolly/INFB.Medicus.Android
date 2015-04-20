package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StorageInfo implements Parcelable {

    public final static String STORAGE_ID = "id";
    public final static String STORAGE_TYPE = "type";
    public final static String STORAGE_DESCRIPTION = "description";
    public final static String STORAGE_THUMBNAIL = "thumbnail";
    public final static String STORAGE_TIMESTAMP = "timestamp";
    public final static String STORAGE_ACCESS_URL = "access_url";
    public final static String STORAGE_ACCESS_SESSION_ID = "access_session_id";

    public static final Creator<StorageInfo> CREATOR = new Creator<StorageInfo>() {
        @Override
        public StorageInfo createFromParcel(Parcel source) {
            return new StorageInfo(source);
        }

        @Override
        public StorageInfo[] newArray(int size) {
            return new StorageInfo[size];
        }
    };

    private int id;
    private String type;
    private String description;
    private String thumbnail;
    private long timestamp;
    private String access_url;
    private String access_session_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccess_url() {
        return access_url;
    }

    public void setAccess_url(String access_url) {
        this.access_url = access_url;
    }

    public String getAccess_session_id() {
        return access_session_id;
    }

    public void setAccess_session_id(String access_session_id) {
        this.access_session_id = access_session_id;
    }

    public StorageInfo() {
    }

    public StorageInfo(Parcel source) {
        id = source.readInt();
        type = source.readString();
        description = source.readString();
        thumbnail = source.readString();
        timestamp = source.readLong();
        access_url = source.readString();
        access_session_id = source.readString();
    }

    public StorageInfo(JSONObject object) {
        if (object == null)
            return;

        if (object.has(STORAGE_ID)) {
            this.id = object.optInt(STORAGE_ID);
        }

        if (object.has(STORAGE_TYPE)) {
            this.type = object.optString(STORAGE_TYPE);
        }

        if (object.has(STORAGE_DESCRIPTION)) {
            this.description = object.optString(STORAGE_DESCRIPTION);
        }

        if (object.has(STORAGE_THUMBNAIL)) {
            this.thumbnail = object.optString(STORAGE_THUMBNAIL);
        }

        if (object.has(STORAGE_TIMESTAMP)) {
            this.timestamp = object.optLong(STORAGE_TIMESTAMP);
        }

        if (object.has(STORAGE_ACCESS_URL)) {
            this.access_url = object.optString(STORAGE_ACCESS_URL);
        }

        if (object.has(STORAGE_ACCESS_SESSION_ID)) {
            this.access_session_id = object.optString(STORAGE_ACCESS_SESSION_ID);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeLong(timestamp);
        dest.writeString(access_url);
        dest.writeString(access_session_id);
    }
}
