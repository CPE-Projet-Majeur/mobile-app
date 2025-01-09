package com.example.duellingwands.ui.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.MainActivityBinding;
import com.example.duellingwands.ui.fragments.MapFragment;
import com.example.duellingwands.ui.fragments.UserFragment;
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener;

import org.osmdroid.config.Configuration;

import java.util.Objects;

public class MainActivity extends AbstractActivity {

    private MainActivityBinding binding;

    private final OnItemSelectedListener navListener = item -> {
        String title = Objects.requireNonNull(item.getTitle()).toString();
        switch (title) {
            case "Map":
                loadFragment(new MapFragment(), R.id.fragmentContainerView);
                break;
            case "User":
                loadFragment(new UserFragment(), R.id.fragmentContainerView);
                break;
            case "Settings":
                break;
            default:
                break;
        }
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
