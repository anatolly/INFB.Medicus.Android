package com.intrafab.medicus.pedometer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.intrafab.medicus.PedometerActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepService extends Service {
    private static final String TAG = StepService.class.getName();

    private SharedPreferences mSettings;
    private Settings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    private BuzzerNotifier mBuzzerNotifier;
    private StepNotifier mStepNotifier;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private CaloriesNotifier mCaloriesNotifier;

    private NotificationManager mNotificationManager;
    private IStepCallback mCallback;

    private long mSteps;
    private long mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;

    private final IBinder mBinder = new StepBinder();

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }

    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        @Override
        public void onPaceChanged(long value) {
            mPace = value;
            if (mCallback != null) {
                mCallback.onPaceChanged(mPace);
            }
        }
    };

    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        @Override
        public void onDistanceChanged(float value) {
            mDistance = value;
            if (mCallback != null) {
                mCallback.onDistanceChanged(mDistance);
            }
        }
    };

    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        @Override
        public void onCaloriesChanged(float value) {
            mCalories = value;
            if (mCallback != null) {
                mCallback.onCaloriesChanged(mCalories);
            }
        }
    };

    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        @Override
        public void onSpeedChanged(float value) {
            mSpeed = value;
            if (mCallback != null) {
                mCallback.onSpeedChanged(mSpeed);
            }
        }
    };

    private StepNotifier.Listener mStepListener = new StepNotifier.Listener() {
        @Override
        public void onStepChanged(long value) {
            mSteps = value;
            if (mCallback != null) {
                mCallback.onStepChanged(mSteps);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG, "onBind");
        return mBinder;
    }

    public void registerCallback(IStepCallback callback) {
        mCallback = callback;
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public void onCreate() {
        Logger.i(TAG, "onCreate");
        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();

        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new Settings(mSettings);
        mState = getSharedPreferences("state", 0);

        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mStepNotifier = new StepNotifier(mPedometerSettings);
        mStepNotifier.setStepsCount(mSteps = mState.getLong("steps", 5000));
        mStepNotifier.addListener(mStepListener);

        mStepDetector.addListener(mStepNotifier);

        mPaceNotifier = new PaceNotifier(mPedometerSettings);
        mPaceNotifier.setPace(mPace = mState.getLong("pace", 0L));
        mPaceNotifier.addListener(mPaceListener);

        mStepDetector.addListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(mPedometerSettings);
        mDistanceNotifier.setDistance(mDistance = mState.getFloat("distance", 0));
        mDistanceNotifier.addListener(mDistanceListener);

        mStepDetector.addListener(mDistanceNotifier);

        mSpeedNotifier = new SpeedNotifier(mPedometerSettings);
        mSpeedNotifier.setSpeed(mSpeed = mState.getFloat("speed", 0));
        mPaceNotifier.addListener(mSpeedNotifier);
        mSpeedNotifier.addListener(mSpeedListener);

        mStepDetector.addListener(mSpeedNotifier);

        mCaloriesNotifier = new CaloriesNotifier(mPedometerSettings);
        mCaloriesNotifier.setCalories(mCalories = mState.getFloat("calories", 0));
        mCaloriesNotifier.addListener(mCaloriesListener);

        mStepDetector.addListener(mCaloriesNotifier);

        // Used when debugging:
        mBuzzerNotifier = new BuzzerNotifier(this);

        mStepDetector.addListener(mBuzzerNotifier);

        loadSettings();
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterDetector();

        mStateEditor = mState.edit();
        mStateEditor.putLong("steps", mSteps);
        mStateEditor.putLong("pace", mPace);
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putFloat("speed", mSpeed);
        mStateEditor.putFloat("calories", mCalories);
        mStateEditor.commit();

        mNotificationManager.cancel(R.string.app_name);

        super.onDestroy();

        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);
    }

    private void showNotification() {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_launcher);

        // This intent is fired when notification is clicked
        Intent pedometerIntent = new Intent(Intent.ACTION_VIEW);
        pedometerIntent.setComponent(new ComponentName(this, PedometerActivity.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, pedometerIntent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(getText(R.string.app_name));

        // Content text, which appears in smaller text below the title
        builder.setContentText("Pedometer is running");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("Current steps");

        // Will display the notification in the notification bar
        mNotificationManager.notify(R.string.app_name, builder.build());
    }

    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
            }
        }
    };

    public void loadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

//        if (mStepDetector != null) {
//            mStepDetector.setSensitivity(
//                    Float.valueOf(mSettings.getString("sensitivity", "10"))
//            );
//        }

        if (mStepNotifier != null) mStepNotifier.loadSettings();
        if (mPaceNotifier != null) mPaceNotifier.loadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.loadSettings();
        if (mSpeedNotifier != null) mSpeedNotifier.loadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.loadSettings();
    }
}
