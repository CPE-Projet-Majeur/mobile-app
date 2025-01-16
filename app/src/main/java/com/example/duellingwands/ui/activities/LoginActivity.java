package com.example.duellingwands.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.LoginActivityBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private LoginActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.login_activity);

        this.binding.QRButton.setOnClickListener(view -> {
            // IntentIntegrator delegates the QR SCanning to a ZXING activity
            IntentIntegrator intentIntegrator = new IntentIntegrator(LoginActivity.this);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setPrompt("DEHORS");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.initiateScan();
        });
    }

    // This method is called when a child activity, started with the startActivityForResult()
    // method, returns a result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null){
            String content = intentResult.getContents();
            if(content != null){
                Log.d(TAG, "onActivityResult: " + content);
            }
        } else {
            Log.d(TAG, "onActivityResult: NULL");
        }

        // TODO : /les résultats du QR code vont dans shared preferences.

        super.onActivityResult(requestCode, resultCode, data);
    }
}
