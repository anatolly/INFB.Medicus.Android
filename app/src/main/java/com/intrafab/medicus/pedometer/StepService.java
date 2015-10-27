package com.intrafab.medicus.pedometer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.intrafab.medicus.PedometerActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepService extends Service implements IServiceActions {
    private static final String TAG = StepService.class.getName();

    private static final int NOTIFICATION_ID = 1001;

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

    private boolean mIsRunning;

    private long mSteps;
    private long mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;

    private PowerManager.WakeLock mWakeLock;

    private class CommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceEvent.START:
                    onStart(msg);
                    break;

                case ServiceEvent.STOP:
                    onStop(msg);
                    break;

                case ServiceEvent.PAUSE:
                    onPause(msg);
                    break;

                case ServiceEvent.RESUME:
                    onResume(msg);
                    break;
            }
        }
    }

    private final Messenger mServiceMessenger = new Messenger(new CommandHandler());
    private Messenger mOutgoingMessenger;

    @Override
    public void onStart(Message msg) {
        Logger.d(TAG, "onStart");
        mOutgoingMessenger = msg.replyTo;
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

        Bundle data = msg.getData();
        String messageNotify = data != null ? data.getString(ServiceEvent.NOTIFY_MESSAGE, "") : "";

        startInForeground(messageNotify);

        if (mOutgoingMessenger != null) {
            Message msgOutgoing = new Message();
            msgOutgoing.what = ServiceEvent.START_DETECTOR;

            try {
                mOutgoingMessenger.send(msgOutgoing);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (!mIsRunning) {
            startService(new Intent(this, StepService.class));
            mIsRunning = true;
        }
    }

    @Override
    public void onPause(Message msg) {
        Logger.d(TAG, "onPause");
        mOutgoingMessenger = msg.replyTo;
        if (mOutgoingMessenger != null) {
            Message msgOutgoing = new Message();
            msgOutgoing.what = ServiceEvent.PAUSE;

            try {
                mOutgoingMessenger.send(msgOutgoing);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(Message msg) {
        Logger.d(TAG, "onResume");
        mOutgoingMessenger = msg.replyTo;
        if (mOutgoingMessenger != null) {
            Message msgOutgoing = new Message();
            msgOutgoing.what = ServiceEvent.RESUME;

            try {
                mOutgoingMessenger.send(msgOutgoing);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop(Message msg) {
        Logger.d(TAG, "onStop");
        mOutgoingMessenger = msg.replyTo;

        unregisterReceiver(mReceiver);
        unregisterDetector();

        mStateEditor = mState.edit();
        mStateEditor.putLong("steps", mSteps);
        mStateEditor.putLong("pace", mPace);
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putFloat("speed", mSpeed);
        mStateEditor.putFloat("calories", mCalories);
        mStateEditor.commit();

        // Stop detecting
        if (mSensorManager != null)
            mSensorManager.unregisterListener(mStepDetector);

        stopForeground(true);
        Logger.d(TAG, "stopForeground");

        if (mOutgoingMessenger != null) {
            Message msgOutgoing = new Message();
            msgOutgoing.what = ServiceEvent.STOP_DETECTOR;

            try {
                mOutgoingMessenger.send(msgOutgoing);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        mIsRunning = false;
        stopSelf();
    }

    private void startInForeground(String contentText) {
        Logger.d(TAG, "startInForeground");
        Intent taskIntent = new Intent(getApplicationContext(), PedometerActivity.class);
        taskIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                taskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Logger.d(TAG, "startInForeground contentText: " + contentText);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle(getString(R.string.app_name))
                        .setTicker(contentText)
                        .setStyle(new NotificationCompat.InboxStyle().setSummaryText(contentText))
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(contentIntent);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        @Override
        public void onPaceChanged(long value) {
            mPace = value;
            if (mOutgoingMessenger != null) {
                Message msg = new Message();
                msg.what = ServiceEvent.PACE_CHANGED;
                Bundle data = new Bundle();
                data.putLong(ServiceEvent.PACE_VALUE, mPace);
                msg.setData(data);

                try {
                    mOutgoingMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        @Override
        public void onDistanceChanged(float value) {
            mDistance = value;
            if (mOutgoingMessenger != null) {
                Message msg = new Message();
                msg.what = ServiceEvent.DISTANCE_CHANGED;
                Bundle data = new Bundle();
                data.putFloat(ServiceEvent.DISTANCE_VALUE, mDistance);
                msg.setData(data);

                try {
                    mOutgoingMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        @Override
        public void onCaloriesChanged(float value) {
            mCalories = value;
            if (mOutgoingMessenger != null) {
                Message msg = new Message();
                msg.what = ServiceEvent.CALORIES_CHANGED;
                Bundle data = new Bundle();
                data.putFloat(ServiceEvent.CALORIES_VALUE, mCalories);
                msg.setData(data);

                try {
                    mOutgoingMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        @Override
        public void onSpeedChanged(float value) {
            mSpeed = value;
            if (mOutgoingMessenger != null) {
                Message msg = new Message();
                msg.what = ServiceEvent.SPEED_CHANGED;
                Bundle data = new Bundle();
                data.putFloat(ServiceEvent.SPEED_VALUE, mSpeed);
                msg.setData(data);

                try {
                    mOutgoingMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private StepNotifier.Listener mStepListener = new StepNotifier.Listener() {
        @Override
        public void onStepChanged(long value) {
            mSteps = value;
            if (mOutgoingMessenger != null) {
                Message msg = new Message();
                msg.what = ServiceEvent.STEP_CHANGED;
                Bundle data = new Bundle();
                data.putLong(ServiceEvent.STEP_VALUE, mSteps);
                msg.setData(data);

                try {
                    mOutgoingMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind");
        return mServiceMessenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Logger.d(TAG, "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(TAG, "onUnbind");
        return false;//super.onUnbind(intent);
    }

    private void registerDetector() {
        Logger.d(TAG, "registerDetector");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                1000);//SCREEN_OFF_RECEIVER_DELAY  SensorManager.SENSOR_DELAY_FASTEST
    }

    private void unregisterDetector() {
        Logger.d(TAG, "unregisterDetector");
        if (mSensorManager != null) {
            Logger.d(TAG, "unregisterDetector unregisterListener");
            mSensorManager.unregisterListener(mStepDetector);
        }
    }

    @Override
    public void onCreate() {
        Logger.i(TAG, "onCreate");
        super.onCreate();

        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new Settings(mSettings);
        mState = getSharedPreferences("state", 0);
        mIsRunning = false;
        loadSettings();
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");

        super.onDestroy();
    }

    private boolean isStop = false;

    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
//                StepService.this.unregisterDetector();
//                StepService.this.registerDetector();

//                Runnable runnable = new Runnable() {
//                    public void run() {
//                        Logger.d(TAG, "Runnable ACTION_SCREEN_OFF");
//                        StepService.this.unregisterDetector();
//                        StepService.this.registerDetector();
//                    }
//                };
//
//                new Handler().postDelayed(runnable, 500);
//                ComponentName comp = new ComponentName(context.getPackageName(),
//                        SimpleWakefulService.class.getName());
//                // Start the service, keeping the device awake while it is launching.
//                startWakefulService(context, (intent.setComponent(comp)));
//                setResultCode(Activity.RESULT_OK);

                Logger.d(TAG, "WakefulBroadcastReceiver onReceive ACTION_SCREEN_OFF");
                //Intent service = new Intent(context, SimpleWakefulService.class);
                //startWakefulService(context, service);
                //sendBroadcast(new Intent(StepService.this, SimpleWakefulBroadcastReceiver.class));
                isStop = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Logger.d(TAG, "WakefulBroadcastReceiver onReceive ACTION_SCREEN_ON");
                isStop = true;
            }
        }
    };

//    public class SimpleWakefulService extends IntentService {//implements SensorEventListener {
//        private final String TAG = SimpleWakefulService.class.getName();
//        public SimpleWakefulService() {
//            super("SimpleWakefulService");
//            Logger.d(TAG, "SimpleWakefulService");
//        }
//
//        @Override
//        protected void onHandleIntent(Intent intent) {
//            Logger.d(TAG, "onHandleIntent");
//            // At this point SimpleWakefulReceiver is still holding a wake lock
//            // for us.  We can do whatever we need to here and then tell it that
//            // it can release the wakelock.  This sample just does some slow work,
//            // but more complicated implementations could take their own wake
//            // lock here before releasing the receiver's.
//            //
//            // Note that when using this approach you should be aware that if your
//            // service gets killed and restarted while in the middle of such work
//            // (so the Intent gets re-delivered to perform the work again), it will
//            // at that point no longer be holding a wake lock since we are depending
//            // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
//            // acquire a separate wake lock here.
////            Runnable runnable = new Runnable() {
////                public void run() {
////                    Logger.d(TAG, "Runnable ACTION_SCREEN_OFF");
////                    StepService.this.unregisterDetector();
////                    StepService.this.registerDetector();
////                }
////            };
////
////            new Handler().postDelayed(runnable, 500);
//
////            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
////            Sensor sensor = sensorManager.getDefaultSensor(
////                    Sensor.TYPE_ACCELEROMETER /*|
////            Sensor.TYPE_MAGNETIC_FIELD |
////            Sensor.TYPE_ORIENTATION*/);
////            sensorManager.registerListener(this,
////                    sensor,
////                    1000);//SCREEN_OFF_RECEIVER_DELAY  SensorManager.SENSOR_DELAY_FASTEST
//
//            while (!isStop) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            Logger.d("SimpleWakefulReceiver", "Completed service @ " + SystemClock.elapsedRealtime());
//            mReceiver.completeWakefulIntent(intent);
//        }
//
////        @Override
////        public void onSensorChanged(SensorEvent sensorEvent) {
////            Logger.d(TAG, "onSensorChanged");
////        }
////
////        @Override
////        public void onAccuracyChanged(Sensor sensor, int i) {
////            Logger.d(TAG, "onAccuracyChanged");
////        }
//    }

    private void loadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        if (mStepDetector != null) {
            mStepDetector.setSensitivity(
                    Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }

        if (mStepNotifier != null) mStepNotifier.loadSettings();
        if (mPaceNotifier != null) mPaceNotifier.loadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.loadSettings();
        if (mSpeedNotifier != null) mSpeedNotifier.loadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.loadSettings();
    }
}
