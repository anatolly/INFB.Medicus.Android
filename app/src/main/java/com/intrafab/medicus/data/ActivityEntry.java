package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Artemiy Terekhov on 01.05.2015.
 */
public class ActivityEntry implements Parcelable {

    public static final Creator<ActivityEntry> CREATOR = new Creator<ActivityEntry>() {
        @Override
        public ActivityEntry createFromParcel(Parcel source) {
            return new ActivityEntry(source);
        }

        @Override
        public ActivityEntry[] newArray(int size) {
            return new ActivityEntry[size];
        }
    };

    private String id;
    private Date stateStart;
    private Date stateEnd;
    private String stateTitle;
    private String stateDescription;
    private String stateType;
    private String stateStatus;
    private String lang;
    private String timezone;

    public ActivityEntry() {
    }

    public ActivityEntry(Parcel source) {
        id = source.readString();
        long _start = source.readLong();
        stateStart = _start == -1 ? null : new Date(_start);
        long _end = source.readLong();
        stateEnd = _end == -1 ? null : new Date(_end);
        stateTitle = source.readString();
        stateDescription = source.readString();
        stateType = source.readString();
        stateStatus = source.readString();
        lang = source.readString();
        timezone = source.readString();
    }

    public ActivityEntry(JSONObject object) {
        if (object == null)
            return;

        if (object.has("vid")) {
            this.id = object.optString("vid");
        }

        if (object.has("title")) {
            this.stateTitle = object.optString("title");
        }

        if (object.has("status")) {
            this.stateStatus = object.optString("status");
        }

        if (object.has("language")) {
            this.lang = object.optString("language");
        } else {
            this.lang = "und";
        }

        if (object.has("body")) {
            JSONObject body = object.optJSONObject("body");
            if (body != null && body.has(this.lang)) {
                JSONArray langs = body.optJSONArray(this.lang);
                if (langs != null && langs.length() > 0) {
                    JSONObject currentLang = langs.optJSONObject(0);
                    if (currentLang != null && currentLang.has("value")) {
                        this.stateDescription = currentLang.optString("value");
                    }
                }
            }
        }

        if (object.has("field_activity_date")) {
            JSONObject body = object.optJSONObject("field_activity_date");
            if (body != null && body.has(this.lang)) {
                JSONArray langs = body.optJSONArray(this.lang);
                if (langs != null && langs.length() > 0) {
                    JSONObject currentLang = langs.optJSONObject(0);
                    if (currentLang != null) {
                        this.timezone = "Europe/Moscow";// TODO USA?
                        if (currentLang.has("timezone")) {
                            this.timezone = object.optString("timezone");
                        }

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone(timezone));

                        if (currentLang.has("value")) {
                            String _startDate = object.optString("value");
                            try {
                                this.stateStart = format.parse(_startDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                this.stateStart = null;
                            }
                        }

                        if (currentLang.has("value2")) {
                            String _endDate = object.optString("value2");
                            try {
                                this.stateEnd = format.parse(_endDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                this.stateEnd = null;
                            }
                        }
                    }
                }
            }
        }
    }

    public String toJson() throws JSONException {
        JSONObject root = new JSONObject();

        root.put("vid", id);
        root.put("title", stateTitle);
        root.put("status", stateStatus);

        String currentLang = TextUtils.isEmpty(lang) ? "und" : lang;
        root.put("language", currentLang);

        JSONObject body = new JSONObject();
        JSONArray langs = new JSONArray();
        JSONObject currentLangObject = new JSONObject();
        currentLangObject.put("value", stateDescription);
        langs.put(currentLangObject);
        body.put(currentLang, langs);
        root.put("body", body);


        JSONObject field_activity_date = new JSONObject();
        JSONArray langsDate = new JSONArray();
        JSONObject currentLangObjectDate = new JSONObject();
        currentLangObjectDate.put("value", stateStart);
        currentLangObjectDate.put("value2", stateEnd);
        currentLangObjectDate.put("timezone", timezone);
        langsDate.put(currentLangObject);
        body.put(currentLang, langsDate);
        root.put("field_activity_date", field_activity_date);

        return root.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(stateStart != null ? stateStart.getTime() : -1);
        dest.writeLong(stateEnd != null ? stateEnd.getTime() : -1);
        dest.writeString(stateTitle);
        dest.writeString(stateDescription);
        dest.writeString(stateType);
        dest.writeString(stateStatus);
        dest.writeString(lang);
        dest.writeString(timezone);
    }
}
