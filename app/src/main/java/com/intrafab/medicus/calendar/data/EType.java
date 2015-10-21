package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public enum EType implements Parcelable {
    ETSingleChoose, ETMultiplyChoose, ETDateTime, ETInterval, ETSwitch, ETComment, ETNumber;

    public static final String TAG = EType.class.getName();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ordinal());
    }

    public static final Parcelable.Creator<EType> CREATOR = new Parcelable.Creator<EType>() {

        public EType createFromParcel(Parcel in) {
            return EType.values()[in.readInt()];
        }

        public EType[] newArray(int size) {
            return new EType[size];
        }

    };
}
