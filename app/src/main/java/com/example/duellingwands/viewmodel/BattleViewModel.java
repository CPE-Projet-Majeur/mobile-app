package com.example.duellingwands.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.ai.SpellRecognition;
import com.example.duellingwands.utils.ImagePreprocessor;

import java.io.IOException;

import okhttp3.WebSocket;

public class BattleViewModel extends ViewModel {

    private SpellRecognition spellRecognition;
    private WebSocket socket;
    private LiveData<Boolean> isFighting = new MutableLiveData<>(false);

    public BattleViewModel() {
        // this.socket = SocketManager.initializeSocket();
    }

    public void initialize(Context context) {
        try {
            this.spellRecognition = new SpellRecognition(context);
        } catch (IOException e) {
            Log.e("BattleViewModel", "Error initializing SpellRecognition", e);
        }
    }

    public int recognizeSpell(Bitmap bitmap, Context context) {
        return this.spellRecognition.recognizeSpell(ImagePreprocessor.preprocessImage(bitmap, context));
    }

    public void modelClose(){
        this.spellRecognition.close();
    }
    
    public LiveData<Boolean> getIsFighting() {
        return isFighting;
    }
}
