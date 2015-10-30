package com.intrafab.medicus.service;

import android.app.AlarmManager;
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
import com.intrafab.medicus.medJournal.activities.MedicalJournalActivity;
import com.intrafab.medicus.medJournal.activities.PeriodCalendarActivity;
import com.intrafab.medicus.medJournal.data.ContraceptionInfo;
import com.intrafab.medicus.utils.Logger;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Анна on 08.10.2015.
 */
public class NotificationIntentService extends IntentService {

    public static final String TAG = NotificationIntentService.class.getName();
    private final int CONTRACEPTION_ALARM = 0;

    public NotificationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContraceptionInfo contraceptionInfo = intent.getParcelableExtra("contInfo");
        Logger. d(TAG, contraceptionInfo.toString());
        Logger.d(TAG, "GET INTENT@@@@@@@@@@@@@@@@@@@@");
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (contraceptionInfo.getContraceptionTypeId() == ContraceptionInfo.TYPE_RING){
            Calendar todayMillis = Calendar.getInstance();
            Calendar startDay = Calendar.getInstance();
            startDay.setTimeInMillis(contraceptionInfo.getStartDate());
            long diffInMillis = todayMillis.getTimeInMillis() - startDay.getTimeInMillis();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            diffInDays++;
            Logger.d (TAG, "TYPE_RING diffInDays: " + diffInDays);
            if (diffInDays < contraceptionInfo.getActiveDays())
                return;
        }

        Intent notificationIntent = new Intent(this, MedicalJournalActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500,500,500};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder.setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(contraceptionInfo.getNotificationTitle())
                .setContentText(contraceptionInfo.getNotificationText())
                .setContentIntent(contentIntent)
                .setSound(alarmSound)
                .setLights(Color.MAGENTA, 500, 1500)
                .setVibrate(pattern)
                .addAction(android.R.drawable.ic_menu_recent_history, "Snooze", null)
                .addAction(android.R.drawable.ic_menu_view, "Pill taken", contentIntent);
        //notification.defaults |= Notification.DEFAULT_SOUND;
        //notification.flags |= notification.FLAG_AUTO_CANCEL;
        //notification.setLatestEventInfo(getApplicationContext(), "It's about time", "You should open the app now", contentIntent);
        nm.notify(1, notificationBuilder.build());

    }

    private void setupAlarm (){

    }

    private void checkAlarm(Intent intent) {
        ContraceptionInfo contraceptionInfo = intent.getParcelableExtra("contInfo");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    }
}
