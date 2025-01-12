package com.example.duellingwands.ui.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.BattleActivityBinding;
import com.example.duellingwands.ui.fragments.BattleFragment;
import com.example.duellingwands.viewmodel.BattleViewModel;

public class BattleActivity extends AppCompatActivity {

    private static final String TAG = "BattleActivity";
    private BattleActivityBinding binding;
    private BattleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = DataBindingUtil.setContentView(this, R.layout.battle_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, new BattleFragment())
                .commit();
        this.viewModel = new ViewModelProvider(this).get(BattleViewModel.class);
        this.viewModel.getIsFighting().observe(this, isFighting -> {
            Log.d(TAG, "isFighting: " + isFighting);
        });
    }
}