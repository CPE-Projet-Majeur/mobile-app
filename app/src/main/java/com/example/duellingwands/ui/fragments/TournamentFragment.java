package com.example.duellingwands.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.TournamentFragmentBinding;
import com.example.duellingwands.ui.activities.BattleActivity;

import com.example.duellingwands.ui.activities.CustomCaptureActivity;
import com.example.duellingwands.ui.activities.LoginActivity;
import com.example.duellingwands.ui.activities.MainActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult == null || intentResult.getContents() == null) {
            this.showDialog("Error", "Invalid QR Code.");
            return;
        }
        String content = intentResult.getContents();
        if(Integer.parseInt(content) <= 0) {
            this.showDialog("Error", "Invalid Battle ID");
            return;
        }
        Intent intent = new Intent(requireContext(), BattleActivity.class);
        intent.putExtra("BattleID", battleId);
        startActivity(intent);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupListeners() {
        binding.scanQrCodeButton.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this.requireActivity());
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setPrompt(":)");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
            intentIntegrator.initiateScan();
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

    private void showDialog(String title, String message){
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
