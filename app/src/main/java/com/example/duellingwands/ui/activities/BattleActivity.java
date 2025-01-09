package com.example.duellingwands.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.BattleActivityBinding;
import com.example.duellingwands.ui.fragments.Frag2;

public class BattleActivity extends AppCompatActivity {

    private static final String TAG = "BattleActivity";
    private BattleActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = DataBindingUtil.setContentView(this, R.layout.battle_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, new Frag2())
                .commit();
    }
}