package com.opentext.activitytracker;

import android.app.Activity;
import android.os.Bundle;

import com.opentext.utils.SettingsFragment;

/**
 * Created by kclark on 9/5/2015.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
