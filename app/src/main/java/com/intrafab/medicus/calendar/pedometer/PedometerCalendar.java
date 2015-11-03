package com.intrafab.medicus.calendar.pedometer;

import android.os.Parcel;

import com.intrafab.medicus.calendar.data.CalendarInfo;
import com.intrafab.medicus.calendar.data.DataInfo;
import com.intrafab.medicus.calendar.data.EType;
import com.intrafab.medicus.calendar.data.EventInfo;
import com.intrafab.medicus.calendar.data.ExtraParametersInfo;
import com.intrafab.medicus.calendar.data.ParametersInfo;
import com.intrafab.medicus.utils.Logger;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Artemiy Terekhov on 28.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class PedometerCalendar extends CalendarInfo {
    public static final String TAG = PedometerCalendar.class.getName();

    private static final String ARG_UNITS = TAG.concat(".units");
    public static final String ARG_UNITS_METRIC = "metric";
    public static final String ARG_UNITS_IMPERIAL = "imperial";

    private static final String ARG_STEP_LENGTH = TAG.concat(".step_length");
    public static final float DEFAULT_STEP_LENGTH = 50f;

    private static final String ARG_BODY_WEIGHT = TAG.concat(".body_weight");
    public static final float DEFAULT_BODY_WEIGHT = 50f;

//    private static final String ARG_EXERCISE_TYPE = TAG.concat(".exercise_type");
//    public static final String ARG_TYPE_RUNNING = "running";

    private static final String ARG_MAINTAIN_OPTION = TAG.concat(".maintain_option");
//    public static final String ARG_OPTION_NONE = "none";
//    public static final String ARG_OPTION_PACE = "pace";
//    public static final String ARG_OPTION_SPEED = "speed";

    public static final int MAINTAIN_OPTION_NONE = 1;
    public static final int MAINTAIN_OPTION_PACE = 2;
    public static final int MAINTAIN_OPTION_SPEED = 3;

    private static final String ARG_DESIRED_PACE = TAG.concat(".desired_pace");
    public static final int DEFAULT_DESIRED_PACE = 68; // steps/minute

    private static final String ARG_DESIRED_SPEED = TAG.concat(".desired_speed");
    public static final float DEFAULT_DESIRED_SPEED = 2f; // km/h or mph

//    private static final String ARG_OPERATION_LEVEL = TAG.concat(".operation_level");
//    public static final String ARG_OPERATION_BACKGROUND = "run_in_background";
//    public static final String ARG_OPERATION_WAKE_UP = "wake_up";
//    public static final String ARG_OPERATION_KEEP_SCREEN_ON = "keep_screen_on";
//
//    private static final String ARG_SERVICE_RUNNING = TAG.concat(".service_running");
    private static final String ARG_LAST_CHANGED = TAG.concat(".last_changed");


    private static final String ARG_VALUE_STEPS = "steps";
    private static final String ARG_VALUE_PACE = "pace";
    private static final String ARG_VALUE_DISTANCE = "distance";
    private static final String ARG_VALUE_SPEED = "speed";
    private static final String ARG_VALUE_CALORIES = "calories";
    private static final String ARG_VALUE_TIMER = "timer";

    public PedometerCalendar() {
        super();
    }

    protected PedometerCalendar(Parcel in) {
        super(in);
    }

    public static final Creator<PedometerCalendar> CREATOR = new Creator<PedometerCalendar>() {
        @Override
        public PedometerCalendar createFromParcel(Parcel in) {
            return new PedometerCalendar(in);
        }

        @Override
        public PedometerCalendar[] newArray(int size) {
            return new PedometerCalendar[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }

    public static PedometerCalendar create() {
        PedometerCalendar calendar = new PedometerCalendar();
        calendar.setHeader("Sport Calendar");
        calendar.setSubHeader("Step counter, Calories counter");
        calendar.setDescription("Improve the sportswear");
        calendar.setId("UniPedometerCalendarId");

        return calendar;
    }

    public EventInfo createEvent() {
        Logger.d(TAG, "!!!!!!!!!createEvent");
        EventInfo info = new EventInfo();
        info.setCalendarId(getId());

        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        info.setCurrentDate(timeInMillis);
        int day = calendar.get(Calendar.DAY_OF_YEAR);

        info.setId("UniEventId" + timeInMillis);

        // add settings parameters
        info.addParameter(createStepLengthParameter());
        info.addParameter(createUnitParameter());
        info.addParameter(createWeightParameter());
        info.addParameter(createDesiredPaceParameter());
        info.addParameter(createDesiredSpeedParameter());
        info.addParameter(createMaintainOptionParameter());

        // set default parameters
        ExtraParametersInfo extra = new ExtraParametersInfo();
        extra.setKey(String.valueOf(day));
        extra.addData("calories", 0f);
        extra.addData("distance", 0f);
        extra.addData("speed", 0f);
        extra.addData("pace", 0L);
        extra.addData("steps", 0L);
        extra.addData("timer", 0L);
//        Data data = new Data();
//        data.calories = 0f;
//        data.distance = 0f;
//        data.speed = 0f;
//        data.pace = 0L;
//        data.steps = 0L;
//        data.timer = 0L;
//
//        extra.getData().put(String.valueOf(day), data);
        info.addExtraParameters(extra);

        addEvent(info);
        return info;
    }

    private ParametersInfo createStepLengthParameter() {
        ParametersInfo parameter = new ParametersInfo();
        parameter.setId(ARG_STEP_LENGTH);
        parameter.setHeader("Step Length");
        parameter.setDescription("Average length of a step");
        parameter.setType(EType.ETNumber);
        parameter.setValue(DEFAULT_STEP_LENGTH);
        return parameter;
    }

    private ParametersInfo createUnitParameter() {
        ParametersInfo parameter = new ParametersInfo();
        parameter.setId(ARG_UNITS);
        parameter.setHeader("Units");
        parameter.setDescription("Count system");
        parameter.setType(EType.ETSingleChoose);
        parameter.setValue(ARG_UNITS_METRIC);
        return parameter;
    }

    private ParametersInfo createWeightParameter() {
        ParametersInfo parameter = new ParametersInfo();
        parameter.setId(ARG_BODY_WEIGHT);
        parameter.setHeader("Weight");
        parameter.setDescription("Your current weight");
        parameter.setType(EType.ETNumber);
        parameter.setValue(DEFAULT_BODY_WEIGHT);
        return parameter;
    }

    private ParametersInfo createDesiredPaceParameter() {
        ParametersInfo parameter = new ParametersInfo();
        parameter.setId(ARG_DESIRED_PACE);
        parameter.setHeader("Desired pace");
        parameter.setDescription("Your desired pace steps/min");
        parameter.setType(EType.ETNumber);
        parameter.setValue(DEFAULT_DESIRED_PACE);
        return parameter;
    }

    private ParametersInfo createDesiredSpeedParameter() {
        ParametersInfo parameter = new ParametersInfo();
        parameter.setId(ARG_DESIRED_SPEED);
        parameter.setHeader("Desired speed");
        parameter.setDescription("Your desired speed steps/min");
        parameter.setType(EType.ETNumber);
        parameter.setValue(DEFAULT_DESIRED_SPEED);
        return parameter;
    }

    private ParametersInfo createMaintainOptionParameter() {
        ParametersInfo parameter = new ParametersInfo();
        parameter.setId(ARG_MAINTAIN_OPTION);
        parameter.setHeader("Maintain option");
        parameter.setDescription("Your maintain option (pace or speed)");
        parameter.setType(EType.ETSingleChoose);
        parameter.setValue(MAINTAIN_OPTION_NONE);
        return parameter;
    }

    public EventInfo getCurrentEvent() {
        Calendar currentCalendar = Calendar.getInstance();
        int currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        for (EventInfo info : getEvents()) {
            long timeInMillis = info.getCurrentDate();

            Calendar infoCalendar = Calendar.getInstance();
            infoCalendar.setTimeInMillis(timeInMillis);

            int infoDay = infoCalendar.get(Calendar.DAY_OF_YEAR);
            int infoYear = infoCalendar.get(Calendar.YEAR);

            if (infoYear == currentYear && infoDay == currentDay) {
                return info;
            }
        }

        return createEvent();
    }

    public boolean isMetric() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_UNITS)) {
                String value = (String) param.getValue();
                return value.equals(ARG_UNITS_METRIC);
            }
        }

        return false;
    }

    public boolean isImperial() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_UNITS)) {
                String value = (String) param.getValue();
                return value.equals(ARG_UNITS_IMPERIAL);
            }
        }

        return false;
    }

    public void saveMetricSettings(String metric) {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_UNITS)) {
                param.setValue(metric.equals(ARG_UNITS_METRIC) ? ARG_UNITS_METRIC : ARG_UNITS_IMPERIAL);
                break;
            }
        }
    }

    public float getStepLength() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_STEP_LENGTH)) {
                Object obj = param.getValue();
                if (obj instanceof Number) {
                    return ((Number) obj).floatValue();
                }
            }
        }

        return DEFAULT_STEP_LENGTH;
    }

    public void saveStepLengthSettings(float length) {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_STEP_LENGTH)) {
                param.setValue(length);
                break;
            }
        }
    }

    public float getBodyWeight() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_BODY_WEIGHT)) {
                Object obj = param.getValue();
                if (obj instanceof Number) {
                    return ((Number) obj).floatValue();
                }
            }
        }

        return DEFAULT_BODY_WEIGHT;
    }

    public void saveBodyWeightSettings(float weight) {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_BODY_WEIGHT)) {
                param.setValue(weight);
                break;
            }
        }
    }

    public int getMaintainOption() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_MAINTAIN_OPTION)) {
                Object obj = param.getValue();
                if (obj instanceof Number) {
                    return ((Number) obj).intValue();
                }
            }
        }

        return MAINTAIN_OPTION_NONE;
    }

    public int getDesiredPace() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_DESIRED_PACE)) {
                Object obj = param.getValue();
                if (obj instanceof Number) {
                    return ((Number) obj).intValue();
                }
            }
        }

        return DEFAULT_DESIRED_PACE;
    }

    public float getDesiredSpeed() {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_DESIRED_SPEED)) {
                Object obj = param.getValue();
                if (obj instanceof Number) {
                    return ((Number) obj).floatValue();
                }
            }
        }

        return DEFAULT_DESIRED_SPEED;
    }

    public void saveMaintainSetting(int maintain, float desired) {
        EventInfo info = getCurrentEvent();
        for (ParametersInfo param : info.getParameters()) {
            if (param.getId().equals(ARG_MAINTAIN_OPTION)) {
                param.setValue(maintain);
            } else if (param.getId().equals(ARG_DESIRED_PACE)) {
                if (maintain == MAINTAIN_OPTION_PACE) {
                    param.setValue((int) desired);
                }
            } else if (param.getId().equals(ARG_DESIRED_SPEED)) {
                if (maintain == MAINTAIN_OPTION_SPEED) {
                    param.setValue(desired);
                }
            }
        }
    }

    // TODO save every hour
    public void saveHourInfo(long stepCount) {
        EventInfo info = getCurrentEvent();
//        ExtraParametersInfo extra = info.getExtraParameters();
//        if (extra != null) {
//            Calendar currentCalendar = Calendar.getInstance();
//            int hourOfDay = currentCalendar.get(Calendar.HOUR_OF_DAY);
//
//            extra.getData().put(String.valueOf(hourOfDay), new DataInfo(ARG_VALUE_STEPS, String.valueOf(stepCount)));
//        }
    }

    public void saveData(Data data) {
        EventInfo info = getCurrentEvent();
        Logger.d(TAG, "calendar saveData info = " + (info == null ? "NULL" : "NOT NULL"));

        Calendar currentCalendar = Calendar.getInstance();
        int day = currentCalendar.get(Calendar.DAY_OF_YEAR);

        ExtraParametersInfo extra = info.getExtraParameters(String.valueOf(day));
        Logger.d(TAG, "calendar saveData extra = " + (extra == null ? "NULL" : "NOT NULL"));
        if (extra != null) {
            Logger.d(TAG, "calendar saveData put day = " + day + ", data = " + (data == null ? "NULL" : "NOT NULL"));

            //extra.setKey(String.valueOf(day));
            extra.addData("calories", data.calories);
            extra.addData("distance", data.distance);
            extra.addData("speed", data.speed);
            extra.addData("pace", data.pace);
            extra.addData("steps", data.steps);
            extra.addData("timer", data.timer);

            Logger.d(TAG, "saveData data = " + data.toString());
        }
    }

    public Data getData() {
        EventInfo info = getCurrentEvent();
        Logger.d(TAG, "calendar getData info = " + (info == null ? "NULL" : "NOT NULL"));
        Calendar currentCalendar = Calendar.getInstance();
        int day = currentCalendar.get(Calendar.DAY_OF_YEAR);

        ExtraParametersInfo extra = info.getExtraParameters(String.valueOf(day));
        Logger.d(TAG, "calendar getData extra = " + (extra == null ? "NULL" : "NOT NULL"));
        if (extra != null) {
            List<DataInfo> dataInfo = extra.getData();
            Logger.d(TAG, "calendar getData get day = " + day + ", data = " + (dataInfo == null ? "NULL" : "NOT NULL"));

            Data data = new Data();
            data.calories = extra.getDataValue("calories") == null ? 0.0f : ((Number) extra.getDataValue("calories")).floatValue();
            data.distance = extra.getDataValue("distance") == null ? 0.0f : ((Number) extra.getDataValue("distance")).floatValue();
            data.speed = extra.getDataValue("speed") == null ? 0.0f : ((Number) extra.getDataValue("speed")).floatValue();
            data.pace = extra.getDataValue("pace") == null ? 0L : ((Number) extra.getDataValue("pace")).longValue();
            data.steps = extra.getDataValue("steps") == null ? 0L : ((Number) extra.getDataValue("steps")).longValue();
            data.timer = extra.getDataValue("timer") == null ? 0L : ((Number) extra.getDataValue("timer")).longValue();

            Logger.d(TAG, "getData data = " + data.toString());
            return data;
        }

        return null;
    }

//    public boolean wakeAggressively() {
//        return mPreferences.getString(ARG_OPERATION_LEVEL, ARG_OPERATION_BACKGROUND).equals(ARG_OPERATION_WAKE_UP);
//    }
//
//    public boolean keepScreenOn() {
//        return mPreferences.getString(ARG_OPERATION_LEVEL, ARG_OPERATION_BACKGROUND).equals(ARG_OPERATION_KEEP_SCREEN_ON);
//    }

//    public void saveServiceRunningWithTimestamp(boolean running) {
//        SharedPreferences.Editor editor = mPreferences.edit();
//        editor.putBoolean(ARG_SERVICE_RUNNING, running);
//        editor.putLong(ARG_LAST_CHANGED, currentTimeInMillis());
//        editor.commit();
//    }
//
//    public void saveServiceRunningWithNullTimestamp(boolean running) {
//        SharedPreferences.Editor editor = mPreferences.edit();
//        editor.putBoolean(ARG_SERVICE_RUNNING, running);
//        editor.putLong(ARG_LAST_CHANGED, 0);
//        editor.commit();
//    }

//    public void clearServiceRunning() {
//        SharedPreferences.Editor editor = mPreferences.edit();
//        editor.putBoolean(ARG_SERVICE_RUNNING, false);
//        editor.putLong(ARG_LAST_CHANGED, 0);
//        editor.commit();
//    }
//
//    public boolean isServiceRunning() {
//        return mPreferences.getBoolean(ARG_SERVICE_RUNNING, false);
//    }

//    public boolean isLastPaused(long timeout) {
//        return mPreferences.getLong(ARG_LAST_CHANGED, 0) < currentTimeInMillis() - timeout;
//    }

//    public static long currentTimeInMillis() {
//        Time time = new Time();
//        time.setToNow();
//        return time.toMillis(false);
//    }
}
