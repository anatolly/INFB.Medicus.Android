package com.intrafab.medicus.pedometer;

import android.content.Context;
import android.os.Vibrator;

import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class BuzzerNotifier implements NotifyListener {
    private static final String TAG = BuzzerNotifier.class.getName();

    private Vibrator mVibrator;

    public BuzzerNotifier(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onStep() {
        Logger.d(TAG, "onStep");
        if (mVibrator != null)
            mVibrator.vibrate(50);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }
}
