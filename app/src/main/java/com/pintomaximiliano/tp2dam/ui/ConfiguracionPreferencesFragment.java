package com.pintomaximiliano.tp2dam.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.pintomaximiliano.tp2dam.R;
import com.pintomaximiliano.tp2dam.dao.BuilderAPI;

public class ConfiguracionPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_configuracion);

    }

}
