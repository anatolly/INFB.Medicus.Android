package com.intrafab.medicus.calendar.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class ScheduleInfo implements Parcelable {
    public static final String TAG = ScheduleInfo.class.getName();

    private long mStartDate;
    private long mEndDate;
    private long mStartTime;
    private long mEndTime;
    private long mInterval;
    private List<EDay> mDays;

    public List<EDay> getDays() {
        return mDays;
    }

    public void setDays(List<EDay> days) {
        this.mDays = days;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public void setEndDate(long endDate) {
        this.mEndDate = endDate;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public long getInterval() {
        return mInterval;
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public void setStartDate(long startDate) {
        this.mStartDate = startDate;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    protected ScheduleInfo(Parcel in) {
        mStartDate = in.readLong();
        mEndDate = in.readLong();
        mStartTime = in.readLong();
        mEndTime = in.readLong();
        mInterval = in.readLong();
        mDays = in.createTypedArrayList(EDay.CREATOR);
    }

    public static final Creator<ScheduleInfo> CREATOR = new Creator<ScheduleInfo>() {
        @Override
        public ScheduleInfo createFromParcel(Parcel in) {
            return new ScheduleInfo(in);
        }

        @Override
        public ScheduleInfo[] newArray(int size) {
            return new ScheduleInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mStartDate);
        parcel.writeLong(mEndDate);
        parcel.writeLong(mStartTime);
        parcel.writeLong(mEndTime);
        parcel.writeLong(mInterval);
        parcel.writeTypedList(mDays);
    }
}
