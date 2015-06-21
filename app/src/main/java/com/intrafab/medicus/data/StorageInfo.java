package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.intrafab.medicus.http.RestApiConfig;

import java.util.Date;

/**
 * Created by Artemiy Terekhov on 19.04.2015.
 */
public class StorageInfo implements Parcelable {

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
    private String name;
    private boolean belongsToActiveTrip;
    private Date createdAt;
    private Date updatedAt;
    private int belongsToUser;
    private int document;

    public boolean isBelongsToActiveTrip() {
        return belongsToActiveTrip;
    }

    public void setBelongsToActiveTrip(boolean belongsToActiveTrip) {
        this.belongsToActiveTrip = belongsToActiveTrip;
    }

    public int getBelongsToUser() {
        return belongsToUser;
    }

    public void setBelongsToUser(int belongsToUser) {
        this.belongsToUser = belongsToUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getDocument() {
        return document;
    }

    public void setDocument(int document) {
        this.document = document;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StorageInfo() {
    }

    public StorageInfo(Parcel source) {
        id = source.readInt();
        type = source.readString();
        name = source.readString();
        belongsToActiveTrip = source.readInt() == 1 ? true : false;
        long _createdAt = source.readLong();
        createdAt = _createdAt == -1 ? null : new Date(_createdAt);
        long _updatedAt = source.readLong();
        updatedAt = _updatedAt == -1 ? null : new Date(_updatedAt);
        belongsToUser = source.readInt();
        document = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(belongsToActiveTrip ? 1 : 0);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
        dest.writeInt(belongsToUser);
        dest.writeInt(document);
    }

    public String getImagePath() {
        if (type.equalsIgnoreCase("JPG") || type.equalsIgnoreCase("JPEG"))
            return RestApiConfig.FILESTORAGE_HOST_URL + "/api/v1.0/fileEntity/getFile?id=" + document;

        return "";
    }
}
