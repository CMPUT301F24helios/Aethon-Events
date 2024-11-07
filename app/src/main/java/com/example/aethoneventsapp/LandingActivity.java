package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aethoneventsapp.MainActivity;
import com.example.aethoneventsapp.ProfileActivity;
import com.example.aethoneventsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LandingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        // Get the unique device ID
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize FireStore instance
        db = FirebaseFirestore.getInstance();

        // Check if the device is already recognized in the database
        checkDeviceRecognition();
    }

    private void checkDeviceRecognition() {
        // Reference to the "devices" collection
        CollectionReference devicesCollection = db.collection("devices");

        // Query to check if the device ID already exists in the Firestore collection
        devicesCollection.whereEqualTo("deviceId", deviceId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Device is recognized
                            Toast.makeText(LandingActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            navigateToMainApp();
                        } else {
                            // Device not recognized, save it to Firestore
                            saveNewDevice();
                            navigateToProfileSetup();
                        }
                    }
                });
    }

    private void saveNewDevice() {
        // Create a new document with the device ID in the "devices" collection
        db.collection("devices").add(new Device(deviceId));
    }

    // Method to navigate to the main part of the app for recognized devices
    private void navigateToMainApp() {
        startActivity(new Intent(LandingActivity.this, MainActivity.class));
        finish();
    }

    // Method to navigate to profile setup or onboarding for new devices
    private void navigateToProfileSetup() {
        startActivity(new Intent(LandingActivity.this, ProfileActivity.class));
        finish();
    }

    // Device model class
    public static class Device {
        private String deviceId;

        public Device() {} // Needed for Firestore

        public Device(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceId() {
            return deviceId;
        }

    }
}
