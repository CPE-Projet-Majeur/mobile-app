package com.example.duellingwands.viewmodel;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.duellingwands.model.entities.POI;
import com.example.duellingwands.model.repository.POIRepository;

import java.util.List;

public class MapViewModel extends ViewModel {

    private final POIRepository mapRepository = POIRepository.getInstance();

    // Expose POIs to the View
    private final MutableLiveData<List<POI>> _poiList = new MutableLiveData<>();
    public LiveData<List<POI>> poiList = _poiList;

    // Expose error to the View
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void fetchPOIs(double latitude, double longitude, int radius) {
        Log.d("MapViewModel", "Fetching POIs");
        mapRepository.getPOIs(latitude, longitude, radius, new POIRepository.POICallback() {
            @Override
            public void onSuccess(List<POI> pois) {
                Log.d("MapViewModel", "POIs fetched");
                _poiList.postValue(pois);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MapViewModel", "Error fetching POIs", e);
                _error.postValue(e.getMessage());
            }
        });
    }
}
