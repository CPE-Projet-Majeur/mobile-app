package com.example.duellingwands.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class AbstractActivity extends AppCompatActivity {
    protected void loadFragment(Fragment fragment, int containerId) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
