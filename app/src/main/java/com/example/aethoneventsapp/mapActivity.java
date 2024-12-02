package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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
    private FirebaseFirestore db;
    private String eventId;
    private static final String TAG = "mapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Mapbox and Firestore
        Mapbox.getInstance(this);
        db = FirebaseFirestore.getInstance();

        // Set your layout
        setContentView(R.layout.activity_event_map);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        Intent intent = getIntent();

        // Retrieve the event ID passed from the previous activity
        if (intent != null) {
            eventId = intent.getStringExtra("eventId");
            if (eventId != null) {
                Log.d(TAG, "Received eventId: " + eventId);
            } else {
                Log.d(TAG, "No eventId was passed with the intent.");
            }
        }

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(mapboxMap -> {
                String key = BuildConfig.MAPTILER_API_KEY;
                String mapId = "streets-v2";
                String styleUrl = "https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + key;

                mapboxMap.setStyle(new Style.Builder().fromUri(styleUrl), style -> {
                    mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

                    // Fetch and display user coordinates
                    getCoordinates(new CoordinatesCallback() {
                        @Override
                        public void onResult(List<LatLng> coordinatesList) {
                            if (!coordinatesList.isEmpty()) { // Check if the list is not empty
                                // Set the camera position to the first coordinate in the list
                                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                        .target(coordinatesList.get(0)) // Default center, e.g., Edmonton
                                        .zoom(10) // Set the zoom level
                                        .build());

                                // Add markers for each coordinate in the list
                                for (LatLng coordinate : coordinatesList) {
                                    mapboxMap.addMarker(new MarkerOptions()
                                            .position(coordinate)
                                            .title("User Location"));
                                }
                            } else {
                                // Notify the user when the list is empty
                                Toast.makeText(mapActivity.this, "No members in the waiting list", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error fetching coordinates: " + error);
                        }
                    });
                });
            });
        } else {
            Log.e(TAG, "MapView initialization failed.");
        }
    }
    private void getCoordinates(CoordinatesCallback callback) {
        // Fetch the waitlist from Firestore
        db.collection("Events")
                .document(eventId)
                .collection("WaitingList")
                .get()
                .addOnSuccessListener(waitlistSnapshot -> {
                    List<String> waitlist = new ArrayList<>();
                    for (DocumentSnapshot doc : waitlistSnapshot.getDocuments()) {
                        waitlist.add(doc.getId());
                    }
                    Log.d(TAG, "Waitlist: " + waitlist.toString());
                    // Fetch user coordinates for the waitlist
                    db.collection("users")
                            .get()
                            .addOnSuccessListener(userSnapshot -> {
                                List<LatLng> coordinatesList = new ArrayList<>();
                                for (DocumentSnapshot document : userSnapshot.getDocuments()) {
                                    String userId = document.getString("deviceId");
                                    if (waitlist.contains(userId)){
                                        GeoPoint geoPoint = document.getGeoPoint("coordinates"); // Replace "coordinates" with actual field name
                                        if (geoPoint != null) {
                                            LatLng latLng = new LatLng(
                                                    geoPoint.getLatitude(),
                                                    geoPoint.getLongitude()
                                            );
                                            coordinatesList.add(latLng);
                                            // Print each coordinate
                                            Log.d(TAG, "User Coordinate: " + latLng);
                                        }
                                    }

                                }
                                // Log the entire coordinatesList
                                Log.d(TAG, "Coordinates List: " + coordinatesList.toString());

                                callback.onResult(coordinatesList);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to fetch user coordinates", e);
                                callback.onError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch waitlist", e);
                    callback.onError(e.getMessage());
                });
    }
    // Callback interface for asynchronous results
    interface CoordinatesCallback {
        void onResult(List<LatLng> coordinatesList);

        void onError(String error);
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
