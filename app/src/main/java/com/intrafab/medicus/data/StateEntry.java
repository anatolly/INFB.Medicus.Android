package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Artemiy Terekhov on 27.04.2015.
 */
public class StateEntry implements Parcelable {
    public final static String ENTRY_STATE_START = "start";
    public final static String ENTRY_STATE_END = "end";
    public final static String ENTRY_STATE_DESCRIPTION = "description";
    public final static String ENTRY_STATE_TYPE = "type";
    public final static String ENTRY_STATE_STATUS = "status";

    public static final Creator<StateEntry> CREATOR = new Creator<StateEntry>() {
        @Override
        public StateEntry createFromParcel(Parcel source) {
            return new StateEntry(source);
        }

        @Override
        public StateEntry[] newArray(int size) {
            return new StateEntry[size];
        }
    };

    private long stateStart;
    private long stateEnd;
    private String stateDescription;
    private String stateType;
    private String stateStatus;

    public String getStateStatus() {
        return stateStatus;
    }

    public void setStateStatus(String stateStatus) {
        this.stateStatus = stateStatus;
    }

    public long getStateStart() {
        return stateStart;
    }

    public void setStateStart(long stateStart) {
        this.stateStart = stateStart;
    }

    public long getStateEnd() {
        return stateEnd;
    }

    public void setStateEnd(long stateEnd) {
        this.stateEnd = stateEnd;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    public String getStateType() {
        return stateType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    public StateEntry() {
    }

    public StateEntry(Parcel source) {
        stateStart = source.readLong();
        stateEnd = source.readLong();
        stateDescription = source.readString();
        stateType = source.readString();
        stateStatus = source.readString();
    }

    public StateEntry(JSONObject object) {
        if (object == null)
            return;

        if (object.has(ENTRY_STATE_START)) {
            this.stateStart = object.optLong(ENTRY_STATE_START);
        }

        if (object.has(ENTRY_STATE_END)) {
            this.stateEnd = object.optLong(ENTRY_STATE_END);
        }

        if (object.has(ENTRY_STATE_DESCRIPTION)) {
            this.stateDescription = object.optString(ENTRY_STATE_DESCRIPTION);
        }

        if (object.has(ENTRY_STATE_TYPE)) {
            this.stateType = object.optString(ENTRY_STATE_TYPE);
        }

        if (object.has(ENTRY_STATE_STATUS)) {
            this.stateStatus = object.optString(ENTRY_STATE_STATUS);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(stateStart);
        dest.writeLong(stateEnd);
        dest.writeString(stateDescription);
        dest.writeString(stateType);
        dest.writeString(stateStatus);
    }
}
