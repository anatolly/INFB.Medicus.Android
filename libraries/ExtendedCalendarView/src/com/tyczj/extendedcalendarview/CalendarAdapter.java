package com.tyczj.extendedcalendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter{
	
	static final int FIRST_DAY_OF_WEEK =0;
	protected Context context;
	protected Calendar cal;
	public String[] days;
//	OnAddNewEventClick mAddEvent;
	
	protected ArrayList<Day> dayList = new ArrayList<Day>();
	protected HashMap<Long,Event> eventHashMap = new HashMap<Long, Event>();
	
	public CalendarAdapter(Context context, Calendar cal){
		this.cal = cal;
		this.context = context;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		refreshDays();
	}

	@Override
	public int getCount() {
		return days.length;
	}

	@Override
	public Object getItem(int position) {
		return dayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public int getPrevMonth(){
		if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)){
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
		}else{
			
		}
		int month = cal.get(Calendar.MONTH);
		if(month == 0){
			return month = 11;
		}
		
		return month-1;
	}
	
	public int getMonth(){
		return cal.get(Calendar.MONTH);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(position >= 0 && position < 7){
			v = vi.inflate(R.layout.day_of_week, null);
			TextView day = (TextView)v.findViewById(R.id.textView1);
			
			if(position == 0){
				day.setText(R.string.sunday);
			}else if(position == 1){
				day.setText(R.string.monday);
			}else if(position == 2){
				day.setText(R.string.tuesday);
			}else if(position == 3){
				day.setText(R.string.wednesday);
			}else if(position == 4){
				day.setText(R.string.thursday);
			}else if(position == 5){
				day.setText(R.string.friday);
			}else if(position == 6){
				day.setText(R.string.saturday);
			}
			
		}else{
			
	        v = vi.inflate(R.layout.day_view, null);
			FrameLayout today = (FrameLayout)v.findViewById(R.id.today_frame);
			Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
			Day d = dayList.get(position);
			/// set up a grey circle
			if(d.getYear() == cal.get(Calendar.YEAR) && d.getMonth() == cal.get(Calendar.MONTH) && d.getDay() == cal.get(Calendar.DAY_OF_MONTH)){
				today.setVisibility(View.VISIBLE);
			}else{
				today.setVisibility(View.GONE);
			}
			
			TextView dayTV = (TextView)v.findViewById(R.id.textView1);
			
			RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.rl);
			ImageView period = (ImageView)v.findViewById(R.id.period);
			ImageView expectedPeriod = (ImageView)v.findViewById(R.id.expected_period);
			ImageView ovulation = (ImageView)v.findViewById(R.id.ovulation);
			ImageView fertilePeriod = (ImageView)v.findViewById(R.id.fertile_period);
			ImageView pain = (ImageView)v.findViewById(R.id.pain);
			ImageView intercourse = (ImageView)v.findViewById(R.id.intercourse);
			ImageView text = (ImageView)v.findViewById(R.id.text);

			period.setVisibility(View.INVISIBLE);
			expectedPeriod.setVisibility(View.INVISIBLE);
			ovulation.setVisibility(View.INVISIBLE);
			fertilePeriod.setVisibility(View.INVISIBLE);
			text.setVisibility(View.INVISIBLE);
			pain.setVisibility(View.INVISIBLE);
			intercourse.setVisibility(View.INVISIBLE);

			if (eventHashMap != null && !eventHashMap.isEmpty())
			{
				Event event = eventHashMap.get(d.getTimeInSec());
				//Log.d("CalendarAdapter", "eventHashMap.size(): " + eventHashMap.size());
				//Log.d("CalendarAdapter", "event: " + d.getDateToString());
				if (event != null){
					if (event.isPeriod())
						period.setVisibility(View.VISIBLE);
					if (event.isExpectedPeriod())
						expectedPeriod.setVisibility(View.VISIBLE);
					if (event.isOvulation())
						ovulation.setVisibility(View.VISIBLE);
					if (event.isFertilePeriod())
						fertilePeriod.setVisibility(View.VISIBLE);
					if (event.isPain())
						pain.setVisibility(View.VISIBLE);
					if (event.isTextNote())
						text.setVisibility(View.VISIBLE);
					if (event.isIntercourse())
						intercourse.setVisibility(View.VISIBLE);
				}
			}
			//else
				//Log.d("CalendarAdapter", "eventHashMap is empty");

			Day day = dayList.get(position);

			if(day.getDay() == 0){
				rl.setVisibility(View.GONE);
			}else{
				dayTV.setVisibility(View.VISIBLE);
				dayTV.setText(String.valueOf(day.getDay()));
			}
		}

		return v;
	}
	
	public void refreshDays()
    {
    	// clear items
    	dayList.clear();
    	
    	int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)+7;
        int firstDay = (int)cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
		Log.d("refreshDays, month", String.valueOf(month));
        TimeZone tz = TimeZone.getDefault();
        
        // figure size of the array
        if(firstDay==1){
        	days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
        	days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }
        
        int j=FIRST_DAY_OF_WEEK;
        
        // populate empty days before first real day
        if(firstDay>1) {
	        for(j=0;j<(firstDay-FIRST_DAY_OF_WEEK)+7;j++) {
	        	days[j] = "";
	        	Day d = new Day(context,0,0,0);
	        	dayList.add(d);
	        }
        }
	    else {
	    	for(j=0;j<(FIRST_DAY_OF_WEEK*6)+7;j++) {
	        	days[j] = "";
	        	Day d = new Day(context,0,0,0);
	        	dayList.add(d);
	        }
	    	j=FIRST_DAY_OF_WEEK*6+1; // sunday => 1, monday => 7
	    }
        
        // populate days
        int dayNumber = 1;
        
        if(j>0 && dayList.size() > 0 && j != 1){
        	dayList.remove(j-1);
        }
        
        for(int i=j-1;i<days.length;i++) {
        	Day d = new Day(context,dayNumber,year,month);
        	
        	Calendar cTemp = Calendar.getInstance();
        	cTemp.set(year, month, dayNumber);
        	int startDay = Time.getJulianDay(cTemp.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp.getTimeInMillis())));
        	
        	d.setAdapter(this);
        	d.setStartDay(startDay);
        	
        	days[i] = ""+dayNumber;
        	dayNumber++;
        	dayList.add(d);
        }
    }

	public void addEvents (HashMap<Long, Event> events) {
//		if (events == null || events.isEmpty())
//			return;
//		for (Event event : events){
//			if (eventHashMap.containsKey(event.getDateToString())){
//				Event existEvent = eventHashMap.get(event.getDateToString());
//				event.setIsFertilePeriod(existEvent.isFertilePeriod());
//				event.setIsOvulation(existEvent.isOvulation());
//				event.setIsPeriod(event.isPeriod());
//				event.setIsExpectedPeriod(event.isExpectedPeriod());
//			}
//			eventHashMap.put(event.getDateToString(), event);
//		}
		eventHashMap = events;
		if (eventHashMap == null)
			return;
		/*for (Event event : eventHashMap.values()){
			Log.d("hashMapEvent: ", event.getDateToString());
		}*/
	}

//	public void addEvent (Event event) {
//		if (event != null){
//			if (eventHashMap.containsKey(event.getTimeInSec())){
//				Event existEvent = eventHashMap.get(event.getTimeInSec());
//				event.setIsFertilePeriod(existEvent.isFertilePeriod());
//				event.setIsOvulation(existEvent.isOvulation());
//				event.setIsPeriod(event.isPeriod());
//				event.setIsExpectedPeriod(event.isExpectedPeriod());
//			}
//			eventHashMap.put(event.getTimeInSec(), event);
//		}
//	}
//
//	public void addNewPeriod (ArrayList<Event> events) {
//		if (events == null || events.isEmpty())
//			return;
//		Log.d("Calendar Adapter", "addNewPeriod arrayList events size: " + events.size());
//		for (Event event : events){
//			if (eventHashMap.containsKey(event.getTimeInSec())){
//				Event existEvent = eventHashMap.get(event.getTimeInSec());
//				existEvent.setIsOvulation(event.isOvulation());
//				existEvent.setIsFertilePeriod(event.isFertilePeriod());
//				existEvent.setIsPeriod(event.isPeriod());
////				eventHashMap.put(existEvent.getDateToString(), existEvent);
//			} else{
//				eventHashMap.put(event.getTimeInSec(), event);
//			}
//		}
//	}

//	public abstract static class OnAddNewEventClick{
//		public abstract void onAddNewEventClick();
//	}
	
}
