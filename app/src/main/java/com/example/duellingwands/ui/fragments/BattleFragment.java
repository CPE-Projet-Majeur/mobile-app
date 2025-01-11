package com.example.duellingwands.ui.fragments;

import static android.content.Context.SENSOR_SERVICE;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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
import com.example.duellingwands.databinding.CanvasBinding;
import com.example.duellingwands.ui.acquisition.GyroscopeDrawingStrategy;
import com.example.duellingwands.ui.acquisition.IDrawingStrategy;
import com.example.duellingwands.ui.views.CanvasView;
import com.example.duellingwands.viewmodel.BattleViewModel;

public class BattleFragment extends Fragment {

    private CanvasBinding binding;
    // private SensorManager sensorManager;
    private CanvasView canvas;
    private final View.OnTouchListener touchListener = ((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                this.drawingStrategy.startDrawing();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Log.d("BattleFragment", "Stop drawing");
                this.drawingStrategy.stopDrawing();
                view.performClick();
            }
            return true;
        });

    private BattleViewModel viewModel;

    private IDrawingStrategy drawingStrategy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        this.binding = DataBindingUtil.inflate(inflater, R.layout.canvas, container, false);
        this.canvas = binding.canvasView;
        // Anchor listeners
        this.canvas.setOnTouchListener(this.touchListener);
        this.binding.buttonErase.setOnClickListener(view -> drawingStrategy.erase());
        this.binding.buttonCheckSpell.setOnClickListener(view -> this.viewModel.recognizeSpell(canvas.getBitmap(), requireContext()));
        // Set drawing strategy
        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);
        this.drawingStrategy = new GyroscopeDrawingStrategy(sensorManager);
        //this.drawingStrategy = new TouchDrawingStrategy();
        this.drawingStrategy.setCanvas(canvas);
        // Set viewmodel
        this.viewModel = new ViewModelProvider(this).get(BattleViewModel.class);
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
}
