package com.intrafab.medicus.pedometer;

import com.intrafab.medicus.calendar.pedometer.SettingsInfo;

import java.util.ArrayList;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public abstract class BaseNotifier {

    public interface BaseListener {
    }

    protected SettingsInfo mSettings;
    protected ArrayList<BaseListener> mListeners;

    protected String nUnitsType;
    protected boolean mIsRunning;
    protected float mStepLength;
    protected float mBodyWeight;
    protected float mDesiredSpeed;
    protected int mDesiredPace;

    public BaseNotifier(SettingsInfo settings) {
        mListeners = new ArrayList<>();
        mSettings = settings;

        loadSettings();
    }

    public void addListener(BaseListener listener) {
        mListeners.add(listener);
    }

    public void clearListeners() {
        mListeners.clear();
    }

    public void loadSettings() {
        nUnitsType = mSettings.unitsType;
        mIsRunning = mSettings.isRunning;
        mStepLength = mSettings.stepLength;
        mBodyWeight = mSettings.bodyWeight;
        mDesiredSpeed = mSettings.desiredSpeed;
        mDesiredPace = mSettings.desiredPace;
        notifyListener();
    }

    protected abstract void notifyListener();
}
