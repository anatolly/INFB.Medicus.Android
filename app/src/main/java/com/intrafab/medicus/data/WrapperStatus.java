package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 16.06.2015.
 */
public class WrapperStatus implements Parcelable {

    public static final Creator<WrapperStatus> CREATOR = new Creator<WrapperStatus>() {
        @Override
        public WrapperStatus createFromParcel(Parcel source) {
            return new WrapperStatus(source);
        }

        @Override
        public WrapperStatus[] newArray(int size) {
            return new WrapperStatus[size];
        }
    };

    public String getInteger_activity_status() {
        return integer_activity_status;
    }

    public void setInteger_activity_status(String integer_activity_status) {
        this.integer_activity_status = integer_activity_status;
    }

    private String integer_activity_status;

    public WrapperStatus() {

    }

    public WrapperStatus(Parcel source) {
        integer_activity_status = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(integer_activity_status);
    }
}
