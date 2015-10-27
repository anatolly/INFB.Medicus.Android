package com.intrafab.medicus.pedometer;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.intrafab.medicus.utils.Logger;

/**
 * Created by Artemiy Terekhov on 27.10.2015.
 * Copyright (c) 2015 Artemiy Terekhov. All rights reserved.
 */
public class SimpleWakefulBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This is the Intent to deliver to our service.
        Intent service = new Intent(context, SimpleWakefulService.class);

        // Start the service, keeping the device awake while it is launching.
        Logger.d("SimpleWakefulBroadcastReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, service);

    }
}
