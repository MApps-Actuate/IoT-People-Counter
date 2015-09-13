package com.opentext.otiotservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistable;

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

    public class MqttUtil extends IOTService implements MqttCallback {
        MqttClient mqttClient;
        MqttPersistable mqttPersistable;

        private String  mqttHostname = null;
        private String  mqttPort     = null;
        private String  mqttTopic    = null;
        private boolean mqttSSL     = false;

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
                mqttClient = new MqttClient(mqttConnectionString, mqttClientID);
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
                this.mqttConnectionString = "tcp://" + mqttHostname + ":" + mqttPort;  // TODO: Implement SSL
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
            System.out.println(message.getPayload());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token)  {
            System.out.println("Delivery complete!");
        }
    }

}
