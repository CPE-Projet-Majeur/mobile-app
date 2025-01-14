package com.example.duellingwands.utils;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

import androidx.preference.PreferenceManager;

import com.example.duellingwands.ui.acquisition.GyroscopeDrawingStrategy;
import com.example.duellingwands.ui.acquisition.IDrawingStrategy;
import com.example.duellingwands.ui.acquisition.TouchDrawingStrategy;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * This class is used to handle the application state. Used mostly for dependency injection.
 */
public class ApplicationStateHandler {

    private static WebSocket socket;

    public static String SERVER_URL = "ws://example.com/socket";

    /**
     * This method is used to get the drawing strategy from the user's preferences.
     * @param context
     * @return The drawing strategy to use.
     * @implNote Dependency injection for the drawing strategy pattern.
     */
    public static IDrawingStrategy getDrawingStrategy(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String drawingMode = sharedPreferences.getString("drawing_mode", "gyroscope");
        switch (drawingMode) {
            case "touch":
                return new TouchDrawingStrategy();
            case "gyroscope":
            default:
                SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
                return new GyroscopeDrawingStrategy(sensorManager);
        }
    }

    public static WebSocket getSocket() {
        return ApplicationStateHandler.socket;
    }

    public static void setSocket(WebSocket webSocket) {
        ApplicationStateHandler.socket = webSocket;
    }
}
