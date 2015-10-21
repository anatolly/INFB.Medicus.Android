package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class CaloriesNotifier extends BaseNotifier implements NotifyListener {

    public interface Listener extends BaseListener {
        void onCaloriesChanged(float value);
    }

    private static final double METRIC_RUNNING_FACTOR = 1.02784823;
    private static final double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    private static final double METRIC_WALKING_FACTOR = 0.708;
    private static final double IMPERIAL_WALKING_FACTOR = 0.517;

    private float mCalories;

    public CaloriesNotifier(Settings settings) {
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
    public void onStep(int activity) {
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
}
