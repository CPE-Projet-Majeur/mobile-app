package com.example.duellingwands.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.TournamentFragmentBinding;
import com.example.duellingwands.ui.activities.BattleActivity;

public class TournamentFragment extends Fragment {

    private TournamentFragmentBinding binding;
    private String battleId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.tournament_fragment, container, false);
        setupListeners();
        return binding.getRoot();
    }

    private void setupListeners() {
        binding.scanQrCodeButton.setOnClickListener(v -> {
            String scannedCode = "1234";
            updateBattleId(scannedCode);
            binding.scannedCodeText.setText("Scanned Code: " + scannedCode);
            binding.scannedCodeText.setVisibility(View.VISIBLE);
        });

        binding.manualCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateBattleId(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.joinTournamentButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BattleActivity.class);
            intent.putExtra("BattleID", battleId);
            startActivity(intent);
        });
    }

    private void updateBattleId(String newBattleId) {
        this.battleId = newBattleId;
        boolean isValid = isBattleIdValid();

        binding.joinTournamentButton.setEnabled(isValid);
        binding.joinTournamentButton.setBackgroundTintList(requireContext().getColorStateList(
                isValid ? android.R.color.holo_blue_light : android.R.color.darker_gray
        ));
    }

    private boolean isBattleIdValid() {
        if (battleId == null || battleId.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(battleId.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
