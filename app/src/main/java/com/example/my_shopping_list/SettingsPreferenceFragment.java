package com.example.my_shopping_list;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.Collections;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference_fragment, rootKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SwitchPreference switchPreference = (SwitchPreference)getPreferenceManager().findPreference("sort_selection");

        switchPreference.setOnPreferenceChangeListener(new SwitchPreferenceCompat.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MainActivity.sorted = (boolean) newValue;
                if (MainActivity.sorted)
                    Collections.sort(MainActivity.shoppingList);
                return true;
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
