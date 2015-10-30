package com.intrafab.medicus.pedometer;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.intrafab.medicus.PedometerActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.actions.ActionSavePedometerTask;
import com.intrafab.medicus.calendar.pedometer.Data;
import com.intrafab.medicus.calendar.pedometer.PedometerCalendar;
import com.intrafab.medicus.calendar.pedometer.SettingsInfo;
import com.intrafab.medicus.db.DBManager;
import com.intrafab.medicus.loaders.PedometerLoader;
import com.intrafab.medicus.utils.Logger;
import com.intrafab.medicus.utils.SupportVersion;
import com.telly.groundy.Groundy;
import com.telly.groundy.GroundyService;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepService extends GroundyService implements IServiceActions, Loader.OnLoadCompleteListener<PedometerCalendar> {
    private static final String TAG = StepService.class.getName();

    private static final int NOTIFICATION_ID = 1001;
    private static final int BATCH_LATENCY = 1000000; //1sec
    private static final int LOADER_CALENDAR_ID = 150;

    //private SharedPreferences mSettings;
    //private Settings mPedometerSettings;
    //private SharedPreferences mState;
    //private SharedPreferences.Editor mStateEditor;
    //private SensorManager mSensorManager;
    private Sensor mSensor;
    private BaseStepDetector mStepDetector;
    private BuzzerNotifier mBuzzerNotifier;
    private StepNotifier mStepNotifier;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private CaloriesNotifier mCaloriesNotifier;
    private TimerNotifier mTimerNotifier;

    private PedometerCalendar mCalendar;

    private boolean mIsRunning;

    private long mSteps;
    private long mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;
    private long mTimerValue;

//    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onLoadComplete(Loader<PedometerCalendar> loader, PedometerCalendar pedometerCalendar) {
        Logger.d(TAG, "onLoadComplete pedometerCalendar = " + (pedometerCalendar == null ? "NULL" : "NOT NULL"));
        Logger.d(TAG, "onLoadComplete mCalendar = " + (mCalendar == null ? "NULL" : "NOT NULL"));
        if (pedometerCalendar == null && mCalendar == null) {
            mCalendar = PedometerCalendar.create();
        }

        if (pedometerCalendar != null) {
            mCalendar = pedometerCalendar;
        }

        runDetector();
        //pedometerCalendar.setEvents();
    }

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

                case ServiceEvent.UPDATE:
                    onUpdate(msg);
                    break;
            }
        }
    }

    private final Messenger mServiceMessenger = new Messenger(new CommandHandler());
    private Messenger mOutgoingMessenger;

    private PedometerLoader mCalendarLoader;

    private void createPedometerLoader() {
        Logger.d(TAG, "createPedometerLoader");
        mCalendarLoader = new PedometerLoader(this) {
            @Override
            protected void onStartLoading() {
                DBManager.getInstance().registerObserver(getContext(), this, PedometerLoader.class);
                forceLoad();
            }
        };
    }

    private void resetPedometerLoader() {
        Logger.d(TAG, "resetPedometerLoader");
        if (mCalendarLoader != null)
            mCalendarLoader.reset();
    }

    private void stopPedometerLoader() {
        Logger.d(TAG, "stopPedometerLoader");
        if (mCalendarLoader != null) {
            mCalendarLoader.stopLoading();
            mCalendarLoader.unregisterListener(this);
        }
    }

    private void startPedometerLoader() {
        Logger.d(TAG, "startPedometerLoader");
        if (mCalendarLoader != null) {
            mCalendarLoader.registerListener(LOADER_CALENDAR_ID, this);
            mCalendarLoader.startLoading();
        }
    }

    private void initDetector() {
        // Start detecting
        mStepDetector = isKitkatWithStepSensor() ? new BatchStepDetector() : new StepDetector();

        SettingsInfo settings = new SettingsInfo();
        settings.unitsType = mCalendar.isMetric() ?
                PedometerCalendar.ARG_UNITS_METRIC : PedometerCalendar.ARG_UNITS_IMPERIAL;
        settings.bodyWeight = mCalendar.getBodyWeight();
        settings.desiredPace = mCalendar.getDesiredPace();
        settings.desiredSpeed = mCalendar.getDesiredSpeed();
        settings.isRunning = false; // TODO
        settings.stepLength = mCalendar.getStepLength();

        Data data = mCalendar.getData();

        mStepNotifier = new StepNotifier(settings);
        mStepNotifier.setStepsCount(mSteps = data.steps);
        mStepNotifier.addListener(mStepListener);

        mStepDetector.addListener(mStepNotifier);

        mPaceNotifier = new PaceNotifier(settings);
        mPaceNotifier.setPace(mPace = data.pace);
        mPaceNotifier.addListener(mPaceListener);

        mStepDetector.addListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(settings);
        mDistanceNotifier.setDistance(mDistance = data.distance);
        mDistanceNotifier.addListener(mDistanceListener);

        mStepDetector.addListener(mDistanceNotifier);

        mSpeedNotifier = new SpeedNotifier(settings);
        mSpeedNotifier.setSpeed(mSpeed = data.speed);
        mPaceNotifier.addListener(mSpeedNotifier);
        mSpeedNotifier.addListener(mSpeedListener);

        mStepDetector.addListener(mSpeedNotifier);

        mCaloriesNotifier = new CaloriesNotifier(settings);
        mCaloriesNotifier.setCalories(mCalories = data.calories);
        mCaloriesNotifier.addListener(mCaloriesListener);

        mStepDetector.addListener(mCaloriesNotifier);

        // Used when debugging:
        mBuzzerNotifier = new BuzzerNotifier(this);

        mStepDetector.addListener(mBuzzerNotifier);

        mTimerNotifier = new TimerNotifier(settings);
        mTimerNotifier.setTimer(mTimerValue = data.timer);
        mTimerNotifier.addListener(mTimerListener);

        mStepDetector.addListener(mTimerNotifier);

        loadSettings();
    }

    private String mNotifyMessage;

    @Override
    public void onStart(Message msg) {
        Logger.d(TAG, "onStart");
        mOutgoingMessenger = msg.replyTo;
        Bundle data = msg.getData();
        mNotifyMessage = data != null ? data.getString(ServiceEvent.NOTIFY_MESSAGE, "") : "";


        startPedometerLoader();



        //mStepDetector.notifyListeners();
    }

    private void runDetector() {
        initDetector();
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

//        Bundle data = msg.getData();
//        String messageNotify = data != null ? data.getString(ServiceEvent.NOTIFY_MESSAGE, "") : "";

        startInForeground(mNotifyMessage);

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

        if (mStepDetector != null && mIsRunning)
            mStepDetector.resume();
    }

    public boolean isKitkatWithStepSensor() {
        PackageManager packageManager = getPackageManager();
        return SupportVersion.Kitkat()
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
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

        if (mStepDetector != null)
            mStepDetector.pause();
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

        if (mStepDetector != null && mIsRunning)
            mStepDetector.resume();
    }

    public void onUpdate(Message msg) {
        Logger.d(TAG, "onUpdate");
        if (mStepDetector != null)
            mStepDetector.notifyListeners();
    }

    @Override
    public void onStop(Message msg) {
        Logger.d(TAG, "onStop");
        mOutgoingMessenger = msg.replyTo;

        unregisterReceiver(mReceiver);
        unregisterDetector();

//        mStateEditor = mState.edit();
//        mStateEditor.putLong("steps", mSteps);
//        mStateEditor.putLong("pace", mPace);
//        mStateEditor.putFloat("distance", mDistance);
//        mStateEditor.putFloat("speed", mSpeed);
//        mStateEditor.putFloat("calories", mCalories);
//        mStateEditor.putLong("timer", mTimerValue);
//        mStateEditor.commit();

        Data data = new Data();
        data.steps = mSteps;
        data.pace = mPace;
        data.distance = mDistance;
        data.speed = mSpeed;
        data.calories = mCalories;
        data.timer = mTimerValue;
        mCalendar.saveData(data);

        Groundy.create(ActionSavePedometerTask.class)
                //.callback(this)
                .arg(ActionSavePedometerTask.PEDOMETER_DATA, mCalendar)
                .service(StepService.class).queueUsing(this);

        stopPedometerLoader();

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

        if (mStepDetector != null) {
            mStepDetector.stop();
            mStepDetector = null;
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

    private TimerNotifier.Listener mTimerListener = new TimerNotifier.Listener() {
        @Override
        public void onTimerChanged(long value) {
            mTimerValue = value;
            if (mOutgoingMessenger != null) {
                Message msg = new Message();
                msg.what = ServiceEvent.TIMER_CHANGED;
                Bundle data = new Bundle();
                data.putLong(ServiceEvent.TIMER_VALUE, mTimerValue);
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

    @SuppressLint("NewApi")
    private void registerDetector() {
        Logger.d(TAG, "registerDetector");
        boolean isKitKatStepSensor = isKitkatWithStepSensor();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        int sensorType = isKitKatStepSensor ?
                (Sensor.TYPE_STEP_DETECTOR /* | Sensor.TYPE_STEP_COUNTER*/) :
                (Sensor.TYPE_ACCELEROMETER /*|
                Sensor.TYPE_MAGNETIC_FIELD |
                Sensor.TYPE_ORIENTATION*/);
        mSensor = sensorManager.getDefaultSensor(sensorType);
//        if (isKitKatStepSensor) {
//            sensorManager.registerListener(mStepDetector, mSensor,
//                    SensorManager.SENSOR_DELAY_NORMAL);//, BATCH_LATENCY);
//        } else {
            sensorManager.registerListener(mStepDetector, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }

    private void unregisterDetector() {
        Logger.d(TAG, "unregisterDetector");
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Logger.d(TAG, "unregisterDetector unregisterListener");
            sensorManager.unregisterListener(mStepDetector);
            sensorManager = null;
        }
    }

    @Override
    public void onCreate() {
        Logger.i(TAG, "onCreate");
        super.onCreate();

        // Load settings
//        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
//        mPedometerSettings = new Settings(mSettings);
//        mState = getSharedPreferences("state", 0);
        mIsRunning = false;

        createPedometerLoader();
        //startPedometerLoader();
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
//        resetPedometerLoader();
//        stopPedometerLoader();

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
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();

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
//        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

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
