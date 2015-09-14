package com.opentext.activitytracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.opentext.otiotservice.IOTService;
import com.opentext.otiotservice.IOTService.MyLocalBinder;
import com.opentext.otiotservice.R;
import com.opentext.otiotservice.ThreasholdActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient mApiClient;
    private IOTService myService;
    private Button button;
    private int notification_id = 1;
    private final String NOTIFICATION_ID = "notification_id";
    boolean isBound = false;

    private NotificationCompat.Builder notification_builder;
    private NotificationManagerCompat notification_manager;


    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
            myService.setContext(getApplicationContext());
            showTime();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    public void sendNotification(String title, String message) {
        Intent viewIntent = new Intent(this, ThreasholdActivity.class);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // This needs to unbind the main activity from the service onDestroy() otherwise
        // the service will remain bound and when we resume the main activity we will get
        // a failure.
        if(myService != null) {
            unbindService(myConnection);
        }
    }

    public void showTime() {
        //String currentTime = myService.getTime();
        //TextView tv = (TextView) findViewById(R.id.tv);
        //tv.setText(currentTime);
    }

    private String generateHash() {
        String hash = null;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                        Log.d("KeyHash:",
                                Base64.encodeToString(md.digest(), Base64.DEFAULT));
                hash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        return hash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, IOTService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        CookieManager.getInstance().setAcceptCookie(true);

        WebView mWebView = (WebView) findViewById(R.id.webView);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(getString(R.string.default_landing_val));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        MainActivity.this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
