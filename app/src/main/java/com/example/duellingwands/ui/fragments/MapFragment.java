package com.example.duellingwands.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.duellingwands.R;
import com.example.duellingwands.databinding.MapFragmentBinding;
import com.example.duellingwands.model.entities.POI;
import com.example.duellingwands.ui.activities.ArenaActivity;
import com.example.duellingwands.ui.activities.BattleActivity;
import com.example.duellingwands.viewmodel.MapViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Custom location provider that updates POIs when the user moves
 */
class CustomLocationProvider extends GpsMyLocationProvider {

    private MapViewModel viewModel;
    private static final int DISTANCE_THRESHOLD = 100;
    private static final int RADIUS = 1000;
    private Location last_location;

    public CustomLocationProvider(Context context, MapViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        // First location
        if (last_location == null) {
            Log.d("CustomLocationProvider", "First location");
            last_location = location;
            viewModel.fetchPOIs(location.getLatitude(), location.getLongitude(), RADIUS);
            return;
        }
        // If distance to last update's location is large enough, update POIs
        if (location.distanceTo(last_location) > DISTANCE_THRESHOLD) {
            Log.d("CustomLocationProvider", "Location changed : fetching POIs...");
            // The view is observing the viewmodel's POIs list, so this call will update the view
            viewModel.fetchPOIs(location.getLatitude(), location.getLongitude(), RADIUS);
        }
    }
}

/**
 * Map fragment that displays the map and the user's location with POIs
 */
public class MapFragment extends Fragment  {

    private MapFragmentBinding binding;
    private IMapController mapController;
    private static final int REQUEST_LOCATION_PERMISSIONS = 123;

    private GpsMyLocationProvider locationProvider;
    private MyLocationNewOverlay myLocationOverlay;

    private MapViewModel mapViewModel;

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
        // Get MapViewModel and observe POIs
        this.mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        this.mapViewModel.poiList.observe(getViewLifecycleOwner(), poiList -> {
            if (poiList == null) return;
            for (POI poi : poiList) {
                this.generateMarker(poi);
            }
        });
        // Set location tools
        this.locationProvider = new CustomLocationProvider(requireContext(), mapViewModel);
        // Set user's location overlay
        this.myLocationOverlay = new MyLocationNewOverlay(this.locationProvider, this.binding.mapView);
        this.myLocationOverlay.enableMyLocation();
        this.myLocationOverlay.enableFollowLocation();
        this.binding.mapView.getOverlays().add(this.myLocationOverlay);
        // Return root
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

    // ========================== MAP METHODS ============================

    /**
     * Method that generates OSMDroid markers for a given POI. Clicking on an arena
     * starts the ArenaActivity.
     * @param poi The POI to generate a marker for
     * @implNote Might be moved to a factory method class oriented architecture
     */
    private void generateMarker(POI poi) {
        Log.d("MapFragment", "Generating marker for " + poi.getName());
        Marker marker = new Marker(this.binding.mapView);
        marker.setPosition(new GeoPoint(poi.getLatitude(), poi.getLongitude()));
        marker.setTitle(poi.getName());
        marker.setSubDescription(poi.getType());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            // TODO on prod : check distance
            Log.d("MainActivity", "Clicked on " + poi.getName() + " arena.");
            Intent intent = new Intent(requireActivity(), BattleActivity.class); // TODO : ArenaActivity.class);
            intent.putExtra("arenaName", poi.getName());
            startActivity(intent);
            return true;
        });
        this.binding.mapView.getOverlays().add(marker);
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
