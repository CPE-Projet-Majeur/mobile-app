package com.example.duellingwands.ui.fragments;

import static android.content.Context.SENSOR_SERVICE;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.CanvasBinding;
import com.example.duellingwands.ui.views.CanvasView;
import com.example.duellingwands.ui.views.Point;

public class Frag2 extends Fragment {

    private CanvasBinding binding;
    private Sensor gyro;
    private SensorManager sensorManager;
    private CanvasView canvas;
    private final int DELAY = 60000;

    private float[] rotationMatrix = new float[9];
    private float[] orientation    = new float[3];

    private static final float SMOOTHING_ALPHA = 0.5f;
    private static final int FIRST_READING = -1;

    private float previousX = -1;
    private float previousY = -1;

    private final SensorEventListener sensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR && drawing) {
                // Récupérer la matrice de rotation 3x3
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);

                // Extraire l’orientation en radians
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuth = orientation[0]; // ∈ [-π..+π], environ
                float pitch = orientation[1]; // ∈ [-π/2..+π/2], environ
                float roll  = orientation[2]; // ∈ [-π..+π], environ

                float canvasW = canvas.getWidth();
                float canvasH = canvas.getHeight();

                // Normaliser
                // float ratioX = (roll + (float)Math.PI) / ((float)Math.PI * 2f);
                float ratioX = (azimuth + (float)Math.PI) / ((float)Math.PI * 2f);
                float ratioY = (pitch + (float)Math.PI/2f) / (float)Math.PI;
                float rawX = ratioX * canvasW;
                //float rawY = (1 - ratioY) * canvasH; // Inverser axe vertical (peut etre une option)
                float rawY = ratioY * canvasH;

                // On ne dessine pas le premier point
                if (previousX == FIRST_READING) {
                    previousX = rawX;
                    previousY = rawY;
                } else {
                    // Lissage
                    float smoothedX = SMOOTHING_ALPHA * previousX + (1 - SMOOTHING_ALPHA) * rawX;
                    float smoothedY = SMOOTHING_ALPHA * previousY + (1 - SMOOTHING_ALPHA) * rawY;
                    canvas.addPoint((int) smoothedX, (int) smoothedY);
                    canvas.invalidate();

                    previousX = smoothedX;
                    previousY = smoothedY;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private boolean drawing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        this.binding = DataBindingUtil.inflate(inflater, R.layout.canvas, container, false);
        this.canvas = binding.canvasView;

        this.canvas.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //sensorManager.registerListener(sensor, gyro, DELAY);
                drawing = true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //sensorManager.unregisterListener(sensor);
                //this.previousX = this.previousY = FIRST_READING;
                drawing = false;
                view.performClick();
            }
            return true;
        });

        this.binding.buttonErase.setOnClickListener(view -> {
            this.previousX = this.previousY = FIRST_READING;
            canvas.clearPoints();
            canvas.invalidate();
        });

        this.sensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);
        this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensor, gyro, DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensor);
    }

    //    private void saveCanvasAsImage(CanvasView canvasView) {
//        Bitmap bitmap = canvasView.getBitmap(); // Récupérer le bitmap
//
//        File directory = new File(requireActivity().getExternalFilesDir(null), "images");
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//
//        File file = new File(directory, "canvas_image.png");
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Enregistrer en PNG
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
