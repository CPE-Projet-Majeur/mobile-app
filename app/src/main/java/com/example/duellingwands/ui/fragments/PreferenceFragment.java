package com.example.duellingwands.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.bumptech.glide.Glide;
import com.example.duellingwands.R;

// Preference Fragment, dans SettingsFragment
public class PreferenceFragment extends PreferenceFragmentCompat {

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backgroundGif = new ImageView(requireContext());
        backgroundGif.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Charger le GIF avec Glide
        Glide.with(requireContext())
                .asGif()
                .load(R.raw.settings)
                .into(backgroundGif);

        // Ajouter l'ImageView au parent de la vue des préférences
        //((ViewGroup) view.getParent()).addView(backgroundGif, 0);
        ((ViewGroup) view).addView(backgroundGif, 0);
    }
}
