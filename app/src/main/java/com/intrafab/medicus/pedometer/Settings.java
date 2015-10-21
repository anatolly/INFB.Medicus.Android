package com.intrafab.medicus.pedometer;

import android.content.SharedPreferences;
import android.text.format.Time;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class Settings {
    public static final String TAG = Settings.class.getName();

    public static final int MAINTAIN_OPTION_NONE = 1;
    public static final int MAINTAIN_OPTION_PACE = 2;
    public static final int MAINTAIN_OPTION_SPEED = 3;

    private static final String ARG_UNITS = TAG.concat(".units");
    public static final String ARG_UNITS_METRIC = "metric";
    public static final String ARG_UNITS_IMPERIAL = "imperial";

    private static final String ARG_STEP_LENGTH = TAG.concat(".step_length");
    public static final float DEFAULT_STEP_LENGTH = 50f;

    private static final String ARG_BODY_WEIGHT = TAG.concat(".body_weight");
    public static final float DEFAULT_BODY_WEIGHT = 50f;

    private static final String ARG_EXERCISE_TYPE = TAG.concat(".exercise_type");
    public static final String ARG_TYPE_RUNNING = "running";

    private static final String ARG_MAINTAIN_OPTION = TAG.concat(".maintain_option");
    public static final String ARG_OPTION_NONE = "none";
    public static final String ARG_OPTION_PACE = "pace";
    public static final String ARG_OPTION_SPEED = "speed";

    private static final String ARG_DESIRED_PACE = TAG.concat(".desired_pace");
    public static final int DEFAULT_DESIRED_PACE = 60; // steps/minute

    private static final String ARG_DESIRED_SPEED = TAG.concat(".desired_speed");
    public static final float DEFAULT_DESIRED_SPEED = 2f; // km/h or mph

    private static final String ARG_OPERATION_LEVEL = TAG.concat(".operation_level");
    public static final String ARG_OPERATION_BACKGROUND = "run_in_background";
    public static final String ARG_OPERATION_WAKE_UP = "wake_up";
    public static final String ARG_OPERATION_KEEP_SCREEN_ON = "keep_screen_on";

    private static final String ARG_SERVICE_RUNNING = TAG.concat(".service_running");
    private static final String ARG_LAST_CHANGED = TAG.concat(".last_changed");

    private SharedPreferences mPreferences;

    public Settings(SharedPreferences settings) {
        mPreferences = settings;
    }

    public boolean isMetric() {
        return mPreferences.getString(ARG_UNITS, ARG_UNITS_METRIC).equals(ARG_UNITS_METRIC);
    }

    public boolean isImperial() {
        return mPreferences.getString(ARG_UNITS, ARG_UNITS_METRIC).equals(ARG_UNITS_IMPERIAL);
    }

    public float getStepLength() {
        return mPreferences.getFloat(ARG_STEP_LENGTH, DEFAULT_STEP_LENGTH);
    }

    public float getBodyWeight() {
        return mPreferences.getFloat(ARG_BODY_WEIGHT, DEFAULT_BODY_WEIGHT);
    }

    public boolean isRunning() {
        return mPreferences.getString(ARG_EXERCISE_TYPE, ARG_TYPE_RUNNING).equals(ARG_TYPE_RUNNING);
    }

    public int getMaintainOption() {
        return mPreferences.getInt(ARG_MAINTAIN_OPTION, MAINTAIN_OPTION_NONE);
    }

    public int getDesiredPace() {
        return mPreferences.getInt(ARG_DESIRED_PACE, DEFAULT_DESIRED_PACE);
    }

    public float getDesiredSpeed() {
        return mPreferences.getFloat(ARG_DESIRED_SPEED, DEFAULT_DESIRED_SPEED);
    }

    public void saveMaintainSetting(int maintain, float desired) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (maintain == MAINTAIN_OPTION_PACE) {
            editor.putInt(ARG_DESIRED_PACE, (int) desired);
        } else if (maintain == MAINTAIN_OPTION_SPEED) {
            editor.putFloat(ARG_DESIRED_SPEED, desired);
        }
        editor.commit();
    }

    public boolean wakeAggressively() {
        return mPreferences.getString(ARG_OPERATION_LEVEL, ARG_OPERATION_BACKGROUND).equals(ARG_OPERATION_WAKE_UP);
    }

    public boolean keepScreenOn() {
        return mPreferences.getString(ARG_OPERATION_LEVEL, ARG_OPERATION_BACKGROUND).equals(ARG_OPERATION_KEEP_SCREEN_ON);
    }

    public void saveServiceRunningWithTimestamp(boolean running) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(ARG_SERVICE_RUNNING, running);
        editor.putLong(ARG_LAST_CHANGED, currentTimeInMillis());
        editor.commit();
    }

    public void saveServiceRunningWithNullTimestamp(boolean running) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(ARG_SERVICE_RUNNING, running);
        editor.putLong(ARG_LAST_CHANGED, 0);
        editor.commit();
    }

    public void clearServiceRunning() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(ARG_SERVICE_RUNNING, false);
        editor.putLong(ARG_LAST_CHANGED, 0);
        editor.commit();
    }

    public boolean isServiceRunning() {
        return mPreferences.getBoolean(ARG_SERVICE_RUNNING, false);
    }

    public boolean isLastPaused(long timeout) {
        return mPreferences.getLong(ARG_LAST_CHANGED, 0) < currentTimeInMillis() - timeout;
    }

    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
    }
}
