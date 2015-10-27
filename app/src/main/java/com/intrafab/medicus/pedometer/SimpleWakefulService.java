package com.intrafab.medicus.pedometer;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 27.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class SimpleWakefulService extends IntentService implements SensorEventListener {
    private final String TAG = SimpleWakefulService.class.getName();

    public SimpleWakefulService() {
        super("SimpleWakefulService");
        Logger.d(TAG, "SimpleWakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.d(TAG, "onHandleIntent");
        // At this point SimpleWakefulReceiver is still holding a wake lock
        // for us.  We can do whatever we need to here and then tell it that
        // it can release the wakelock.  This sample just does some slow work,
        // but more complicated implementations could take their own wake
        // lock here before releasing the receiver's.
        //
        // Note that when using this approach you should be aware that if your
        // service gets killed and restarted while in the middle of such work
        // (so the Intent gets re-delivered to perform the work again), it will
        // at that point no longer be holding a wake lock since we are depending
        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
        // acquire a separate wake lock here.
//            Runnable runnable = new Runnable() {
//                public void run() {
//                    Logger.d(TAG, "Runnable ACTION_SCREEN_OFF");
//                    StepService.this.unregisterDetector();
//                    StepService.this.registerDetector();
//                }
//            };
//
//            new Handler().postDelayed(runnable, 500);

            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/);
            sensorManager.registerListener(this,
                    sensor,
                    1000);//SCREEN_OFF_RECEIVER_DELAY  SensorManager.SENSOR_DELAY_FASTEST

        int i = 0;
        while (i < 1000) {
            try {
                Thread.sleep(100);
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.d(TAG, "Completed service @ " + SystemClock.elapsedRealtime());
        SimpleWakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Logger.d(TAG, "onSensorChanged");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Logger.d(TAG, "onAccuracyChanged");
        }
}
