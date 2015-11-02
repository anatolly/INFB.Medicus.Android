package com.intrafab.medicus.medJournal.data;

import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;

import com.intrafab.medicus.Constants;
import com.intrafab.medicus.utils.Logger;
import com.tyczj.extendedcalendarview.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by 1 on 10.09.2015.
 */
public class PeriodDataKeeper {
    public static String TAG = PeriodDataKeeper.class.getName();

    public static Integer menstrualDuration = 4;
    public static Integer periodDuration = 28;
    public static Integer folicularPhaseDuration = 14;
    public static Integer menstrualDurationMax = 12;

    private static PeriodDataKeeper instance;
    private HashMap<Long, PeriodCalendarEntry> mCalendarData;
    private ArrayList<PeriodCycleEntry> mCycleData;
    private HashMap<Long, Event> mCalendarEvents;

    public static PeriodDataKeeper getInstance (){
        if (instance == null){
            instance = new PeriodDataKeeper();
        }
        return instance;
    }

    private PeriodDataKeeper (){
        mCalendarData = new HashMap<>();
        mCycleData = new ArrayList<>();
        mCalendarEvents = new HashMap<>();
    }

    public void setCalendarData (List<PeriodCalendarEntry> data){
        if (data != null){
            for (PeriodCalendarEntry entry : data){
                mCalendarData.put(entry.getTimeInSec(), entry);
            }
        }
    }

    public void setCycleData (List<PeriodCycleEntry> data) {
        if (data != null) {
            mCycleData.addAll(data);
        }
    }

    public HashMap<Long, PeriodCalendarEntry> getCalendarData(){
        return mCalendarData;
    }
    public ArrayList<PeriodCycleEntry> getPeriodData(){
        return mCycleData;
    }

    public HashMap<Long, Event> getCalendarEvents(){
        return mCalendarEvents;
    }

    public void checkData (){
        if (mCalendarData == null || mCalendarData.isEmpty())
            return;
        if(mCycleData == null || mCycleData.isEmpty())
            createCycleData();
        else {
            for (int j=0; j<mCycleData.size(); j++){
                PeriodCycleEntry period = mCycleData.get(j);
                for (int i=0; i<period.getMentsrualDuration(); i++){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(period.getFirstDay() + i*Constants.Numeric.dayToMillis);
                    long dateInSec = calendar.getTimeInMillis();
                    if(!mCalendarData.containsKey(dateInSec)){
                        mCycleData.remove(j);
                        break;
                    }
                }
            }
            createCycleData();
        }
    }

    private void createCycleData(){
        for (PeriodCalendarEntry entry : mCalendarData.values()){
            long firstDayInMillis = getFirstDayInMillis(entry);
            int index = findPeriod(firstDayInMillis);
            if (index < 0){
                PeriodCycleEntry period = new PeriodCycleEntry(firstDayInMillis);
                if (entry.getPeriod() > 1)
                    period.setPeriodDuration(entry.getPeriod());
                if (entry.getOvulationDay() > 0)
                    period.setOvulationDay(entry.getOvulationDay());
                addPeriodToList(period);
            }
            else {
                PeriodCycleEntry period = mCycleData.get(index);
                if (entry.isPeriod()) {
                    if (period.getPeriodDuration() < entry.getPeriod())
                        period.setPeriodDuration(entry.getPeriod());
                }
                else if (entry.isOvulationDay()){
                    period.setOvulationDay(entry.getOvulationDay());
                }
            }
        }
        int i;
        for (i=0; i<mCycleData.size()-1; i++){
            long firstDayInNextPeriod = mCycleData.get(i+1).getFirstDay();
            long firstDayInCurrentPeriod = mCycleData.get(i).getFirstDay();
            long diffInDays = TimeUnit.DAYS.convert(firstDayInNextPeriod - firstDayInCurrentPeriod, TimeUnit.MILLISECONDS);
            mCycleData.get(i).setPeriodDuration((int)diffInDays);
        }
        long today = Calendar.getInstance().getTimeInMillis();
        long firstDayInCurrentPeriod = mCycleData.get(i).getFirstDay();
        long diffInDays = TimeUnit.DAYS.convert(today - firstDayInCurrentPeriod, TimeUnit.MILLISECONDS);
        mCycleData.get(i).setPeriodDuration((int)diffInDays);
    }

    private int findPeriod(long firstDayInMillis) {

        for (int i=0; i< mCycleData.size(); i++){
            if (mCycleData.get(i).getFirstDay() > firstDayInMillis)
                return -1*(i);
            if (mCycleData.get(i).getFirstDay() == firstDayInMillis)
                return i;
        }
        return -1*mCycleData.size();
    }

    private long getFirstDayInMillis(PeriodCalendarEntry entry) {
        long offset = -1;
        if (entry.isPeriod())
            offset += entry.getPeriod();
        else if (entry.isOvulationDay())
            offset += entry.getOvulationDay();
        else
            offset = 0;
        offset *= Constants.Numeric.dayToMillis;
        long firstDayInMillis = entry.getTimeInSec()*1000;
        firstDayInMillis -= offset;
        return firstDayInMillis;
    }

    public int addPeriodToList (PeriodCycleEntry period){
		Logger.d (TAG, "addPeriodToList");
        Logger.d (TAG, "before adding");
        showAllPeriod();
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTimeInMillis(period.getFirstDay());
        Calendar lastDay = Calendar.getInstance();
		Logger.d (TAG, "firstDay: " + firstDay.get(Calendar.DAY_OF_MONTH) + "." + firstDay.get(Calendar.MONTH));
        int i;
        if (mCycleData.isEmpty()) {
            mCycleData.add(period);
            return 0;
        }
        for (i=0; i<mCycleData.size(); i++){
            //Logger.d (TAG, "firstDay " + i + " : " + mCycleData.get(i).getFirstDay());
            lastDay.setTimeInMillis(mCycleData.get(i).getFirstDay());
            //lastDay.add(Calendar.DATE, mCycleData.get(i).getPeriodDuration());
            Logger.d(TAG, "lastDay: " + lastDay.getTimeInMillis()+ " date: " + lastDay.get(Calendar.DAY_OF_MONTH) + "." + lastDay.get(Calendar.MONTH) + "." + lastDay.get(Calendar.YEAR) + "  firstDay: " + mCycleData.get(i).getFirstDay() + " periodDuration: " + mCycleData.get(i).getPeriodDuration());
            if (lastDay.after(firstDay)){
                mCycleData.add(i, period);
                break;
            }
            if (i == mCycleData.size()-1) {
                mCycleData.add(period);
                i++;
                break;
            }
        }
		Logger.d(TAG, "Find period place!!! " + i);
        Logger.d (TAG, "after adding");
        showAllPeriod();
        /*// if previous period exists -> decrease its duration
        if (i > 0 && mCycleData.get(i-1) != null && isNewPeriod) {
            long firstDayInPreviousPeriod = mCycleData.get(i - 1).getFirstDay();
            long firstDayInCurrentPeriod = mCycleData.get(i).getFirstDay();
            long diffInDays = TimeUnit.DAYS.convert(firstDayInCurrentPeriod - firstDayInPreviousPeriod, TimeUnit.MILLISECONDS);
            mCycleData.get(i - 1).setPeriodDuration((int) diffInDays);
            updatePeriod2(i - 1);
        }
        // if next period exists -> set period duration in current period
        if (i+1<mCycleData.size() && mCycleData.get(i + 1) != null){
            long firstDayInNextPeriod = mCycleData.get(i+1).getFirstDay();
            long firstDayInCurrentPeriod = mCycleData.get(i).getFirstDay();
            long diffInDays = TimeUnit.DAYS.convert(firstDayInNextPeriod - firstDayInCurrentPeriod, TimeUnit.MILLISECONDS);
            Logger.d (TAG,"set current period's duration: " + diffInDays);
            mCycleData.get(i).setPeriodDuration((int) diffInDays);
        }*/
        return i;
    }

    public void deletePeriod(PeriodCalendarEntry entry, boolean isDeletedPeriod){
        int index = findPeriod(getFirstDayInMillis(entry));
        if (index < 0){
            Logger.d (TAG, "NOT DELETE");
            Logger.d (TAG, "entry date: " + entry.getDateString());
            Logger.d (TAG, "getFirstDayInMillis(entry): " + getFirstDayInMillis(entry));
            for (PeriodCycleEntry period : mCycleData)
            Logger.d(TAG, "period get First Day: " + period.getFirstDay());
            return;
        }
        Logger.d (TAG, "deletePeriod: index" + index);
        PeriodCycleEntry period = mCycleData.get(index);
        Logger.d (TAG, "menstrual duration" + period.getMentsrualDuration());
        Logger.d (TAG, "FertileFirstDay" + period.getFertileFirstDay());
        Logger.d (TAG, "date in Sec: " + period.getFirstDay()/1000);

        // delete menstruation
        long dateInSec = period.getFirstDay()/1000;
        for (int i=0; i<period.getMentsrualDuration(); i++){
            if(mCalendarData.containsKey(dateInSec)) {
                mCalendarData.get(dateInSec).setMenstrualPeriod(0);
                mCalendarData.get(dateInSec).setMenstrualFlowAmount(0);
            }
            if(mCalendarEvents.containsKey(dateInSec))
                mCalendarEvents.get(dateInSec).setIsPeriod(false);
            dateInSec += Constants.Numeric.dayToSec;
        }
        dateInSec = period.getFirstDay()/1000;
        dateInSec += period.getFertileFirstDay() * Constants.Numeric.dayToSec;

        for (int i=0; i<7; i++){
            if(mCalendarEvents.containsKey(dateInSec))
                mCalendarEvents.get(dateInSec).setIsFertilePeriod(false);
            dateInSec += Constants.Numeric.dayToSec;
        }
        // delete ovulation
        dateInSec = period.getFirstDay()/1000;
        dateInSec += period.getOvulationDay() * Constants.Numeric.dayToSec;
        if (mCalendarData.containsKey(dateInSec))
            mCalendarData.get(dateInSec).setOvulationDay(0);
        if(mCalendarEvents.containsKey(dateInSec))
            mCalendarEvents.get(dateInSec).setIsOvulation(false);
        // after deleting all event and pcentry, we should correct previous month
        mCycleData.remove(index);
        if (isDeletedPeriod && index > 0){
            updatePeriod2(index - 1);
        }
       // else
        //updatePeriod(index-1);

    }

    public void showAllEntries (){
        for (long entryKey : mCalendarData.keySet()){
            Logger.d(TAG, "key: " + entryKey + " date:" + mCalendarData.get(entryKey).getDateString() + "  " + mCalendarData.get(entryKey).getTimeInSec());
        }
    }

	public void showAllPeriod (){
        for (PeriodCycleEntry period : mCycleData){
            Logger.d(TAG, "period.getFirstDay(): " + period.getFirstDay()+ "firstDayDate" + period.getFirstDayCalendar().get(Calendar.DAY_OF_MONTH) + "." +  period.getFirstDayCalendar().get(Calendar.MONTH) + " periodDuration" + period.getPeriodDuration());
        }
    }

    // call when a lot of/all entries were updated/created
    public HashMap<Long, Event> setEntryEvents(){
        for (PeriodCalendarEntry entry : mCalendarData.values()){
            addEntryEvent(entry.getTimeInSec());
        }
        return mCalendarEvents;
    }

    // call when 1 entry was updated/created
    public HashMap<Long, Event> addEntryEvent(long entryDateInSec){
        PeriodCalendarEntry mEntry = mCalendarData.get(entryDateInSec);
        if (mEntry == null){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(entryDateInSec*1000);
            Logger.d (TAG, "entry: is null: " + cal.get(Calendar.DAY_OF_MONTH)+"."+cal.get(Calendar.MONTH)+"."+cal.get(Calendar.YEAR));
            return null;
        }
        Event event;
        if (mCalendarEvents.containsKey(entryDateInSec))
            event = mCalendarEvents.get(entryDateInSec);
        else
            event = new Event(entryDateInSec*1000);
        event.setIsPeriod(mEntry.isPeriod());
        event.setIsOvulation(mEntry.isOvulationDay());
        event.setIsIntercourse(mEntry.isIntercourse());
        event.setIsPain(false);
        event.setIsTextNote(mEntry.getNote() != null && !TextUtils.isEmpty(mEntry.getNote()) && !TextUtils.equals(mEntry.getNote(), ""));
        mCalendarEvents.put(event.getTimeInSec(), event);
        return mCalendarEvents;
    }
    // call when 1 entry should be deleted
    public HashMap<Long, Event> deleteEntryEvent(){

        return mCalendarEvents;
    }

    // call when one period was updated/created
    public HashMap<Long, Event> addPeriod (int index){
        addEventsFromPeriod(mCycleData.get(index));
        return mCalendarEvents;
    }

    // call when one period was udeleted
   // public HashMap<Long, Event> deletePeriod (){ return mCalendarEvents;  }

    // call when a lot of/all period was updated/created
    public HashMap<Long, Event> setPeriods (){
        for (PeriodCycleEntry period : mCycleData) {
            addEventsFromPeriod(period);
        }
        return mCalendarEvents;
    }

    public void addEventsFromPeriod (PeriodCycleEntry newPeriod){
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(newPeriod.getFirstDay());
        for (int i=0; i<newPeriod.getMentsrualDuration(); i++){
            Event event;
            if (mCalendarEvents.containsKey(day.getTimeInMillis()/1000))
                event = mCalendarEvents.get(day.getTimeInMillis()/1000);
            else
                event = new Event(day.getTimeInMillis());
            event.setIsPeriod(true);
            mCalendarEvents.put(event.getTimeInSec(), event);
            day.add(Calendar.DATE, 1);
        }
        // if period is not enough long to contain fertile days
        if (newPeriod.getFertileFirstDay() == 0)
            return;

        day.setTimeInMillis(newPeriod.getFirstDay());
        day.add(Calendar.DATE, newPeriod.getFertileFirstDay());
        for (int i = 0; i < 7; i++) {
            Event event;
            if (mCalendarEvents.containsKey(day.getTimeInMillis() / 1000))
                event = mCalendarEvents.get(day.getTimeInMillis() / 1000);
            else
                event = new Event(day.getTimeInMillis());
            if (i == 5)
                event.setIsOvulation(true);
            else
                event.setIsFertilePeriod(true);
            mCalendarEvents.put(event.getTimeInSec(), event);
            day.add(Calendar.DATE, 1);
        }
    }

    public void updatePeriod(int index){
        Logger. d(TAG, "update period at index: " + index);
        if (index < 0 || mCycleData.size() <= index)
            return;
        PeriodCycleEntry period = mCycleData.get(index);
        // if period became less longer than menstruation
        if (period.getMentsrualDuration() > period.getPeriodDuration()){
            period.setMentsrualDuration(period.getPeriodDuration());
        }
        if (period.getPeriodDuration() / 2 > period.getMentsrualDuration()){
            period.setFertileFirstDay(period.getPeriodDuration() / 2 - 5);
            period.setOvulationDay(period.getPeriodDuration() / 2);
        } // if period is not enough long to contains fertile days
        else {
            period.setFertileFirstDay(0);
            period.setOvulationDay(0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(period.getFirstDay());
        for (int i = 2; i<= period.getMentsrualDuration(); i++) {
            calendar.add(Calendar.DATE, 1);
            Long timeInSec = calendar.getTimeInMillis()/1000;
            if (mCalendarData.containsKey(timeInSec)){
                PeriodCalendarEntry existEntry = mCalendarData.get(timeInSec);
                existEntry.setMenstrualPeriod(i);
                Logger.d(TAG, "period entry exist");
            } else {
                Logger.d(TAG, "new period entry: " + calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR));
                PeriodCalendarEntry newEntry = new PeriodCalendarEntry(calendar);
                newEntry.setMenstrualPeriod(i);
                mCalendarData.put(timeInSec, newEntry);
                Logger.d (TAG, "mCalendarData.size(): + " + mCalendarData.size());
            }
        }
        refreshEvents();
    }


    public void updatePeriod2 (int index){
        Logger.d(TAG, "updatePeriod2");
        // find period
        PeriodCycleEntry period = mCycleData.get(index);
        Calendar calendar = period.getFirstDayCalendar();
        Logger.d (TAG, "firstDayDeleted period: " + calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH));

		Logger.d(TAG, "before deleting");
        showAllPeriod();

        // delete period
        PeriodCalendarEntry entry = mCalendarData.get(period.getFirstDay()/1000);
        Logger. d(TAG, "entry in deleted period date: " + entry.getDateString());
        deletePeriod(entry, false);

		Logger.d(TAG, "after deleting");
        showAllPeriod();

        // create period
        createPeriod(period.getFirstDay(), false);
        int newIndex = findPeriod(period.getFirstDay());
        Logger.d (TAG, "last index:" + index + " new index: " + newIndex);
        Logger.d(TAG, "after recreating");
        showAllPeriod();

        // add period events
        addPeriod(newIndex);

    }

    public int createPeriod (long firstEntryDateInMillis, boolean isNewPeriod){
        Calendar calendar1  = Calendar.getInstance();
        calendar1.setTimeInMillis(firstEntryDateInMillis);
        Logger.d(TAG, "create new period: " + calendar1.get(Calendar.DAY_OF_MONTH) + "." + calendar1.get(Calendar.MONTH));
        /// create new PeriodCycleEntry
        PeriodCycleEntry newPeriod = new PeriodCycleEntry(firstEntryDateInMillis);
        int periodIndex = addPeriodToList(newPeriod);
        // if next period exists
        if (periodIndex < mCycleData.size()-1) {
            long firstDayInNextPeriod = mCycleData.get(periodIndex+1).getFirstDay();
            long diffInDays = TimeUnit.DAYS.convert(firstDayInNextPeriod - firstEntryDateInMillis, TimeUnit.MILLISECONDS);
            mCycleData.get(periodIndex).setPeriodDuration((int) diffInDays);
            Logger.d (TAG, "setPeriodDuration: " + diffInDays);
        }
        // if next period doesn't exist
        else
            newPeriod.setPeriodDuration(PeriodDataKeeper.periodDuration);

        newPeriod.setMentsrualDuration(PeriodDataKeeper.menstrualDuration);
        newPeriod.setFertileFirstDay(newPeriod.getPeriodDuration() / 2 - 5);
        newPeriod.setOvulationDay(newPeriod.getPeriodDuration() / 2);

        // if period is less longer than menstruation
        if (newPeriod.getMentsrualDuration() > newPeriod.getPeriodDuration()){
            Logger.d (TAG, "period is less longer than menstruation");
            newPeriod.setMentsrualDuration(newPeriod.getPeriodDuration());
        }
        // if half of period duration is longer than menstrual duration
        if (newPeriod.getPeriodDuration() / 2 > newPeriod.getMentsrualDuration() + 5){
            Logger.d (TAG, "half of period duration is longer than menstrual duration");
            newPeriod.setFertileFirstDay(newPeriod.getPeriodDuration() / 2 - 5);
            newPeriod.setOvulationDay(newPeriod.getPeriodDuration() / 2);
        } // if period is not enough long to contains fertile days
        else {
            Logger.d (TAG, "if period is not enough long to contains fertile days");
            newPeriod.setFertileFirstDay(0);
            newPeriod.setOvulationDay(0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newPeriod.getFirstDay());
        for (int i = 1; i<= newPeriod.getMentsrualDuration(); i++) {
            Long timeInSec = calendar.getTimeInMillis()/1000;
            if (mCalendarData.containsKey(timeInSec)){
                PeriodCalendarEntry existEntry = mCalendarData.get(timeInSec);
                existEntry.setMenstrualPeriod(i);
                Logger.d(TAG, "period entry exist");
            } else {
                Logger.d(TAG, "new period entry: " + calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR));
                PeriodCalendarEntry newEntry = new PeriodCalendarEntry(calendar);
                newEntry.setMenstrualPeriod(i);
                mCalendarData.put(timeInSec, newEntry);
                Logger.d (TAG, "mCalendarData.size(): + " + mCalendarData.size());
            }
            calendar.add(Calendar.DATE, 1);
        }
 		// set ovulation day
        calendar.setTimeInMillis(newPeriod.getFirstDay());
        calendar.add(Calendar.DATE, newPeriod.getOvulationDay());
        long dateInSec = calendar.getTimeInMillis()/1000;
        if (mCalendarData.containsKey(dateInSec))
            mCalendarData.get(dateInSec).setOvulationDay(newPeriod.getFertileFirstDay());
        else {
            PeriodCalendarEntry newEntry = new PeriodCalendarEntry(calendar);
            newEntry.setOvulationDay(newPeriod.getFertileFirstDay());
            mCalendarData.put(newEntry.getTimeInSec(), newEntry);
        }

        // update previous period
        if (isNewPeriod && periodIndex > 0)
            updatePeriod2(periodIndex - 1);
        return periodIndex;
    }

    public void refreshEvents(){
        mCalendarEvents.clear();
        setEntryEvents();
        setPeriods();
    }

	public String getPeriodInformation(){
        String resultString = null;
        for (PeriodCycleEntry period : mCycleData){
            Calendar calendar = period.getFirstDayCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);
            dateFormat.setTimeZone(calendar.getTimeZone());
            resultString += dateFormat.format(calendar.getTime());
            resultString += " - ";
            calendar.add(Calendar.DATE, period.getPeriodDuration());
            resultString += dateFormat.format(calendar.getTime());
            resultString += "\nPeriod Duration: ";
            resultString += period.getPeriodDuration() + "\n";
        }
        return resultString;
	}

    public void endPeriod(long timeInSec){
        PeriodCalendarEntry lastMenstrualEntry = mCalendarData.get(timeInSec);
        if (lastMenstrualEntry == null)
            return;



    }

}
