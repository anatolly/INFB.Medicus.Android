package com.tyczj.extendedcalendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.BaseAdapter;

public class Day{
	
	int startDay;
	int monthEndDay;
	int day;
	int year;
	int month;
	Context context;
	BaseAdapter adapter;
	long timeInSec;

	public Day(Context context,int day, int year, int month){
		this.day = day;
		this.year = year;
		this.month = month;
		this.context = context;
		Calendar cal = Calendar.getInstance();
		// cal.set(year, month-1, day);
		cal.set(year, month, day, 0, 0, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		timeInSec = cal.getTimeInMillis()/1000;
		int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month, end, 0, 0, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		TimeZone tz = TimeZone.getDefault();
		monthEndDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}

	public long getTimeInSec(){
		return timeInSec;
	}

	public String getDateToString (){
		return day+"."+month+"."+year;
	}
//	public long getStartTime(){
//		return startTime;
//	}
//	
//	public long getEndTime(){
//		return endTime;
//	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	
	public int getDay(){
		return day;
	}

	/**
	 * Set the start day
	 * 
	 * @param startDay
	 */
	public void setStartDay(int startDay){
		this.startDay = startDay;
	}
	
	public int getStartDay(){
		return startDay;
	}


	public void setAdapter(BaseAdapter adapter){
		this.adapter = adapter;
	}
	
}
