package com.example.duellingwands.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.ArenaActivityBinding;
import com.example.duellingwands.ui.fragments.ArenaFragment;

public class ArenaActivity extends AbstractActivity {

    private static final String TAG = "ArenaActivity";
    private ArenaActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.arena_activity);
        loadFragment(new ArenaFragment(), R.id.fragmentContainerView);
    }
}
