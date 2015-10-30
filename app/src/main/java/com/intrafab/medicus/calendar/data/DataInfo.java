package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class DataInfo implements Parcelable {
    public String key;
    public String value;

    public DataInfo() {

    }

    public DataInfo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    protected DataInfo(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public static final Creator<DataInfo> CREATOR = new Creator<DataInfo>() {
        @Override
        public DataInfo createFromParcel(Parcel in) {
            return new DataInfo(in);
        }

        @Override
        public DataInfo[] newArray(int size) {
            return new DataInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(value);
    }
}
