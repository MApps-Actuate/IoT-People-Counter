package com.opentext.utils;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.opentext.otiotservice.R;

/**
 * Created by kclark on 9/5/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
