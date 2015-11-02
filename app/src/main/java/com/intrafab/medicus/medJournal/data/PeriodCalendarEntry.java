package com.intrafab.medicus.medJournal.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.intrafab.medicus.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

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

    private final String type = "medicuspcentry";
    private int id;
    private int year;                           // 	required field
    private int month;                          // 	required field
    private int day;                            // 	required field
    private int is_menstrual_period = 0;          // false by default
    private int menstrual_flow_ammount = 0;    // 0 by default
    private int is_ovulation_period;            // false by default
    private int is_intercourse = 0;             // 0 by default
    private int body_temperature = 0;          // -1 by default
    private String note;                        // empty by default
    private String moods;                       // empty by default
    private String symptoms;                    // empty by default
    private int changed;

    // при добавлении события нужно обязательно написать проверку на наличие изменений
    // чтобы не была пустых записей содержащих только одну дату

    /*public boolean isFertilePeriod() {
        return (isFertilePeriod > 0);
    }

    public void setIsFertilePeriod(boolean isFertilePeriod) {
        this.isFertilePeriod = isFertilePeriod ? 1 : 0;
    }

    public boolean isExpectedPeriod() {
        return (isExpectedPeriod > 0);
    }

    public void setIsExpectedPeriod(boolean isExpectedPeriod) {
        this.isExpectedPeriod = isExpectedPeriod ? 1 : 0;
    }
*/
    protected PeriodCalendarEntry(Parcel source) {
        id = source.readInt();
        year = source.readInt();
        month = source.readInt();
        day = source.readInt();
        is_menstrual_period = source.readInt();
        menstrual_flow_ammount = source.readInt();
        is_ovulation_period = source.readInt();
        is_intercourse = source.readInt();
        body_temperature = source.readInt();
        note = source.readString();
        moods = source.readString();
        symptoms = source.readString();
        changed = source.readInt();
    }

    public PeriodCalendarEntry(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        changed = (int)Calendar.getInstance().getTimeInMillis()/1000;
        Logger.d("PeriodCalendarEntry", "changed: "+String.valueOf(changed));
    }

    public PeriodCalendarEntry(Calendar date) {
        this.year = date.get(Calendar.YEAR);
        this.month = date.get(Calendar.MONTH);
        this.day = date.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(is_menstrual_period);
        parcel.writeInt(menstrual_flow_ammount);
        parcel.writeInt(is_ovulation_period);
        parcel.writeInt(is_intercourse);
        parcel.writeInt(body_temperature);
        parcel.writeString(note);
        parcel.writeString(moods);
        parcel.writeString(symptoms);
        parcel.writeInt(changed);
    }
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public long getTimeInSec() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        return cal.getTimeInMillis()/1000;
    }

    public Calendar getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        return calendar;
    }

    public String getDateString(){
        return day + "." + month + "." + year;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void setMenstrualPeriod(int isPeriod) {
        is_menstrual_period = isPeriod;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public boolean isPeriod() {
        return (is_menstrual_period > 0);
    }

    public int getPeriod() {
        return is_menstrual_period;
    }

    public void setMenstrualFlowAmount(int menstrualFlowAmount) {
        this.menstrual_flow_ammount = menstrualFlowAmount;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public int getMenstrualFlowAmount() {
        return menstrual_flow_ammount;
    }

    public void setOvulationDay(int isOvulationDay) {
        this.is_ovulation_period = isOvulationDay;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public boolean isOvulationDay() {
        return (is_ovulation_period > 0);
    }

    public int getOvulationDay() { return is_ovulation_period;}

    public void setIntercourse(boolean isIntercourse) {
        if (isIntercourse)
            this.is_intercourse = ENABLED;
        else
            this.is_intercourse = DISABLED;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public boolean isIntercourse() {
        return (is_intercourse == ENABLED);
    }

    public void setBodyTemperature(int bodyTemperature) {
        this.body_temperature = bodyTemperature;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public int getBodyTemperature() {
        return body_temperature;
    }

    public void setNote(String note) {
        this.note = note;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public String getNote() {
        return note;
    }

    public void setMoods(String moods) {
        this.moods = moods;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public String getMoods() {
        return moods;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
        changed = (int)(Calendar.getInstance().getTimeInMillis()/1000);
    }

    public String getSymptoms() {
        return symptoms;
    }

   /* public PeriodCalendarEntry(JSONObject object) {
        if (object == null)
            return;
        if (object.has("year")) {
            this.year = object.optInt("year");
        }
        if (object.has("month")) {
            this.month = object.optInt("month");
        }
        if (object.has("day")) {
            this.day = object.optInt("day");
        }
        if (object.has("is_menstrual_period")) {
            this.is_menstrual_period = object.optInt("is_menstrual_period");
        }
        if (object.has("ovulation")) {
            this.is_ovulation_period = object.optInt("ovulation");
        }
        if (object.has("intercourse")) {
            this.is_intercourse = object.optInt("intercourse");
        }
        if (object.has("body_temperature")) {
            this.body_temperature = object.optInt("body_temperature");
        }
        if (object.has("body_temperature")) {
            this.body_temperature = object.optInt("body_temperature");
        }
        if (object.has("body_temperature")) {
            this.body_temperature = object.optInt("body_temperature");
        }
        if (object.has("note")){
            this.note = object.optString("note");
        }
        if (object.has("moods")) {
            moods = object.optString("moods");
        }
        if (object.has("symptoms")){
            symptoms = object.optString("symptoms");
        }
    }
*/
    public String toJson() throws JSONException {
        JSONObject root = new JSONObject();
        root.put("year", year);
        root.put("month", month);
        root.put("day", day);
        root.put("is_menstrual_period", is_menstrual_period);
        root.put("ovulation", is_ovulation_period);
        root.put("intercourse", is_intercourse);
        root.put("body_temperature", body_temperature);
        root.put("moods", moods);
        root.put("symptoms", symptoms);
        Logger.d("JSON object ", root.toString().replace("\\",""));
        return root.toString().replace("\\","") ;
    }

    public static String intArrayToString(int[] array){
        StringBuilder builder = new StringBuilder();
        for (int i=0; i< array.length; i++) {
            builder.append(array[i]);
            builder.append(',');
        }
        return builder.toString();
    }

    public boolean hasPositiveValue (String array){
        if (array == null || TextUtils.isEmpty(array))
            return false;
        String[] splittedStr = array.split(",");
        for (String value : splittedStr)
            if (Integer.valueOf(value) == 1)
                return true;
        return false;
    }

    public boolean isEmpty(){
        if (is_menstrual_period > 0)
            return false;
        if (is_ovulation_period == ENABLED)
            return false;
        else if (is_intercourse == ENABLED)
            return false;
        else if (menstrual_flow_ammount > 0)
            return false;
        else if (!(TextUtils.isEmpty(note)) && !TextUtils.equals(note, ""))
            return false;
        else if (hasPositiveValue(moods))
            return false;
        return (hasPositiveValue(symptoms));
        //return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodCalendarEntry)) return false;

        PeriodCalendarEntry that = (PeriodCalendarEntry) o;

        if (year != that.year) return false;
        if (month != that.month) return false;
        if (day != that.day) return false;
        if (is_menstrual_period != that.is_menstrual_period) return false;
        if (menstrual_flow_ammount != that.menstrual_flow_ammount) return false;
        if (is_ovulation_period != that.is_ovulation_period) return false;
        if (is_intercourse != that.is_intercourse) return false;
        if (body_temperature != that.body_temperature) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (moods != null ? !moods.equals(that.moods) : that.moods != null) return false;
        return !(symptoms != null ? !symptoms.equals(that.symptoms) : that.symptoms != null);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + year;
        result = 31 * result + month;
        result = 31 * result + day;
        result = 31 * result + is_menstrual_period;
        result = 31 * result + menstrual_flow_ammount;
        result = 31 * result + is_ovulation_period;
        result = 31 * result + is_intercourse;
        result = 31 * result + body_temperature;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (moods != null ? moods.hashCode() : 0);
        result = 31 * result + (symptoms != null ? symptoms.hashCode() : 0);
        return result;
    }
}