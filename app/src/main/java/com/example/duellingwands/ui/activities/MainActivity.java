package com.example.duellingwands.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.MainActivityBinding;
import com.example.duellingwands.ui.fragments.TournamentFragment;
import com.example.duellingwands.ui.fragments.TrainingFragment;
import com.example.duellingwands.ui.fragments.BattleFragment;
import com.example.duellingwands.ui.fragments.MapFragment;
import com.example.duellingwands.ui.fragments.SettingsFragment;
import com.example.duellingwands.ui.fragments.UserFragment;
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener;

import java.util.Objects;

public class MainActivity extends AbstractActivity {

    private MainActivityBinding binding;
    private String currentFragment = "Map";

    // ======================= LISTENERS =======================

    private final OnItemSelectedListener navListener = item -> {
        String title = Objects.requireNonNull(item.getTitle()).toString();
        if (title.equals(this.currentFragment)) {
            return true;
        }
        switch (title) {
            case "Map":
                loadFragment(new MapFragment(), R.id.fragmentContainerView);
                break;
            case "User":
                loadFragment(new UserFragment(), R.id.fragmentContainerView);
                break;
            case "Settings":
                loadFragment(new SettingsFragment(), R.id.fragmentContainerView);
                break;
            case "Training":
                loadFragment(new TrainingFragment(), R.id.fragmentContainerView);
                break;
            case "Tournament":
                loadFragment(new TournamentFragment(), R.id.fragmentContainerView);
                break;
            default:
                break;
        }
        this.currentFragment = title;
        return true;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        if (savedInstanceState == null) {
            loadFragment(new MapFragment(), R.id.fragmentContainerView);
        }

        this.binding.bottomNavigationView.setOnItemSelectedListener(navListener);
    }
}
