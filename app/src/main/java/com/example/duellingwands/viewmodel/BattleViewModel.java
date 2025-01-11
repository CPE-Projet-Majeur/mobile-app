package com.example.duellingwands.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.ai.SpellRecognition;
import com.example.duellingwands.utils.ImagePreprocessor;

import java.io.IOException;

public class BattleViewModel extends ViewModel {

    private SpellRecognition spellRecognition;

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
}
