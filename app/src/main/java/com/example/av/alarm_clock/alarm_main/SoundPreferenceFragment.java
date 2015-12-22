package com.example.av.alarm_clock.alarm_main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;

import com.example.av.alarm_clock.R;

/**
 * Created by Михаил on 21.12.2015.
 */
public class SoundPreferenceFragment extends PreferenceFragment {
    private RingtonePreference ringtonePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.alarm_preference);
        ringtonePreference = (RingtonePreference) getPreferenceScreen().findPreference("pref_alarm_sound");
    }

    public RingtonePreference getRingtonePreference() {
        return ringtonePreference;
    }
}
