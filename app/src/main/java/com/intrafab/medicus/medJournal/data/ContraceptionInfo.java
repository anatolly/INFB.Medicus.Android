package com.intrafab.medicus.medJournal.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Анна on 15.10.2015.
 */
public class ContraceptionInfo implements Parcelable {

    public static final int TYPE_PILLS = 0;
    public static final int TYPE_RING = 1;
    public static final int TYPE_PLASTER = 2;
    public static final int TYPE_INJECTION = 3;
    public static final int TYPE_EMERGENCY = 4;

    private int contraceptionTypeId = -1;
    private String contraceptionTypeName;
    private long startDate;
    private int activeDays;
    private int breakDays;
    private int notificationEnabled = 1;
    private int notificationActiveDayFrequency;
    private int notificationBreakDayFrequency;
    private int notificationTimeFrequency;
    //private int notificationTime;
    private int notificationInterval;
    private String notificationTitle;
    private String notificationText;
    private int placebo;

    public int getContraceptionTypeId() {
        return contraceptionTypeId;
    }

    public void setContraceptionTypeId(int contraceptionTypeId) {
        this.contraceptionTypeId = contraceptionTypeId;
    }

    public String getContraceptionTypeName() {
        return contraceptionTypeName;
    }

    public void setContraceptionTypeName(String contraceptionTypeName) {
        this.contraceptionTypeName = contraceptionTypeName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public int getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }

    public int getBreakDays() {
        return breakDays;
    }

    public void setBreakDays(int breakDays) {
        this.breakDays = breakDays;
    }

    public int getNotificationActiveDayFrequency() {
        return notificationActiveDayFrequency;
    }

    public void setNotificationActiveDayFrequency(int notificationActiveDayFrequency) {
        this.notificationActiveDayFrequency = notificationActiveDayFrequency;
    }

    public int getNotificationBreakDayFrequency() {
        return notificationBreakDayFrequency;
    }

    public void setNotificationBreakDayFrequency(int notificationBreakDayFrequency) {
        this.notificationBreakDayFrequency = notificationBreakDayFrequency;
    }

    public int getNotificationTimeFrequency() {
        return notificationTimeFrequency;
    }

    public void setNotificationTimeFrequency(int notificationTimeFrequency) {
        this.notificationTimeFrequency = notificationTimeFrequency;
    }

//    public int getNotificationTime() {
//        return notificationTime;
//    }

//    public void setNotificationTime(int notificationTime) {
//        this.notificationTime = notificationTime;
//    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public int getPlacebo() {
        return placebo;
    }

    public void setPlacebo(int placebo) {
        this.placebo = placebo;
    }

    public int getNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(int notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public int getNotificationInterval() {
        return notificationInterval;
    }

    public void setNotificationInterval(int notificationInterval) {
        this.notificationInterval = notificationInterval;
    }

    public ContraceptionInfo(){

    }

    protected ContraceptionInfo(Parcel in) {
        contraceptionTypeId = in.readInt();
        contraceptionTypeName = in.readString();
        startDate = in.readLong();
        activeDays = in.readInt();
        breakDays = in.readInt();
        notificationEnabled = in.readInt();
        notificationActiveDayFrequency = in.readInt();
        notificationBreakDayFrequency = in.readInt();
        notificationTimeFrequency = in.readInt();
        //notificationTime = in.readInt();
        notificationInterval = in.readInt();
        notificationTitle = in.readString();
        notificationText = in.readString();
        placebo = in.readInt();
    }

    public static final Creator<ContraceptionInfo> CREATOR = new Creator<ContraceptionInfo>() {
        @Override
        public ContraceptionInfo createFromParcel(Parcel in) {
            return new ContraceptionInfo(in);
        }

        @Override
        public ContraceptionInfo[] newArray(int size) {
            return new ContraceptionInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(contraceptionTypeId);
        parcel.writeString(contraceptionTypeName);
        parcel.writeLong(startDate);
        parcel.writeInt(activeDays);
        parcel.writeInt(breakDays);
        parcel.writeInt(notificationEnabled);
        parcel.writeInt(notificationActiveDayFrequency);
        parcel.writeInt(notificationBreakDayFrequency);
        parcel.writeInt(notificationTimeFrequency);
        //parcel.writeInt(notificationTime);
        parcel.writeInt(notificationInterval);
        parcel.writeString(notificationTitle);
        parcel.writeString(notificationText);
        parcel.writeInt(placebo);
    }

    public String toString (){
        String result = "";
        result += "\nid :" + contraceptionTypeId;
        result += "\ncontName: " + contraceptionTypeName;
        result += "\nstartDay: " + startDate;
        result += "\nactiveDay: " + activeDays;
        result += "\nbreakDays:" + breakDays;
        result += "\nnotifEnable" + notificationEnabled;
        result += "\nnotifActiveDayFreq: " + notificationActiveDayFrequency;
        result += "\nnotifBreakDayFreq: " + notificationBreakDayFrequency;
        result += "\nactiveTimeFreq: " + notificationTimeFrequency;
        //result += "\nnotifTime:" + notificationTime;
        result += "\nnotifInterval" + notificationInterval;
        result += "\nnotifName: " + notificationTitle;
        result += "\nnotifText: " + notificationText;
        result += "\nplacebo: " + placebo;
        return result;
    }
}
