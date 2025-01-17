package com.example.duellingwands.viewmodel;

import static android.os.AsyncTask.execute;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.entities.User;
import com.example.duellingwands.model.repository.UserRepository;
import com.example.duellingwands.utils.ApplicationStateHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadingViewModel extends ViewModel {

    public static final String LOADED = "LOADED";
    public static final String NOT_LOADED = "NOT_LOADED";
    public static final String LOADING = "LOADING";

    private final MutableLiveData<String> _isLoaded = new MutableLiveData<>(LOADING);
    public final LiveData<String> isLoaded = _isLoaded;

    private UserRepository userRepository = UserRepository.getInstance();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void loadUser(int userId){
        executor.execute(() -> {
            User user = userRepository.getById(userId);
            if(user != null){
                _isLoaded.postValue(LOADED);
                ApplicationStateHandler.setCurrentUser(user);
            } else {
                _isLoaded.postValue(NOT_LOADED);
            }
        });
    }
}
