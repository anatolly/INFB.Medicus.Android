package com.intrafab.medicus.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.intrafab.medicus.utils.Logger;

import java.util.ArrayList;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepDetector implements SensorEventListener {
    protected static final String TAG = StepDetector.class.getName();

    protected static final float THRESHOLD_AMPLITUDE = 1.0f;
    protected static final int THRESHOLD_INACTIVITY = 10;
    //protected static final int SENSOR_RATE = 60000; //SensorManager.SENSOR_DELAY_UI;
    public static final int MOVEMENT_NONE = 0;
    public static final int MOVEMENT_FORWARD = 1;
    public static final int MOVEMENT_BACKWARD = 2;

    protected ArrayList<NotifyListener> mListeners;
    private float mLastZ;
    private int mLastActivity = MOVEMENT_NONE;
    private int mInactivityCount = 0;

    private SimpleFilter[] mFiltersCascade = new SimpleFilter[3];

    public StepDetector() {
        mListeners = new ArrayList<>();
        mFiltersCascade[0] = new SimpleFilter(1, 1, 0.01f, 0.0025f);
        mFiltersCascade[1] = new SimpleFilter(1, 1, 0.01f, 0.0025f);
        mFiltersCascade[2] = new SimpleFilter(1, 1, 0.01f, 0.0025f);
    }

    public void addListener(NotifyListener listener) {
        mListeners.add(listener);
    }

    public void clearListeners() {
        mListeners.clear();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        final float z = filter(event.values[2]);
        if (Math.abs(z - mLastZ) > THRESHOLD_AMPLITUDE) {
            mInactivityCount = 0;
            int currentActivity = (z > mLastZ) ? MOVEMENT_FORWARD : MOVEMENT_BACKWARD;
            if (currentActivity != mLastActivity) {
                mLastActivity = currentActivity;
                notifyListeners(currentActivity);
            }
        } else {
            if (mInactivityCount > THRESHOLD_INACTIVITY) {
                if (mLastActivity != MOVEMENT_NONE) {
                    mLastActivity = MOVEMENT_NONE;
                    notifyListeners(MOVEMENT_NONE);
                }
            } else {
                mInactivityCount++;
            }
        }
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Smoothes the signal from accelerometer
     */
    private float filter(float measurement) {
        float f1 = mFiltersCascade[0].correct(measurement);
        float f2 = mFiltersCascade[1].correct(f1);
        float f3 = mFiltersCascade[2].correct(f2);
        return f3;
    }

    /**
     * Calls registered event listeners
     */
    private void notifyListeners(int activity) {
        if (activity == MOVEMENT_NONE)
            return;

        for (NotifyListener listener : mListeners) {
            listener.onStep(activity);
            Logger.d(TAG, String.valueOf(activity));
        }
    }
}
