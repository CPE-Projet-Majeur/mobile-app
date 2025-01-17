package com.example.duellingwands.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.LoginActivityBinding;
import com.example.duellingwands.utils.ApplicationStateHandler;
import com.example.duellingwands.viewmodel.LoadingViewModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private LoginActivityBinding binding;
    private LoadingViewModel viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        this.viewModel = new ViewModelProvider(this).get(LoadingViewModel.class);
        // Load main activity when user is loaded
        this.viewModel.isLoaded.observe(this, isLoaded -> {
            Log.d(TAG, "isLoaded = " + isLoaded);
            if (LoadingViewModel.LOADED.equals(isLoaded)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else if (LoadingViewModel.NOT_LOADED.equals(isLoaded)) {
                this.showDialog("Error", "The user could not be loaded");
                ApplicationStateHandler.disconectUser(getApplicationContext());
                this.enableQRScanning();
            }
        });
        // Check if user is already logged in
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userId = this.sharedPreferences.getInt("user_id", -1);
        if(userId != -1) this.viewModel.loadUser(userId);
        else this.enableQRScanning();
    }

    // This method is called when a child activity, started with the startActivityForResult()
    // method, returns a result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult == null || intentResult.getContents() == null) {
            Log.d(TAG, "onActivityResult: NULL");
            this.showDialog("Error", "Invalid QR Code.");
            return;
        }
        String content = intentResult.getContents();
        Log.d(TAG, "onActivityResult: " + content);
        if(Integer.parseInt(content) <= 0) {
            Log.d(TAG, "onActivityResult: INVALID ID");
            this.showDialog("Error", "Invalid User ID");
            return;
        }
        this.sharedPreferences.edit().putInt("user_id", Integer.parseInt(content)).apply();
        this.viewModel.loadUser(Integer.parseInt(content));
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialog(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void enableQRScanning() {
        this.binding.connectionContainer.setVisibility(View.VISIBLE);
        this.binding.QRButton.setOnClickListener(view -> {
            // IntentIntegrator delegates the QR SCanning to a ZXING activity
            IntentIntegrator intentIntegrator = new IntentIntegrator(LoginActivity.this);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setPrompt(":)");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.initiateScan();
        });
    }
}
