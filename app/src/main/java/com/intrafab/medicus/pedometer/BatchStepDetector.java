package com.intrafab.medicus.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class BatchStepDetector extends BaseStepDetector {
    protected static final String TAG = BatchStepDetector.class.getName();

    private int mSteps = 0;
    private int mCounterSteps = 0;

    public BatchStepDetector() {
        super();
        Logger.d(TAG, "CREATE Batch Step Detector");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        Logger.d(TAG, "onSensorChanged: " + sensor.getType());
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                mSteps += sensorEvent.values.length;
                //sensorEvent.timestamp;

                for (int i = 0; i < sensorEvent.values.length; i++) {
                    notifyListeners();
                }

                Logger.d(TAG, "New step detected by STEP_DETECTOR sensor. Total step count: " + mSteps);

            } else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (mCounterSteps < 1) {
                    mCounterSteps = (int) sensorEvent.values[0];
                }
                mSteps = (int) sensorEvent.values[0] - mCounterSteps;

                notifyListeners();

                Logger.d(TAG, "New step detected by STEP_COUNTER sensor. Total step count: " + mSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
