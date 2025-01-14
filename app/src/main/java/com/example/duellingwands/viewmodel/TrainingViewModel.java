package com.example.duellingwands.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.ai.SpellRecognition;
import com.example.duellingwands.utils.ApplicationStateHandler;
import com.example.duellingwands.utils.ImagePreprocessor;

import java.io.IOException;

import okhttp3.WebSocket;

public class TrainingViewModel extends ViewModel {

    private static float THRESHOLD = 0.7f;
    private SpellRecognition spellRecognition;
    //private LiveData<Boolean> isFighting = new MutableLiveData<>(false);
    private WebSocket webSocket = ApplicationStateHandler.getSocket();

    public TrainingViewModel() {
        // this.socket = SocketManager.initializeSocket();
    }

    public void initialize(Context context) {
        try {
            this.spellRecognition = new SpellRecognition(context);
        } catch (IOException e) {
            Log.e("TrainingViewModel", "Error initializing SpellRecognition", e);
        }
    }

    public Pair<String, Float> recognizeSpell(Bitmap bitmap, Context context){
        String[] spellNames = {"Aguamenti", "Ascendio", "Bombarda", "Confundo",
                "Glacius", "Incendio", "Protego", "Ventus"};
        float[] pourcentages = spellRecognition.recognizeSpell(ImagePreprocessor.preprocessImage(bitmap, context));

        String guessedSpell = "None";
        float maxValue = 0;
        int maxIndex = 0;

        for (int i = 0; i < pourcentages.length; i++) {
            if (pourcentages[i] > maxValue) {
                maxValue = pourcentages[i];
                maxIndex = i;
                guessedSpell = spellNames[maxIndex];
            }
        }
        // si > threshold alors traitement pour envoit dans la socket
        // socket.emit(BATTLE_RECEIVE_ACTION, data)
        return new Pair<>(guessedSpell, maxValue);
    }

    public void modelClose(){
        this.spellRecognition.close();
    }
    
//    public LiveData<Boolean> getIsFighting() {
//        return isFighting;
//    }
}
