package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class DistanceNotifier extends BaseNotifier implements NotifyListener {

    public interface Listener extends BaseListener {
        void onDistanceChanged(float value);
    }

    private float mDistance;

    public DistanceNotifier(Settings settings) {
        super(settings);
        mDistance = 0;
    }

    public void setDistance(float distance) {
        mDistance = distance;
        notifyListener();
    }

    @Override
    public void onStep(int activity) {
        if (Settings.ARG_UNITS_METRIC.equals(nUnitsType)) {
            mDistance += (float) (// kilometers
                    mStepLength // centimeters
                            / 100000.0); // centimeters/kilometer
        } else {
            mDistance += (float) (// miles
                    mStepLength // inches
                            / 63360.0); // inches/mile
        }

        notifyListener();
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener) listener).onDistanceChanged(mDistance);
            }
        }
    }
}
