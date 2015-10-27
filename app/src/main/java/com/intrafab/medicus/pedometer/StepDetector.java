package com.intrafab.medicus.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.intrafab.medicus.utils.Logger;

import java.util.ArrayList;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepDetector implements SensorEventListener {
    protected static final String TAG = StepDetector.class.getName();

//    protected static final float THRESHOLD_AMPLITUDE = 1.0f;
//    protected static final int THRESHOLD_INACTIVITY = 10;
//    //protected static final int SENSOR_RATE = 60000; //SensorManager.SENSOR_DELAY_UI;
//    public static final int MOVEMENT_NONE = 0;
//    public static final int MOVEMENT_FORWARD = 1;
//    public static final int MOVEMENT_BACKWARD = 2;

    protected ArrayList<NotifyListener> mListeners;
//    private float mLastZ;
//    private int mLastActivity = MOVEMENT_NONE;
//    private int mInactivityCount = 0;
//
//    private SimpleFilter[] mFiltersCascade = new SimpleFilter[3];

    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;

    public StepDetector() {
        mListeners = new ArrayList<>();
//        mFiltersCascade[0] = new SimpleFilter(1, 1, 0.01f, 0.0025f);
//        mFiltersCascade[1] = new SimpleFilter(1, 1, 0.01f, 0.0025f);
//        mFiltersCascade[2] = new SimpleFilter(1, 1, 0.01f, 0.0025f);

        mYOffset = 480 * 0.5f;
        mScale[0] = - (480 * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (480 * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public void addListener(NotifyListener listener) {
        mListeners.add(listener);
    }

    public void clearListeners() {
        mListeners.clear();
    }

    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
//            return;
//        }
//        final float z = filter(event.values[2]);
//        if (Math.abs(z - mLastZ) > THRESHOLD_AMPLITUDE) {
//            mInactivityCount = 0;
//            int currentActivity = (z > mLastZ) ? MOVEMENT_FORWARD : MOVEMENT_BACKWARD;
//            if (currentActivity != mLastActivity) {
//                mLastActivity = currentActivity;
//                notifyListeners(currentActivity);
//            }
//        } else {
//            if (mInactivityCount > THRESHOLD_INACTIVITY) {
//                if (mLastActivity != MOVEMENT_NONE) {
//                    mLastActivity = MOVEMENT_NONE;
//                    notifyListeners(MOVEMENT_NONE);
//                }
//            } else {
//                mInactivityCount++;
//            }
//        }
//        mLastZ = z;
        Sensor sensor = event.sensor;
        Logger.d(TAG, "onSensorChanged: " + sensor.getType());
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                if (j == 1) {
                    float vSum = 0;
                    for (int i=0 ; i<3 ; i++) {
                        final float v = mYOffset + event.values[i] * mScale[j];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == - mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > mLimit) {

                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                Logger.d(TAG, "step");
                                notifyListeners(0);
                                mLastMatch = extType;
                            }
                            else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    /**
//     * Smoothes the signal from accelerometer
//     */
//    private float filter(float measurement) {
//        float f1 = mFiltersCascade[0].correct(measurement);
//        float f2 = mFiltersCascade[1].correct(f1);
//        float f3 = mFiltersCascade[2].correct(f2);
//        return f3;
//    }

    /**
     * Calls registered event listeners
     */
    private void notifyListeners(int activity) {
//        if (activity == MOVEMENT_NONE)
//            return;

        for (NotifyListener listener : mListeners) {
            listener.onStep(activity);
            Logger.d(TAG, String.valueOf(activity));
        }
    }
}
