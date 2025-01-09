package com.example.duellingwands.ui.fragments;

import static android.content.Context.SENSOR_SERVICE;

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

// POC ! Should be make into a more refined class structure
public class Frag extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linear_acceleration;
    //private Sensor accelerometer;
    //private Sensor magnetometer;
    private Sensor rotVector;
    private CanvasBinding binding;
    private CanvasView canvasView;
    private boolean drawing = true;

    // Sensor values (to be moved ?)
    private final int LINEAR_SENSOR = Sensor.TYPE_LINEAR_ACCELERATION; // No gravity
    private final int DELAY = 6000; // micro secondes
    private float prev_vx = 0;
    private float prev_vy = 0;
    private float prev_x = 0;
    private float prev_y = 0;
    private float[] linAccValues = new float[3]; // récupérées depuis l'accéléro brut
    private float[] geomagneticValues = new float[3]; // récupérées depuis le magnétomètre
    private float[] rotationMatrix = new float[9];
    private float prevTimeStamp = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = DataBindingUtil.inflate(inflater, R.layout.canvas, container, false);
        this.canvasView = binding.canvasView;
        this.canvasView.setOnClickListener(view -> {
            if (this.drawing) {
                sensorManager.unregisterListener(this);
            }
            else {
                if (linear_acceleration != null) {
                    sensorManager.registerListener(this, linear_acceleration, this.DELAY);    //SensorManager.SENSOR_DELAY_UI);
                }
                if (rotVector != null) {
                    sensorManager.registerListener(this, rotVector, this.DELAY);
                }
            }
            this.drawing = !this.drawing;
        });
        this.canvasView.setOnLongClickListener(view -> {
            canvasView.clearPoints();
            canvasView.invalidate();
            return true;
        });

        Log.d("Canevas", "Width: " + binding.canvasView.getWidth() + ", Height: " + binding.canvasView.getHeight());
        this.sensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);
        this.rotVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        this.linear_acceleration = sensorManager.getDefaultSensor(this.LINEAR_SENSOR);

        return binding.getRoot();
    }

    private float lowPassFilter(float current, float previous, float alpha) {
        return alpha * current + (1 - alpha) * previous;
    }

    private float[] transformToWorldCoord(float[] accPhone, float[] rotationMatrix) {
        // rotationMatrix est un tableau de 9 floats
        // indexé : [0,1,2, 3,4,5, 6,7,8]
        float rx = rotationMatrix[0] * accPhone[0]
                + rotationMatrix[1] * accPhone[1]
                + rotationMatrix[2] * accPhone[2];
        float ry = rotationMatrix[3] * accPhone[0]
                + rotationMatrix[4] * accPhone[1]
                + rotationMatrix[5] * accPhone[2];
        float rz = rotationMatrix[6] * accPhone[0]
                + rotationMatrix[7] * accPhone[1]
                + rotationMatrix[8] * accPhone[2];

        return new float[]{rx, ry, rz};
    }

    private void updateMotion(SensorEvent event){
        float dt = (event.timestamp - prevTimeStamp) / 1_000_000_000f;
        prevTimeStamp = event.timestamp;

        float[] worldAccel = transformToWorldCoord(linAccValues, rotationMatrix);
        float axw = worldAccel[1]; //0
        float ayw = worldAccel[2]; //1
        //float azw = worldAccel[2];

        // Acceleration
        final float ACCELERATION_THRESHOLD = 0.1f;
        if (Math.abs(axw) < ACCELERATION_THRESHOLD) axw = 0;
        if (Math.abs(ayw) < ACCELERATION_THRESHOLD) ayw = 0;
        Log.d("Accelerometer", "Ax: " + axw + ", Ay: " + ayw);

        // Velocity
        final float VELOCITY_THRESHOLD = 0.01f; // Seuil pour la vitesse
        final float DAMPING_FACTOR = 0.98f;
        float vx = Math.abs(axw) < ACCELERATION_THRESHOLD ? 0 : this.prev_vx + axw * dt;
        float vy = Math.abs(ayw) < ACCELERATION_THRESHOLD ? 0 : this.prev_vy + ayw * dt;
        vx *= DAMPING_FACTOR;
        vy *= DAMPING_FACTOR;
        if (Math.abs(vx) < VELOCITY_THRESHOLD) vx = 0;
        if (Math.abs(vy) < VELOCITY_THRESHOLD) vy = 0;
        Log.d("Velocity", "Vx: " + vx + ", Vy: " + vy);

        // Position
        float dx = this.prev_x + vx * dt;
        float dy = this.prev_y + vy * dt;
        Log.d("Position", "Dx: " + dx + ", Dy: " + dy);

        // Add point to canvas
        float px = dx * 1000 + canvasView.getWidth() / 2f;
        float py = dy * 1000 + canvasView.getHeight() / 2f;
        px = Math.max(0, Math.min(px, canvasView.getWidth()));
        py = Math.max(0, Math.min(py, canvasView.getHeight()));
        canvasView.addPoint((int) px, (int) py);
        Log.d("Canvas", "Px: " + (int) px + ", Py: " + (int) py);

        // Draw and memorize
        canvasView.invalidate();
        this.prev_vx = vx;
        this.prev_vy = vy;
        this.prev_x = dx;
        this.prev_y = dy;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                Log.d("SensorChanges", "LinAcc");
                System.arraycopy(event.values, 0, this.linAccValues, 0, 3);
                this.updateMotion(event);
                break;
//            case Sensor.TYPE_MAGNETIC_FIELD:
//                System.arraycopy(event.values, 0, this.geomagneticValues, 0, 3);
//                Log.d("SensorChanges", "Magnetic");
//                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                Log.d("SensorChanges", "Rot");
                break;
        }
        //this.updateMotion(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Pas nécessaire pour cet exemple
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (linear_acceleration != null) {
//            sensorManager.registerListener(this, linear_acceleration, this.DELAY);    //SensorManager.SENSOR_DELAY_UI);
//        }
//        if (rotVector != null) {
//            sensorManager.registerListener(this, rotVector, this.DELAY);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.drawing = false;
        sensorManager.unregisterListener(this);
    }
}
