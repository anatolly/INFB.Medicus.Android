package com.intrafab.medicus.pedometer;

import android.hardware.SensorEventListener;

import java.util.ArrayList;

/**
 * Created by Artemiy Terekhov on 27.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public abstract class BaseStepDetector implements SensorEventListener {

    protected ArrayList<NotifyListener> mListeners;

    public BaseStepDetector() {
        mListeners = new ArrayList<>();
    }

    protected void addListener(NotifyListener listener) {
        mListeners.add(listener);
    }

    protected void clearListeners() {
        mListeners.clear();
    }

    protected void notifyListeners() {
        for (NotifyListener listener : mListeners) {
            listener.onStep();
        }
    }

    protected void pause() {
        for (NotifyListener listener : mListeners) {
            listener.onPause();
        }
    }

    protected void resume() {
        for (NotifyListener listener : mListeners) {
            listener.onResume();
        }
    }

    protected void stop() {
        for (NotifyListener listener : mListeners) {
            listener.onStop();
        }
    }
}
