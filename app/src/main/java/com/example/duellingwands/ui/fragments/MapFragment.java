package com.example.duellingwands.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.MapFragmentBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment  { // implements LocationListener {

    private MapFragmentBinding binding;
    private IMapController mapController;
    private static final int REQUEST_LOCATION_PERMISSIONS = 123;

    private LocationManager locationManager;
    private GpsMyLocationProvider locationProvider;
    private MyLocationNewOverlay myLocationOverlay;

    // ======================= LIFECYCLE METHODS =======================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = DataBindingUtil.inflate(inflater, R.layout.map_fragment, container, false);
        this.requestLocationPermissions();
        Configuration.getInstance().setUserAgentValue("DuellingWands/1.0");
        // Set map
        this.binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        this.binding.mapView.setBuiltInZoomControls(true);
        this.binding.mapView.setMultiTouchControls(true);
        this.mapController = this.binding.mapView.getController();
        this.mapController.setZoom(19);
        // Set location tools
        this.locationProvider = new GpsMyLocationProvider(requireContext());
        // Set user's location overlay
        this.myLocationOverlay = new MyLocationNewOverlay(this.locationProvider, this.binding.mapView);
        this.myLocationOverlay.enableMyLocation();
        this.myLocationOverlay.enableFollowLocation();
        this.binding.mapView.getOverlays().add(this.myLocationOverlay);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.binding.mapView.onPause();
    }

    // ======================= PERMISSIONS METHODS =======================

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Permission necessary")
                        .setMessage("GIVE IT NOW.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Ask permission
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            }, REQUEST_LOCATION_PERMISSIONS);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            Log.e("MapFragment", "Permission refused");
                        })
                        .show();
            } else {
                // Ask permission for the first time
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_LOCATION_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MapFragment", "Location permission granted");
            } else {
                Log.e("MapFragment", "Location permission denied");
            }
        }
    }
}
