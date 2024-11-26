package com.example.aethoneventsapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

public class mapActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get your MapTiler API Key
        String key = BuildConfig.MAPTILER_API_KEY;
        String mapId = "streets-v2";
        String styleUrl = "https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + key;

        // Initialize Mapbox
        Mapbox.getInstance(this);

        // Set your layout
        setContentView(R.layout.activity_event_map);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);

        // Define Edmonton locations
        List<LatLng> coordinatesList = new ArrayList<>();
        coordinatesList.add(new LatLng(53.5461, -113.4938));  // Edmonton City Center
        coordinatesList.add(new LatLng(53.5232, -113.5263));  // University of Alberta
        coordinatesList.add(new LatLng(53.5444, -113.4909));  // Rogers Place
        coordinatesList.add(new LatLng(53.5223, -113.6244));  // West Edmonton Mall
        coordinatesList.add(new LatLng(53.5344, -113.4138));  // Edmonton Valley Zoo
        coordinatesList.add(new LatLng(53.5264, -113.5234));  // Old Strathcona
        coordinatesList.add(new LatLng(53.5473, -113.5067));  // Alberta Legislature Building

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(mapboxMap -> {
                mapboxMap.setStyle(new Style.Builder().fromUri(styleUrl), style -> {
                    // Enable zoom controls
                    mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

                    // Center the camera on Edmonton
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(53.5461, -113.4938))  // Edmonton City Center
                            .zoom(12)  // Set a zoom level
                            .build());

                    // Add a marker for each location
                    for (LatLng coordinate : coordinatesList) {
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(coordinate)
                                .title("Marker Title")  // Optional: customize the title
                                .snippet("Additional Info"));  // Optional: customize additional info
                    }
                });
            });
        } else {
            Log.e("MapView", "MapView initialization failed.");
        }
    }

    // Lifecycle methods to manage the MapView's state
    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}