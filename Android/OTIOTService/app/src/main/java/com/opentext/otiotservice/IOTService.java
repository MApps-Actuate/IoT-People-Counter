package com.opentext.otiotservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kclark on 9/5/2015.
 */
public class IOTService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private boolean isRunning = false;
    int mStartMode;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        super.onDestroy();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mStartMode;
    }

    public String getTime() {
        SimpleDateFormat dateformat =
                new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US);
        return (dateformat.format(new Date()));
    }

    public class MyLocalBinder extends Binder {
        public IOTService getService() {
            return IOTService.this;
        }
    }
}
