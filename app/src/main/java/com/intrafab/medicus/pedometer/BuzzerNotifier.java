package com.intrafab.medicus.pedometer;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Artemiy Terekhov on 13.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class BuzzerNotifier implements NotifyListener {

    private Vibrator mVibrator;

    public BuzzerNotifier(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onStep(int activity) {
        if (mVibrator != null)
            mVibrator.vibrate(50);
    }
}
