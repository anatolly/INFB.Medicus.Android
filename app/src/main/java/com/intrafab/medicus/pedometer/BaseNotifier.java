package com.intrafab.medicus.pedometer;

import java.util.ArrayList;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public abstract class BaseNotifier {

    public interface BaseListener {
    }

    protected Settings mSettings;
    protected ArrayList<BaseListener> mListeners;

    protected String nUnitsType;
    protected boolean mIsRunning;
    protected float mStepLength;
    protected float mBodyWeight;
    protected float mDesiredSpeed;
    protected int mDesiredPace;

    public BaseNotifier(Settings settings) {
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
        nUnitsType = mSettings.isMetric() ? Settings.ARG_UNITS_METRIC : Settings.ARG_UNITS_IMPERIAL;
        mIsRunning = mSettings.isRunning();
        mStepLength = mSettings.getStepLength();
        mBodyWeight = mSettings.getBodyWeight();
        mDesiredSpeed = mSettings.getDesiredSpeed();
        mDesiredPace = mSettings.getDesiredPace();
        notifyListener();
    }

    protected abstract void notifyListener();
}
