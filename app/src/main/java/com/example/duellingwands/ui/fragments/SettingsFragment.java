package com.example.duellingwands.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.duellingwands.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        // Listeners
        ListPreference drawingMode = findPreference("drawing_mode");
        if (drawingMode != null) {
            drawingMode.setOnPreferenceChangeListener((preference, newValue) -> {
                Log.d("SettingsFragment", "Drawing mode changed to " + newValue);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("drawing_mode", newValue.toString());
                editor.apply();
                sharedPreferences.getAll().forEach((key, value) -> Log.d("SettingsFragment", key + ": " + value));
                return true;
            });
        }
    }
}
