package com.opentext.utils;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by kclark on 9/9/2015.
 */
public class ListenerService extends WearableListenerService {

    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("GOT IT");
        /*
         * Receive the message from wear
         */
        if (messageEvent.getPath().equals(HELLO_WORLD_WEAR_PATH)) {
            System.out.println("GOT IT");
        }

    }
}
