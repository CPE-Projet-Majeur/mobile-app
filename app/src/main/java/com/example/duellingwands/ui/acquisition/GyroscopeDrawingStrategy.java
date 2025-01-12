package com.example.duellingwands.ui.acquisition;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.duellingwands.ui.views.CanvasView;

public class GyroscopeDrawingStrategy implements IDrawingStrategy {

    private SensorManager sensorManager;
    private Sensor gyro;
    private final int DELAY = 60000;

    private CanvasView canvasView;

    private float[] rotationMatrix = new float[9];
    private float[] orientation    = new float[3];

    //private static final float SMOOTHING_ALPHA = 0.5f;
    private boolean firstReading = true;

    //private float previousX = -1;
    // private float previousY = -1;
    private float previousAzimuth = 0;
    private float previousPitch = 0;
    private float offsetX = 0;
    private float offsetY = 0;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                // Récupérer la matrice de rotation 3x3
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);

                // Extraire l’orientation en radians
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuth = orientation[0]; // ∈ [-π..+π], environ
                float pitch = orientation[1]; // ∈ [-π/2..+π/2], environ

                float deltaAzimuth = azimuth - previousAzimuth;
                float deltaPitch = pitch - previousPitch;

                if (deltaAzimuth > Math.PI) {
                    azimuth -= 2 * Math.PI;
                } else if (deltaAzimuth < -Math.PI) {
                    azimuth += 2 * Math.PI;
                }
                if (deltaPitch > Math.PI) {
                    pitch -= 2 * Math.PI;
                } else if (deltaPitch < -Math.PI) {
                    pitch += 2 * Math.PI;
                }

                float canvasW = canvasView.getWidth();
                float canvasH = canvasView.getHeight();

                // Normaliser
                float ratioX = (azimuth - offsetX + (float)Math.PI) / ((float)Math.PI * 2f);
                float ratioY = (pitch + (float)Math.PI/2f) / (float)Math.PI;
                float X = ratioX * canvasW;
                float Y = ratioY * canvasH;  // (1 - ratioY) * canvasH; pour inverser axe vertical

                // Lissage
                if (!firstReading) {
//                    X = SMOOTHING_ALPHA * previousX + (1 - SMOOTHING_ALPHA) * X;
//                    Y = SMOOTHING_ALPHA * previousY + (1 - SMOOTHING_ALPHA) * Y;
                    canvasView.addPoint((int) X, (int) Y);
                    canvasView.invalidate();
                } else {
                    offsetX = azimuth;
                    offsetY = pitch;
                    firstReading = false;
                }
                previousAzimuth = azimuth;
                previousPitch = pitch;
                //previousX = X;
                //previousY = Y;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //
        }
    };

    public GyroscopeDrawingStrategy(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

    @Override
    public void startDrawing() {
        if(canvasView != null) {
            this.firstReading = true;
            sensorManager.registerListener(sensorEventListener, gyro, DELAY);
        }
    }

    @Override
    public void stopDrawing() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void setCanvas(CanvasView canvas) {
        this.canvasView = canvas;
    }

    @Override
    public void erase() {
        if(canvasView != null) {
            this.firstReading = true;
            canvasView.clearPoints();
            canvasView.invalidate();
        }
    }
}
