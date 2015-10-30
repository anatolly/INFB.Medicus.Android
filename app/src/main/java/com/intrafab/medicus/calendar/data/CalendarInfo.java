package com.intrafab.medicus.calendar.data;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class CalendarInfo extends BaseInfo {
    public static final String TAG = CalendarInfo.class.getName();

    private float mRating;
    private List<EventInfo> mEvents;

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        this.mRating = rating;
    }

    public List<EventInfo> getEvents() {
        return mEvents;
    }

    public void setEvents(List<EventInfo> events) {
        this.mEvents = events;
    }

    public void addEvent(EventInfo info) {
        mEvents.add(info);
    }

    public CalendarInfo() {
        super();
        mEvents = new ArrayList<>();
    }

    protected CalendarInfo(Parcel in) {
        super(in);
        mRating = in.readFloat();
        mEvents = in.createTypedArrayList(EventInfo.CREATOR);
    }

    public static final Creator<CalendarInfo> CREATOR = new Creator<CalendarInfo>() {
        @Override
        public CalendarInfo createFromParcel(Parcel in) {
            return new CalendarInfo(in);
        }

        @Override
        public CalendarInfo[] newArray(int size) {
            return new CalendarInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(mRating);
        parcel.writeTypedList(mEvents);
    }
}
