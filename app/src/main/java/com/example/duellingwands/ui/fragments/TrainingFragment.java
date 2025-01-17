package com.example.duellingwands.ui.fragments;

import android.os.Bundle;
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
import com.example.duellingwands.databinding.TrainingFragmentBinding;
import com.example.duellingwands.ui.acquisition.IDrawingStrategy;
import com.example.duellingwands.ui.views.CanvasView;
import com.example.duellingwands.utils.ApplicationStateHandler;
import com.example.duellingwands.viewmodel.GuesserViewModel;

public class TrainingFragment extends Fragment {

    private TrainingFragmentBinding binding;
    // private SensorManager sensorManager;
    private CanvasView canvas;
    private final View.OnTouchListener touchListener = ((view, motionEvent) -> {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            this.drawingStrategy.startDrawing();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Pair<String, Float> result = this.viewModel.recognizeSpell(canvas.getBitmap(), requireContext());
            binding.spellNameText.setText("Predicted spell: " + result.first);
            binding.spellConfidenceText.setText("Confidence: " + String.format("%.2f%%", result.second * 100));
            view.performClick();
            this.drawingStrategy.stopDrawing();
        }
        return true;
    });

    private GuesserViewModel viewModel;

    private IDrawingStrategy drawingStrategy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        this.binding = DataBindingUtil.inflate(inflater, R.layout.training_fragment, container, false);
        this.canvas = binding.canvasView;
        // Anchor listeners
        this.canvas.setOnTouchListener(this.touchListener);
        this.binding.buttonErase.setOnClickListener(view -> resetUI());
        // Set drawing strategy
        this.drawingStrategy = ApplicationStateHandler.getDrawingStrategy(requireContext());
        this.drawingStrategy.setCanvas(canvas);
        // Set viewmodel
        this.viewModel = new ViewModelProvider(this).get(GuesserViewModel.class);
        this.viewModel.initialize(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.drawingStrategy.stopDrawing();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.viewModel.modelClose();
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
    }
}
