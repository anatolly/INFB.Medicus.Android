package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Artemiy Terekhov on 02.05.2015.
 */
public class Account implements Parcelable {

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private String uid;
    private String name;
    private String mail;
    private Date created;
    private Date access;
    private String status;
    private String timezone;
    private String language;
    private String picture;

    public Account() {
    }

    public Account(Parcel source) {
        uid = source.readString();
        name = source.readString();
        mail = source.readString();
        long _created = source.readLong();
        created = _created == -1 ? null : new Date(_created);
        long _access = source.readLong();
        access = _access == -1 ? null : new Date(_access);
        status = source.readString();
        timezone = source.readString();
        language = source.readString();
        picture = source.readString();
    }

    public Account(JSONObject object) {
        if (object == null)
            return;

        if (object.has("uid")) {
            uid = object.optString("uid");
        }

        if (object.has("name")) {
            name = object.optString("name");
        }

        if (object.has("mail")) {
            mail = object.optString("mail");
        }

        if (object.has("created")) {
            String createdDateString = object.optString("created");
            created = new Date(TextUtils.isEmpty(createdDateString) ? -1 : Long.parseLong(createdDateString));
        }

        if (object.has("access")) {
            String accessDateString = object.optString("access");
            access = new Date(TextUtils.isEmpty(accessDateString) ? -1 : Long.parseLong(accessDateString));
        }

        if (object.has("status")) {
            status = object.optString("status");
        }

        if (object.has("timezone")) {
            timezone = object.optString("timezone");
        }

        if (object.has("language")) {
            language = object.optString("language");
        }

        if (object.has("picture")) {
            picture = object.optString("picture");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeLong(created != null ? created.getTime() : -1);
        dest.writeLong(access != null ? access.getTime() : -1);
        dest.writeString(status);
        dest.writeString(timezone);
        dest.writeString(language);
        dest.writeString(picture);
    }
}
