package com.tyczj.extendedcalendarview;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Анна on 25.08.2015.
 */
public class Event {

    public static final int AMOUNT_OF_EVENTS = 3;
    private final int INTIMATE_RELATIONSHIP = 0;
    private final int PAIN = 1;
    private final int TEXT = 2;

    private int year;
    private int month;
    private int day;
    private boolean[] colors = new boolean[3];


    public Event(Calendar date, int colors) {
        Log.d("Event", "constructor");
        this.year = date.get(GregorianCalendar.YEAR);
        this.month = date.get(GregorianCalendar.MONTH);
        this.day = date.get(GregorianCalendar.DAY_OF_MONTH);
        for (int i = AMOUNT_OF_EVENTS-1; i >= 0; i--) {
            this.colors[i] = (colors % 10 == 1);
            colors = colors / 10;
            Log.d("Event color " + i,": " + String.valueOf(this.colors[i]));
        }
    }

    public int getYear() {
        return this.year;
    }
    public int getMonth() {
        return this.month;
    }
    public int getDayr() {
        return this.day;
    }

    public boolean isIntimateRelEvent(){
        return colors[INTIMATE_RELATIONSHIP];
    }

    public boolean isPainEvent(){
        return colors[PAIN];
    }

    public boolean isTextEvent(){
        return colors[TEXT];
    }

    public boolean[] getEvents(){
        return colors;
    }

}
