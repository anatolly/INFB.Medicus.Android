package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public enum EDay implements Parcelable {
    EDMonday, EDTuesday, EDWednesday, EDThursday, EDFriday, EDSaturday, EDSunday;

    public static final String TAG = EDay.class.getName();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<EDay> CREATOR = new Parcelable.Creator<EDay>() {

        public EDay createFromParcel(Parcel in) {
            return EDay.values()[in.readInt()];
        }

        public EDay[] newArray(int size) {
            return new EDay[size];
        }

    };

    public List<EDay> getAll() {
        return Arrays.asList(EDMonday, EDTuesday, EDWednesday, EDThursday, EDFriday, EDSaturday, EDSunday);
    }

    public List<EDay> getWeekend() {
        return Arrays.asList(EDSaturday, EDSunday);
    }

    public List<EDay> getWork() {
        return Arrays.asList(EDMonday, EDTuesday, EDWednesday, EDThursday, EDFriday);
    }
}
