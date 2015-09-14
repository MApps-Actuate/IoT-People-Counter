package com.opentext.otiotservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;

import com.opentext.activitytracker.SettingsActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistable;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by kclark on 9/5/2015.
 */
public class IOTService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private boolean isRunning = false;
    private MqttUtil mqtt;
    private android.support.v7.app.AppCompatActivity ctx;
    int mStartMode;
    private NotificationCompat.Builder notification_builder;
    private NotificationManagerCompat notification_manager;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        isRunning = true;
        mqtt = new MqttUtil("10.59.23.45", "1883", "/#", false);
        mqtt.connectToBroker();
        System.out.println("*************************************RUNNING*************************************");
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        super.onDestroy();
    }

    public void setContext(android.support.v7.app.AppCompatActivity ctx) {
        this.ctx = ctx;
        //String test = ctx.getSharedPreferences("pref_broker_ip", MODE_PRIVATE).getString("pref_broker_ip", null);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
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

    public class MqttUtil extends IOTService implements MqttCallback {
        MqttClient mqttClient;
        MqttPersistable mqttPersistable;

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
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token)  {
            System.out.println("Delivery complete!");
        }
    }

}
