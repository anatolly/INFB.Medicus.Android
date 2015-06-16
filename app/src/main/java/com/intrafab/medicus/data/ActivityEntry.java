package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    public Date getStateEnd() {
        return stateEnd;
    }

    public void setStateEnd(Date stateEnd) {
        this.stateEnd = stateEnd;
    }

    public Date getStateStart() {
        return stateStart;
    }

    public void setStateStart(Date stateStart) {
        this.stateStart = stateStart;
    }

    public String getStateStatus() {
        return stateStatus;
    }

    public void setStateStatus(String stateStatus) {
        this.stateStatus = stateStatus;
    }

    public String getStateTitle() {
        return stateTitle;
    }

    public void setStateTitle(String stateTitle) {
        this.stateTitle = stateTitle;
    }

    public String getStateType() {
        return stateType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

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

        if (object.has("id")) {
            this.id = object.optString("id");
        }

        if (object.has("title")) {
            this.stateTitle = object.optString("title");
        }

        if (object.has("integer_activity_status")) {
            String status = object.optString("integer_activity_status");
            if (status.equals("0")) {
                this.stateStatus = StateEntryType.STATUSES.get(0);
            } else if (status.equals("1")) {
                this.stateStatus = StateEntryType.STATUSES.get(1);
            } else if (status.equals("2")) {
                this.stateStatus = StateEntryType.STATUSES.get(2);
            } else if (status.equals("3")) {
                this.stateStatus = StateEntryType.STATUSES.get(3);
            } else {
                this.stateStatus = StateEntryType.STATUSES.get(0);
            }
        }

//        if (object.has("language")) {
//            this.lang = object.optString("language");
//        } else {
            this.lang = "und";
//        }

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

        if (object.has("activity_period")) {
            JSONObject body = object.optJSONObject("activity_period");
            if (body != null) {
                if (body.has("value")) {
                    String _startDate = body.optString("value");
                    try {
                        long _start = Long.parseLong(_startDate) * 1000L;
                        this.stateStart = new Date(_start);
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.stateStart = null;
                    }
                }

                if (body.has("value2")) {
                    String _endDate = body.optString("value2");
                    try {
                        long _end = Long.parseLong(_endDate) * 1000L;
                        this.stateEnd = new Date(_end);
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.stateEnd = null;
                    }
                }
            }
        }
    }

    public String toJson() throws JSONException {
        JSONObject root = new JSONObject();

//        root.put("vid", id);
//        root.put("title", stateTitle);
        String status = "0";
        if (stateStatus.equals(StateEntryType.STATUSES.get(0)))
            status = "0";
        else if (stateStatus.equals(StateEntryType.STATUSES.get(1)))
            status = "1";
        else if (stateStatus.equals(StateEntryType.STATUSES.get(2)))
            status = "2";
        else if (stateStatus.equals(StateEntryType.STATUSES.get(3)))
            status = "3";
        root.put("integer_activity_status", status);

//        String currentLang = TextUtils.isEmpty(lang) ? "und" : lang;
//        root.put("language", currentLang);
//
//        JSONObject body = new JSONObject();
//        JSONArray langs = new JSONArray();
//        JSONObject currentLangObject = new JSONObject();
//        currentLangObject.put("value", stateDescription);
//        langs.put(currentLangObject);
//        body.put(currentLang, langs);
//        root.put("body", body);
//
//
//        JSONObject field_activity_date = new JSONObject();
//        JSONArray langsDate = new JSONArray();
//        JSONObject currentLangObjectDate = new JSONObject();
//        currentLangObjectDate.put("value", stateStart);
//        currentLangObjectDate.put("value2", stateEnd);
//        currentLangObjectDate.put("timezone", timezone);
//        langsDate.put(currentLangObject);
//        body.put(currentLang, langsDate);
//        root.put("field_activity_date", field_activity_date);

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
