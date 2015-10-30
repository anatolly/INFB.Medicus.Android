package com.intrafab.medicus.pedometer;

import com.intrafab.medicus.calendar.pedometer.SettingsInfo;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepNotifier extends BaseNotifier implements NotifyListener {
    private static final String TAG = StepNotifier.class.getName();

    public interface Listener extends BaseListener {
        void onStepChanged(long value);
    }

    private long mStepsCount = 0;

    public void setStepsCount(long stepsCount) {
        mStepsCount = stepsCount;
        notifyListener();
    }

    public void resetStepsCount() {
        mStepsCount = 0;
    }

    public StepNotifier(SettingsInfo settings) {
        super(settings);
        mStepsCount = 0;
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener) listener).onStepChanged(mStepsCount);
            }
        }
    }

    @Override
    public void onStep() {
        Logger.d(TAG, "onStep");
        mStepsCount++;
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
