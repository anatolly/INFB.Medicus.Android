package com.tyczj.extendedcalendarview;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Анна on 25.08.2015.
 */
public class Event {

    private Calendar date;
    private boolean isPeriod;
    private boolean isOvulation;
    private boolean isExpectedPeriod;
    private boolean isFertilePeriod;
    private boolean isIntercourse;
    private boolean isPain;
    private boolean isTextNote;

    public Event(long date) {
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(date);
    }
    public String getDateToString (){
        return date.get(Calendar.DAY_OF_MONTH)+"."+date.get(Calendar.MONTH)+"."+date.get(Calendar.YEAR);
    }

    public long getTimeInSec(){
        return date.getTimeInMillis()/1000;
    }

    public boolean isPeriod() {
        return isPeriod;
    }

    public void setIsPeriod(boolean isPeriod) {
        this.isPeriod = isPeriod;
    }

    public boolean isOvulation() {
        return isOvulation;
    }

    public void setIsOvulation(boolean isOvulation) {
        this.isOvulation = isOvulation;
    }

    public boolean isExpectedPeriod() {
        return isExpectedPeriod;
    }

    public void setIsExpectedPeriod(boolean isExpectedPeriod) {
        this.isExpectedPeriod = isExpectedPeriod;
    }

    public boolean isFertilePeriod() {
        return isFertilePeriod;
    }

    public void setIsFertilePeriod(boolean isFertilePeriod) {
        this.isFertilePeriod = isFertilePeriod;
    }

    public boolean isIntercourse() {
        return isIntercourse;
    }

    public void setIsIntercourse(boolean isIntercourse) {
        this.isIntercourse = isIntercourse;
    }

    public boolean isPain() {
        return isPain;
    }

    public void setIsPain(boolean isPain) {
        this.isPain = isPain;
    }

    public boolean isTextNote() {
        return isTextNote;
    }

    public void setIsTextNote(boolean isTextNote) {
        this.isTextNote = isTextNote;
    }



}
