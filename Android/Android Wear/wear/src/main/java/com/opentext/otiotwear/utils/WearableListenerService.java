package com.opentext.otiotwear.utils;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.opentext.otiotwear.Wear;

/**
 * Created by kclark on 9/15/2015.
 */
public class WearableListenerService extends com.google.android.gms.wearable.WearableListenerService {
    private static final String START_ACTIVITY = "/start_activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + messageEvent);
        if(messageEvent.getPath().equalsIgnoreCase(START_ACTIVITY)) {
            Intent intent = new Intent(this, Wear.class);
            startActivity(intent);
        }else{
            super.onMessageReceived(messageEvent);
        }
    }
}
