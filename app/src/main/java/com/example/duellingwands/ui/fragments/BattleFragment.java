package com.example.duellingwands.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.BattleFragmentBinding;
import com.example.duellingwands.ui.acquisition.IDrawingStrategy;
import com.example.duellingwands.ui.views.CanvasView;
import com.example.duellingwands.utils.ApplicationStateHandler;
import com.example.duellingwands.viewmodel.BattleViewModel;
import com.example.duellingwands.viewmodel.GuesserViewModel;

public class BattleFragment extends Fragment {

    private BattleFragmentBinding binding;
    private BattleViewModel battleViewModel;
    private GuesserViewModel guesserViewModel;
    private CanvasView canvas;
    private IDrawingStrategy drawingStrategy;
    private boolean canDraw = true;

    private final View.OnTouchListener touchListener = (view, motionEvent) -> {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !battleViewModel.getAwaitingResponse()) {
            this.drawingStrategy.startDrawing();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP && !battleViewModel.getAwaitingResponse()) {
            Pair<String, Float> result = this.guesserViewModel.recognizeSpell(canvas.getBitmap(), requireContext());
            binding.spellNameText.setText("Predicted spell: " + result.first);
            binding.spellConfidenceText.setText("Confidence: " + String.format("%.2f%%", result.second * 100));
            view.performClick();
            this.drawingStrategy.stopDrawing();
            canDraw = false;
        }
        return true;
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.battle_fragment, container, false);
        canvas = binding.canvasView;
        canvas.setOnTouchListener(touchListener);

        this.binding.buttonErase.setOnClickListener(view -> resetUI());
        binding.buttonCast.setOnClickListener(view -> {
            Integer predictedSpellId = guesserViewModel.getPredictedSpellId();
            Float confidence = guesserViewModel.getSpellConfidence();
            battleViewModel.castSpell(predictedSpellId, confidence);
            resetUI();
        });
        // Set drawing strategy
        this.drawingStrategy = ApplicationStateHandler.getDrawingStrategy(requireContext());
        this.drawingStrategy.setCanvas(canvas);

        // Set viewmodels
        battleViewModel = new ViewModelProvider(requireActivity()).get(BattleViewModel.class);
        guesserViewModel = new ViewModelProvider(requireActivity()).get(GuesserViewModel.class);

        binding.setBattleViewModel(battleViewModel);
        binding.setGuesserViewModel(guesserViewModel);
        guesserViewModel.initialize(requireContext());

        battleViewModel.playerHp.observe(getViewLifecycleOwner(), hp -> {
            if (hp != null) {
                binding.playerHpBar.setProgress(hp);
                binding.playerHpLabel.setText("Player HP: " + hp);
            }
        });

        battleViewModel.opponentHp.observe(getViewLifecycleOwner(), hp -> {
            if (hp != null) {
                binding.opponentHpBar.setProgress(hp);
                binding.opponentHpLabel.setText("Opponent HP: " + hp);
            }
        });
        guesserViewModel.getSpellPredicted().observe(getViewLifecycleOwner(), isPredicted -> {
            if (isPredicted) {
                binding.buttonCast.setEnabled(isPredicted);
                binding.buttonCast.setBackgroundTintList(requireContext().getColorStateList(android.R.color.holo_red_light));
            }
        });

        battleViewModel.battleResult.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                String message;
                switch (result) {
                    case "Victory":
                        message = "You won the battle! 🎉";
                        break;
                    case "Defeat":
                        message = "You lost the battle. 😢";
                        break;
                    default:
                        message = "The battle ended in a draw.";
                        break;
                }
                new AlertDialog.Builder(requireContext())
                        .setTitle("Battle Result")
                        .setMessage(message)
                        .setNegativeButton("Exit", (dialog, which) -> {
                            requireActivity().finish();
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        battleViewModel.simulateHpLoss();
        return binding.getRoot();
    }

    private void resetUI() {
        drawingStrategy.erase();
        binding.spellNameText.animate().alpha(0).setDuration(300).withEndAction(() -> {
            binding.spellNameText.setText("Predicted spell: None");
            binding.spellNameText.setAlpha(1);
        });

        binding.spellConfidenceText.animate().alpha(0).setDuration(300).withEndAction(() -> {
            binding.spellConfidenceText.setText("Confidence: 0%");
            binding.spellConfidenceText.setAlpha(1);
        });
        guesserViewModel.resetPredictionState();
        binding.buttonCast.setEnabled(false);
        binding.buttonCast.setBackgroundTintList(requireContext().getColorStateList(android.R.color.darker_gray));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
