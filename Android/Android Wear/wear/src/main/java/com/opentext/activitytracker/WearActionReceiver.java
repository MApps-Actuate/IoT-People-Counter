package com.opentext.activitytracker;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WearActionReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_ID_STRING = "notification_id";
    public static final String WEAR_ACTION = "WearAction";
    public static final int SNOOZE_NOTIFICATION = 1;

    @Override
    public void onReceive (Context context, Intent intent) {

        if (intent != null) {

            int notificationId = intent.getIntExtra(NOTIFICATION_ID_STRING, 1);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);

            int action = intent.getIntExtra(WEAR_ACTION, 0);
        }
    }
}