package com.intrafab.medicus.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.intrafab.medicus.MainActivity;
import com.intrafab.medicus.PaymentActivity;
import com.intrafab.medicus.R;
import com.intrafab.medicus.utils.Logger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import 	android.support.v4.app.NotificationManagerCompat;
import android.app.Notification;

/**
 * Created by Anna on 29.09.2015.
 */
public class NotificationService extends Service {

    public static final String TAG = NotificationService.class.getName();
    NotificationManager nm;

    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "onCreate");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "onStartCommand");
        someTask();
        //server with be recreated after killing and receive all redeliver intent
        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
    }


    void someTask() {
        // 1-я часть
        //NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Text in status bar").setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis() + 30000).build();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


  /*      // 3-я часть
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("fileName", "somefile");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 2-я часть
        notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);

        // ставим флаг, чтобы уведомление пропало после нажатия
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        // отправляем
        nm.notify(1, notif);*/

        Intent resultIntent = new Intent(this, PaymentActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "onBind");
        return null;
    }
}
