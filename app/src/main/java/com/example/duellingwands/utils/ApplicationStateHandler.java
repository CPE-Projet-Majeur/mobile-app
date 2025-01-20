package com.example.duellingwands.utils;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

import androidx.preference.PreferenceManager;

import com.example.duellingwands.model.entities.User;
import com.example.duellingwands.ui.acquisition.GyroscopeDrawingStrategy;
import com.example.duellingwands.ui.acquisition.IDrawingStrategy;
import com.example.duellingwands.ui.acquisition.TouchDrawingStrategy;

/**
 * This class is used to handle the application state. Used mostly for dependency injection.
 */
public class ApplicationStateHandler {

//    private static WebSocket socket;

    private static User currentUser = null;

    public static String SERVER_URL = "http://duelingwands.ddns.net";

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

    public static void disconectUser(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove("user_id").apply();
        ApplicationStateHandler.currentUser = null;
    }

    public static void setCurrentUser(User currentUser) {
        ApplicationStateHandler.currentUser = currentUser;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
