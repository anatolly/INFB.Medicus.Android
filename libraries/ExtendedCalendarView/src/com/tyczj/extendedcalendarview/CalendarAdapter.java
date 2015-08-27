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
	protected HashMap<String,Event> eventHashMap = new HashMap<String, Event>();
	
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
			ImageView text = (ImageView)v.findViewById(R.id.imageView3);
			ImageView pain = (ImageView)v.findViewById(R.id.imageView5);
			ImageView intimacy = (ImageView)v.findViewById(R.id.imageView6);

			text.setVisibility(View.INVISIBLE);
			pain.setVisibility(View.INVISIBLE);
			intimacy.setVisibility(View.INVISIBLE);
			//dayTV.setVisibility(View.INVISIBLE);
			//rl.setVisibility(View.INVISIBLE);

			if (!eventHashMap.isEmpty())
			{
				//for (int i=1; i<= cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH); i++){
					//DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					//Date today = Calendar.getInstance().getTime();
					//String reportDate = df.format(today);
					String key = d.getDay() + "." + d.getMonth() + "." + d.getYear();
					Log.d("key for events: ", key);
					Event event = eventHashMap.get(key);
					if (event != null){
						if (event.isIntimateRelEvent())
							intimacy.setVisibility(View.VISIBLE);
						if (event.isPainEvent())
							pain.setVisibility(View.VISIBLE);
						if (event.isTextEvent())
							text.setVisibility(View.VISIBLE);
					}
				//}
			}

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

	public void setEvents (List<Calendar> dates, List<Integer> colors) {
		if (dates == null || colors == null || dates.size() != colors.size())
			return;

		eventHashMap.clear();
		for (int i = 0; i < dates.size(); i++) {
			eventHashMap.put(dates.get(i).toString(), new Event(dates.get(i), colors.get(i)));
			Log.d("setEvents, dates:" , dates.get(i).toString());
		}
	}


	public void addEvent (Calendar date, int color) {
		if (date == null)
			return;
		String key = date.get(Calendar.DAY_OF_MONTH) + "." + (date.get(Calendar.MONTH)-1) + "." + date.get(Calendar.YEAR);
		Log.d ("key in add event: " , key);
		eventHashMap.put(key, new Event(date, color));
	}

//	public abstract static class OnAddNewEventClick{
//		public abstract void onAddNewEventClick();
//	}
	
}
