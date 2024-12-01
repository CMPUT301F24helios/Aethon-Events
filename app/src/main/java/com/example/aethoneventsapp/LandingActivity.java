package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Boolean isAdmin = document.getBoolean("isAdmin");
                            if (isAdmin != null && isAdmin) {
                                // User is an admin, navigate to admin page
                                navigateToAdminMode();
                            } else {
                                // User is not an admin, go to Main App
                                navigateToMainApp();
                            }
                        }
                    } else {
                        // No matching user found or error occurred
                        navigateToProfileSetup();

                    }
                });
    }

    // Method to navigate to the main part of the app for recognized devices
    private void navigateToMainApp() {
        startActivity(new Intent(LandingActivity.this, MainActivity.class));
        finish();
    }

    // Method to navigate to profile setup or onboarding for new devices
    private void navigateToProfileSetup() {
        startActivity(new Intent(LandingActivity.this, SignUpActivity.class));
        finish();
    }

    private void navigateToAdminMode(){
        startActivity(new Intent(LandingActivity.this, AdminMainActivity.class));
        finish();
    }

}
