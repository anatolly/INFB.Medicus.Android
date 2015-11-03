package com.intrafab.medicus.calendar.data;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class EventInfo extends BaseInfo {
    public static final String TAG = EventInfo.class.getName();

    private List<ParametersInfo> mParameters;
    private ScheduleInfo mSchedule;
    private boolean mIsNotify;
    private boolean mIsActive;
    private boolean mIsDeleted;
    private long mNotifyTime;
    private List<String> mTags;
    private String mCalendarId;
    private long mCurrentDate;
    private List<ExtraParametersInfo> mExtraParameters;

    public String getCalendarId() {
        return mCalendarId;
    }

    public void setCalendarId(String calendarId) {
        this.mCalendarId = calendarId;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean isActive) {
        this.mIsActive = isActive;
    }

    public boolean isDeleted() {
        return mIsDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.mIsDeleted = isDeleted;
    }

    public boolean isNotify() {
        return mIsNotify;
    }

    public void setNotify(boolean isNotify) {
        this.mIsNotify = isNotify;
    }

    public long getNotifyTime() {
        return mNotifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        this.mNotifyTime = notifyTime;
    }

    public List<ParametersInfo> getParameters() {
        return mParameters;
    }

    public void setParameters(List<ParametersInfo> parameters) {
        this.mParameters = parameters;
    }

    public void addParameter(ParametersInfo info) {
        if (mParameters != null) {
            mParameters.add(info);
        }
    }

    public ScheduleInfo getSchedule() {
        return mSchedule;
    }

    public void setSchedule(ScheduleInfo schedule) {
        this.mSchedule = schedule;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> tags) {
        this.mTags = tags;
    }

    public long getCurrentDate() {
        return mCurrentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.mCurrentDate = currentDate;
    }

    public List<ExtraParametersInfo> getExtraParameters() {
        return mExtraParameters;
    }

    public void setExtraParameters(List<ExtraParametersInfo> extraParameters) {
        this.mExtraParameters = extraParameters;
    }

    public ExtraParametersInfo getExtraParameters(String key) {
        for (ExtraParametersInfo info : mExtraParameters) {
            if (info.getKey().equals(key)) {
                return info;
            }
        }

        ExtraParametersInfo info = new ExtraParametersInfo();
        info.setKey(key);

        return info;
    }

    public void addExtraParameters(ExtraParametersInfo extraParameters) {
        this.mExtraParameters.add(extraParameters);
    }

    public EventInfo() {
        super();
        mParameters = new ArrayList<>();
        mSchedule = new ScheduleInfo();
        mExtraParameters = new ArrayList<>();
    }

    protected EventInfo(Parcel in) {
        super(in);
        mParameters = in.createTypedArrayList(ParametersInfo.CREATOR);
        mSchedule = in.readParcelable(ScheduleInfo.class.getClassLoader());
        mIsNotify = in.readInt() == 1;
        mIsActive = in.readInt() == 1;
        mIsDeleted = in.readInt() == 1;
        mNotifyTime = in.readLong();
        mTags = in.createStringArrayList();
        mCalendarId = in.readString();
        mCurrentDate = in.readLong();
        mExtraParameters = in.createTypedArrayList(ExtraParametersInfo.CREATOR);
    }

    public static final Creator<EventInfo> CREATOR = new Creator<EventInfo>() {
        @Override
        public EventInfo createFromParcel(Parcel in) {
            return new EventInfo(in);
        }

        @Override
        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedList(mParameters);
        parcel.writeParcelable(mSchedule, i);
        parcel.writeInt(mIsNotify ? 1 : 0);
        parcel.writeInt(mIsActive ? 1 : 0);
        parcel.writeInt(mIsDeleted ? 1 : 0);
        parcel.writeLong(mNotifyTime);
        parcel.writeStringList(mTags);
        parcel.writeString(mCalendarId);
        parcel.writeLong(mCurrentDate);
        parcel.writeTypedList(mExtraParameters);
    }
}
