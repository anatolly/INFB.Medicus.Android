package com.intrafab.medicus.pedometer;

import com.intrafab.medicus.calendar.pedometer.SettingsInfo;
import com.intrafab.medicus.utils.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Artemiy Terekhov on 29.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class TimerNotifier extends BaseNotifier implements NotifyListener {
    private static final String TAG = TimerNotifier.class.getName();

    public interface Listener extends BaseListener {
        void onTimerChanged(long value);
    }

    private class StepTimerTask extends TimerTask {
        @Override
        public void run() {
            mTimerValue++;
            //mUpdateHandler.sendEmptyMessage(0);
            notifyListener();
        }
    }

//    private Handler mUpdateHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            notifyListener();
//        }
//    };

    private long mTimerValue = 0;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private boolean mIsRunning = false;

    public void setTimer(long timerValue) {
        mTimerValue = timerValue;
        notifyListener();
    }

    private void initTimer() {
        mTimer = new Timer();
        mTimerTask = new StepTimerTask();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void cleanTimer() {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void resetStepsCount() {
        mTimerValue = 0;
    }

    public TimerNotifier(SettingsInfo settings) {
        super(settings);
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener) listener).onTimerChanged(mTimerValue);
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
        if (!mIsRunning) {
            initTimer();
            mIsRunning = true;
        }
    }

    @Override
    public void onStop() {
        if (mIsRunning) {
            cleanTimer();
            mIsRunning = false;
        }
    }
}
