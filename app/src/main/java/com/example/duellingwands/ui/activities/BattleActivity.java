package com.example.duellingwands.ui.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.BattleActivityBinding;
import com.example.duellingwands.ui.fragments.BattleFragment;
import com.example.duellingwands.ui.fragments.LoadingFragment;
import com.example.duellingwands.viewmodel.BattleViewModel;

public class BattleActivity extends AbstractActivity {

    private static final String TAG = "BattleActivity";
    private BattleActivityBinding binding;
    private BattleViewModel viewModel;
    private String battleId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.battle_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, new LoadingFragment())
                .commit();

        battleId = getIntent().getStringExtra("BattleID");
        if (battleId != null) {
            Log.d(TAG, "Received BattleID: " + battleId);
            this.viewModel = new ViewModelProvider(this).get(BattleViewModel.class);
            viewModel.setBattleId(Integer.parseInt(battleId));
        } else {
            Log.e(TAG, "BattleID not found in Intent extras");
        }
        observeBattleStart();

    }
    private void observeBattleStart() {
        viewModel.getBattleStartEvent().observe(this, hasStarted -> {
            if (hasStarted) {
                Log.d(TAG, "Battle has started, loading BattleFragment...");
                loadBattleFragment();
            }
        });
    }

    private void loadBattleFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new BattleFragment())
                .commit();
    }

}