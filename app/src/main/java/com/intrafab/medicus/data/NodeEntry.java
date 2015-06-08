package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Artemiy Terekhov on 08.06.2015.
 */
public class NodeEntry implements Parcelable {

    public static final Creator<NodeEntry> CREATOR = new Creator<NodeEntry>() {
        @Override
        public NodeEntry createFromParcel(Parcel source) {
            return new NodeEntry(source);
        }

        @Override
        public NodeEntry[] newArray(int size) {
            return new NodeEntry[size];
        }
    };

    private String title;
    private String type;
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NodeEntry() {
    }

    public NodeEntry(Parcel source) {
        title = source.readString();
        type = source.readString();
        lang = source.readString();
    }

    public NodeEntry(JSONObject object) {
        if (object == null)
            return;

        if (object.has("title")) {
            this.title = object.optString("title");
        }

        if (object.has("language")) {
            this.lang = object.optString("language");
        } else {
            this.lang = "und";
        }

        if (object.has("field_service_type")) {
            JSONObject body = object.optJSONObject("field_service_type");
            if (body != null && body.has(this.lang)) {
                JSONArray langs = body.optJSONArray(this.lang);
                if (langs != null && langs.length() > 0) {
                    JSONObject currentLang = langs.optJSONObject(0);
                    if (currentLang != null && currentLang.has("value")) {
                        this.type = currentLang.optString("value");
                    }
                }
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(lang);
    }
}
