package com.intrafab.medicus.calendar.pedometer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class Data implements Parcelable {
    public long steps;
    public long pace;
    public float distance;
    public float speed;
    public float calories;
    public long timer;

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getPace() {
        return pace;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public Data() {
        steps = 0L;
        pace = 0L;
        distance = 0f;
        speed = 0f;
        calories = 0f;
        timer = 0L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;

        Data data = (Data) o;

        if (getSteps() != data.getSteps()) return false;
        if (getPace() != data.getPace()) return false;
        if (Float.compare(data.getDistance(), getDistance()) != 0) return false;
        if (Float.compare(data.getSpeed(), getSpeed()) != 0) return false;
        if (Float.compare(data.getCalories(), getCalories()) != 0) return false;
        return getTimer() == data.getTimer();

    }

    @Override
    public int hashCode() {
        int result = (int) (getSteps() ^ (getSteps() >>> 32));
        result = 31 * result + (int) (getPace() ^ (getPace() >>> 32));
        result = 31 * result + (getDistance() != +0.0f ? Float.floatToIntBits(getDistance()) : 0);
        result = 31 * result + (getSpeed() != +0.0f ? Float.floatToIntBits(getSpeed()) : 0);
        result = 31 * result + (getCalories() != +0.0f ? Float.floatToIntBits(getCalories()) : 0);
        result = 31 * result + (int) (getTimer() ^ (getTimer() >>> 32));
        return result;
    }

    public String toString() {
        return String.format("/nsteps: %d", steps) +
                String.format("/npace: %d", pace) +
                String.format("/ntimer: %d", timer) +
                String.format("/ndistance: %f", distance) +
                String.format("/nspeed: %f", speed) +
                String.format("/ncalories: %f", calories);
    }
}
