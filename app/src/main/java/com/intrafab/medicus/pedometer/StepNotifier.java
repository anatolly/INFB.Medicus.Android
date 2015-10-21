package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class StepNotifier extends BaseNotifier implements NotifyListener {

    public interface Listener extends BaseListener {
        void onStepChanged(long value);
    }

    private long mStepsCount = 0;

    public void setStepsCount(long stepsCount) {
        mStepsCount = stepsCount;
        notifyListener();
    }

    public void resetStepsCount() {
        mStepsCount = 0;
    }

    public StepNotifier(Settings settings) {
        super(settings);
        mStepsCount = 0;
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener) listener).onStepChanged(mStepsCount);
            }
        }
    }

    @Override
    public void onStep(int activity) {
        mStepsCount++;
        notifyListener();
    }
}
