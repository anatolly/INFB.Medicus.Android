package com.intrafab.medicus.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationManagerCompat;
import android.content.Intent;
import 	android.support.v4.app.NotificationCompat;

import com.intrafab.medicus.R;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarActivity;
import com.intrafab.medicus.utils.Logger;

/**
 * Created by Анна on 08.10.2015.
 */
public class NotificationIntentService extends IntentService {

    public static final String TAG = NotificationIntentService.class.getName();

    public NotificationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.d(TAG, "GET INTENT@@@@@@@@@@@@@@@@@@@@");
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long when = System.currentTimeMillis();         // notification time

        Intent notificationIntent = new Intent(this, PeriodCalendarActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500,500,500};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder.setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("ALARM")
                .setContentText("Hello World!")
                .setContentIntent(contentIntent)
                .setSound(alarmSound)
                .setLights(Color.MAGENTA, 500, 1500)
                .setVibrate(pattern);
        //notification.defaults |= Notification.DEFAULT_SOUND;
        //notification.flags |= notification.FLAG_AUTO_CANCEL;
        //notification.setLatestEventInfo(getApplicationContext(), "It's about time", "You should open the app now", contentIntent);
        nm.notify(1, notificationBuilder.build());

    }
}
