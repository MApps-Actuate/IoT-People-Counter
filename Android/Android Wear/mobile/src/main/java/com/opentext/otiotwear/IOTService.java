package com.opentext.otiotwear;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistable;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;

/**
 * Created by kclark on 9/5/2015.
 */
public class IOTService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private boolean isRunning = false;
    private MqttUtil mqtt;
    private Context ctx;
    int mStartMode;
    private NotificationCompat.Builder notification_builder;
    private NotificationManagerCompat notification_manager;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
    }

    private void setMqttProperties(MqttUtil mqtt) {
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        super.onDestroy();
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;

        String hostname = PreferenceManager.getDefaultSharedPreferences(ctx).getString("pref_broker_ip", null);
        String port     = PreferenceManager.getDefaultSharedPreferences(ctx).getString("pref_broker_port", null);
        String topic    = PreferenceManager.getDefaultSharedPreferences(ctx).getString("pref_broker_topic", null);
        Boolean ssl     = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_broker_ssl", false);

        isRunning = true;

        Boolean demoMode = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_demo_mode", true);

        if(demoMode) {
            new Thread(new MqttUtil()).start(); // Fake timed notification
        }else{
            setMqttProperties(mqtt);
            mqtt = new MqttUtil(hostname,
                    port,
                    topic,
                    ssl);
            mqtt.connectToBroker();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mStartMode;
    }

    public class MyLocalBinder extends Binder {
        public IOTService getService() {
            return IOTService.this;
        }
    }

    public class MqttUtil extends IOTService implements MqttCallback, Runnable {
        MqttClient mqttClient;
        MqttPersistable mqttPersistable;

        public MqttUtil() {
            // Fake mqtt for backup
        }

        public void run() {
            while (true) {
                try {
                    sendNotification("Fake Timed Notification");
                    Thread.sleep(900000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private String  mqttHostname = null;
        private String  mqttPort     = null;
        private String  mqttTopic    = null;
        private boolean mqttSSL     = false;
        private MemoryPersistence mqttPersistence = new MemoryPersistence();

        private String mqttConnectionString = null;
        private String mqttClientID         = null;

        public MqttUtil(String mqttHostname, String mqttPort, String mqttTopic, boolean mqttSSL) {
            this.mqttHostname = mqttHostname;
            this.mqttPort     = mqttPort;
            this.mqttTopic    = mqttTopic;
            this.mqttSSL      = mqttSSL;
        }

        public void connectToBroker() {
            String mqttBrokerURL;
            // Generate the connection string
            generateConnectionString();

            // Generate the client ID
            generateClientID();

            // Create the broker connection
            try {
                mqttClient = new MqttClient(mqttConnectionString, mqttClientID, mqttPersistence);
                mqttClient.connect();
                mqttClient.setCallback(this);
                mqttClient.subscribe(mqttTopic);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void sendNotification(String title) {
            NotificationManager notificationManager =
                    (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);

            Intent viewIntent = new Intent(ctx, ThreasholdActivity.class);
            PendingIntent viewPendingIntent =
                    PendingIntent.getActivity(ctx, 0, viewIntent, 0);

            notification_builder =
                    new NotificationCompat.Builder(ctx)
                            .setSmallIcon(R.drawable.messenger_bubble_small_blue)
                            .setContentTitle(title)
                            .setContentIntent(viewPendingIntent);

            notification_manager =
                    NotificationManagerCompat.from(ctx);

            notification_manager.notify(1, notification_builder.build());
        }

        private void generateClientID() {
            mqttClientID = "OTIOTServiceMqttClient" + new Random().nextInt((1000000 - 1) + 1) + 1;
        }

        private void generateConnectionString() {
            if(mqttSSL == true)
                this.mqttConnectionString = "tcp://" + getMqttHostname() + ":" + getMqttPort();  // TODO: Implement SSL
            else
                this.mqttConnectionString = "tcp://" + mqttHostname + ":" + mqttPort;
        }

        public String getConnectionString() {
            return mqttConnectionString;
        }

        public void setMqttHostname(String mqttHostname) {
            this.mqttHostname = mqttHostname;
        }

        public void setMqttPort(String mqttPort) {
            this.mqttPort = mqttPort;
        }

        public void setMqttTopic(String mqttTopic) {
            this.mqttTopic = mqttTopic;
        }

        public void setMqttSSL(boolean mqttSSL) {
            this.mqttSSL = mqttSSL;
        }

        public String getMqttHostname() {
            return mqttHostname;
        }

        public String getMqttPort() {
            return mqttPort;
        }

        public String getMqttTopic() {
            return mqttTopic;
        }

        public boolean getMqttSSL() {
            return mqttSSL;
        }

        @Override
        public void connectionLost(Throwable error) {
            error.printStackTrace();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            System.out.println("******************MESSAGE ARRIVED*******************");
            System.out.println(message);
            sendNotification("Message Arrived!");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token)  {
            System.out.println("Delivery complete!");
        }
    }

}
