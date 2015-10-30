package com.intrafab.medicus.pedometer;

import com.intrafab.medicus.calendar.pedometer.SettingsInfo;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class CaloriesNotifier extends BaseNotifier implements NotifyListener {
    private static final String TAG = CaloriesNotifier.class.getName();

    public interface Listener extends BaseListener {
        void onCaloriesChanged(float value);
    }

    private static final float METRIC_RUNNING_FACTOR = 1.02784823f;
    private static final float IMPERIAL_RUNNING_FACTOR = 0.75031498f;

    private static final float METRIC_WALKING_FACTOR = 0.708f;
    private static final float IMPERIAL_WALKING_FACTOR = 0.517f;

    private float mCalories;

    public CaloriesNotifier(SettingsInfo settings) {
        super(settings);

        mCalories = 0;
    }

    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }

    public void resetCalories() {
        mCalories = 0;
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener)listener).onCaloriesChanged(mCalories);
            }
        }
    }

    @Override
    public void onStep() {
        Logger.d(TAG, "onStep");
        if (Settings.ARG_UNITS_METRIC.equals(nUnitsType)) {
            mCalories +=
                    (mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                            // Distance:
                            * mStepLength // cm
                            / 100000.0; // cm/km
        } else {
            mCalories +=
                    (mBodyWeight * (mIsRunning ? IMPERIAL_RUNNING_FACTOR : IMPERIAL_WALKING_FACTOR))
                            // Distance:
                            * mStepLength // inches
                            / 63360.0; // inches/mile
        }

        notifyListener();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }
}
