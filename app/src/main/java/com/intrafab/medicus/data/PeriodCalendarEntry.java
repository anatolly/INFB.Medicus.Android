package com.intrafab.medicus.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Анна on 24.08.2015.
 */
public class PeriodCalendarEntry implements Parcelable {

    /// static string keys
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;



    public static final Creator<PeriodCalendarEntry> CREATOR = new Creator<PeriodCalendarEntry>() {
        @Override
        public PeriodCalendarEntry createFromParcel(Parcel source) {
            return new PeriodCalendarEntry(source);
        }

        @Override
        public PeriodCalendarEntry[] newArray(int size) {
            return new PeriodCalendarEntry[size];
        }
    };

    private int year;                       // 	required field
    private int month;                      // 	required field
    private int day;                        // 	required field
    private int isMenstrualPeriod;          // false by default
    private int menstrualFlowAmount;        // 0 by default
    private int isOvulationDay;             // false by default
    private int isIntercourse;     // false by default
    private int bodyTemperature;            // -1 by default
    private String note;                    // empty by default
    private SparseArray moods;              // empty by default
    private SparseArray symptoms;           // empty by default

    // при добавлении события нужно обязательно написать проверку на наличие изменений
    // чтобы не была пустых записей содержащих только одну дату


    protected PeriodCalendarEntry(Parcel source) {
        year = source.readInt();
        month = source.readInt();
        day = source.readInt();
        isMenstrualPeriod = source.readInt();
        menstrualFlowAmount = source.readInt();
        isOvulationDay = source.readInt();
        isIntercourse = source.readInt();
        bodyTemperature = source.readInt();
        note = source.readString();
        moods = source.readSparseArray(Integer.class.getClassLoader());
        symptoms = source.readSparseArray(Integer.class.getClassLoader());
    }

    public PeriodCalendarEntry( int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(isMenstrualPeriod);
        parcel.writeInt(menstrualFlowAmount);
        parcel.writeInt(isOvulationDay);
        parcel.writeInt(isIntercourse);
        parcel.writeInt(bodyTemperature);
        parcel.writeString(note);
        parcel.writeSparseArray(moods);
        parcel.writeSparseArray(symptoms);
    }

    public long getTimeInMillis (){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.set(year, month, day);
        return cal.getTimeInMillis();
    }

    public int getYear (){
        return year;
    }

    public int getMonth (){
        return month;
    }

    public int getDay (){
        return day;
    }

    public void setMenstrualPeriod (boolean isMenstrualPeriod){
        if (isMenstrualPeriod)
            this.isMenstrualPeriod = 1;
        else
            this.isMenstrualPeriod = 0;
    }

    public int getMenstrualPeriod (){
        return isMenstrualPeriod;
    }

    public boolean isMenstrualPeriod () {
        return (isMenstrualPeriod == 1);
    }

    public void setMenstrualFlowAmount (int menstrualFlowAmount){
        this.menstrualFlowAmount = menstrualFlowAmount;
    }

    public int getMenstrualFlowAmount (){
        return menstrualFlowAmount;
    }

    public void setOvulationDay (boolean isOvulationDay){
        if (isOvulationDay)
            this.isOvulationDay = 1;
        else
            this.isOvulationDay = 0;
    }

    public int getOvulationDay (){
        return isOvulationDay;
    }

    public boolean isOvulationDay () {
        return (isOvulationDay == 1);
    }

    public void setIntercourse (boolean isIntercourse){
        if (isIntercourse)
            this.isIntercourse = 1;
        else
            this.isIntercourse = 0;
    }

    public int getisIntercourse (){
        return isIntercourse;
    }

    public boolean isIntercourse () {
        return (isIntercourse == 1);
    }

    public void setBodyTemperature (int bodyTemperature){
        this.bodyTemperature = bodyTemperature;
    }

    public int getBodyTemperature (){
        return bodyTemperature;
    }

    public void setNote (String note){
        this.note = note;
    }

    public String getNote (){
        return note;
    }

    public void setMoods (SparseArray moods){
        this.moods = moods;
    }

    public SparseArray getMoods () {
        return moods;
    }

    public void setSymptoms (SparseArray symptoms){
        this.symptoms = symptoms;
    }

    public SparseArray getSymptoms () {
        return symptoms;
    }
}
