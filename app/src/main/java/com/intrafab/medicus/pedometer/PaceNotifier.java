package com.intrafab.medicus.pedometer;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class PaceNotifier extends BaseNotifier implements NotifyListener {

    public interface Listener extends BaseListener {
        void onPaceChanged(long value);
    }

    private long mLastStepTime;
    private long[] mLastStepDeltas = {-1, -1, -1, -1};
    private int mLastStepDeltasIndex;
    private long mPace;

    public PaceNotifier(Settings settings) {
        super(settings);

        mLastStepTime = 0;
        mLastStepDeltasIndex = 0;
        mPace = 0;
    }

    public void setPace(long pace) {
        mPace = pace;
        int avg = (int) (60 * 1000.0 / mPace);
        for (int i = 0; i < mLastStepDeltas.length; i++) {
            mLastStepDeltas[i] = avg;
        }
        notifyListener();
    }

    @Override
    public void onStep(int activity) {
        long thisStepTime = System.currentTimeMillis();

        // Calculate pace based on last n steps
        if (mLastStepTime > 0) {
            long delta = thisStepTime - mLastStepTime;

            mLastStepDeltas[mLastStepDeltasIndex] = delta;
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.length;

            long total = 0;
            boolean isMeaningfull = true;
            for (int i = 0; i < mLastStepDeltas.length; i++) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false;
                    break;
                }
                total += mLastStepDeltas[i];
            }
            if (isMeaningfull && total > 0) {
                long avg = total / mLastStepDeltas.length;
                mPace = 60 * 1000 / avg;
            } else {
                mPace = -1;
            }
        }
        mLastStepTime = thisStepTime;
        notifyListener();
    }

    @Override
    protected void notifyListener() {
        for (BaseListener listener : mListeners) {
            if (listener instanceof Listener) {
                ((Listener) listener).onPaceChanged(mPace);
            }
        }
    }
}
