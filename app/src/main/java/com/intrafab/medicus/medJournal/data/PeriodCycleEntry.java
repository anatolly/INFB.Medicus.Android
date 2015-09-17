package com.intrafab.medicus.medJournal.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by 1 on 08.09.2015.
 */
public class PeriodCycleEntry implements Parcelable {

    public static final String TAG = PeriodCycleEntry.class.getName();

    public static final Creator<PeriodCycleEntry> CREATOR = new Creator<PeriodCycleEntry>() {
        @Override
        public PeriodCycleEntry createFromParcel(Parcel source) {
            return new PeriodCycleEntry(source);
        }

        @Override
        public PeriodCycleEntry[] newArray(int size) {
            return new PeriodCycleEntry[size];
        }
    };


    private Calendar firstDay = Calendar.getInstance();
    private int periodDuration=28;
    private int mentsrualDuration=1;
    private int fertileFirstDay;
    private int ovulationDay;


    public long getFirstDay() {
        return firstDay.getTimeInMillis();
    }
    public Calendar getFirstDayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(firstDay.getTimeInMillis());
        return calendar;
    }

    public void setFirstDay(long firstDay) {
        this.firstDay.setTimeInMillis(firstDay);
    }

    public int getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(int periodDuration) {
        this.periodDuration = periodDuration;
    }

    public int getMentsrualDuration() {
        return mentsrualDuration;
    }

    public void setMentsrualDuration(int mentsrualDuration) {
        this.mentsrualDuration = mentsrualDuration;
    }

    public int getFertileFirstDay() {
        return fertileFirstDay;
    }

    public void setFertileFirstDay(int fertileFirstDay) {
        this.fertileFirstDay = fertileFirstDay;
    }

    public int getOvulationDay() {
        return ovulationDay;
    }

    public void setOvulationDay(int ovulationDay) {
        this.ovulationDay = ovulationDay;
    }

    public PeriodCycleEntry (long date) {
        this.firstDay.setTimeInMillis(date);
    }

    public PeriodCycleEntry (Parcel source){
        firstDay.setTimeInMillis(source.readLong());
        periodDuration = source.readInt();
        mentsrualDuration = source.readInt();
        fertileFirstDay = source.readInt();
        ovulationDay = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(firstDay.getTimeInMillis());
        parcel.writeInt(periodDuration);
        parcel.writeInt(mentsrualDuration);
        parcel.writeInt(fertileFirstDay);
        parcel.writeInt(ovulationDay);
    }
}
