package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public enum EInterval implements Parcelable {
    EIMinute, EIFive, EIFifteen, EIHalfHour, EIHour, EIHalfDay, EIDay, EIWeek, EIMonth, EIYear, EICustom;

    public static final String TAG = EInterval.class.getName();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
    }

    public static final Parcelable.Creator<EInterval> CREATOR = new Parcelable.Creator<EInterval>() {

        public EInterval createFromParcel(Parcel in) {
            return EInterval.values()[in.readInt()];
        }

        public EInterval[] newArray(int size) {
            return new EInterval[size];
        }

    };
}
