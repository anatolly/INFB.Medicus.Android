package com.intrafab.medicus.pedometer;

import com.intrafab.medicus.calendar.pedometer.SettingsInfo;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class SpeedNotifier extends BaseNotifier implements NotifyListener, PaceNotifier.Listener {
    private static final String TAG = SpeedNotifier.class.getName();

    public interface Listener extends BaseListener {
        void onSpeedChanged(float value);
    }

    private float mSpeed;

    @Override
    public void onPaceChanged(long value) {
        if (Settings.ARG_UNITS_METRIC.equals(nUnitsType)) {
            mSpeed = // kilometers / hour
                    value * mStepLength // centimeters / minute
                            / 100000f * 60f; // centimeters/kilometer
        } else {
            mSpeed = // miles / hour
                    value * mStepLength // inches / minute
                            / 63360f * 60f; // inches/mile
        }

        notifyListener();
    }

    public SpeedNotifier(SettingsInfo settings) {
        super(settings);

        mSpeed = 0;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        notifyListener();
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener) listener).onSpeedChanged(mSpeed);
            }
        }
    }

    @Override
    public void onStep() {
        Logger.d(TAG, "onStep");
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
