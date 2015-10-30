package com.intrafab.medicus.calendar.pedometer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class Data implements Parcelable, Serializable {
    public long steps;
    public long pace;
    public float distance;
    public float speed;
    public float calories;
    public long timer;

    public Data() {

    }

    protected Data(Parcel in) {
        steps = in.readLong();
        pace = in.readLong();
        distance = in.readFloat();
        speed = in.readFloat();
        calories = in.readFloat();
        timer = in.readLong();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(steps);
        parcel.writeLong(pace);
        parcel.writeFloat(distance);
        parcel.writeFloat(speed);
        parcel.writeFloat(calories);
        parcel.writeLong(timer);
    }
}
