package com.example.aethoneventsapp;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button scan_btn;
    Button signup_btn;
    Button dash_btn;
    ImageView profile_img;
    String deviceId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
        setContentView(R.layout.activity_main);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Button organizerButton = findViewById(R.id.button_organizer);
        organizerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganizerActivity.class);
            intent.putExtra("organizerId", deviceId);
            startActivity(intent);
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        scan_btn = findViewById(R.id.scanner);
        scan_btn.setOnClickListener(v -> {
            // Start QR code scanner activity
            Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
            startActivity(intent);
        });
        signup_btn = findViewById(R.id.button_signup);
        signup_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EventInvitationActivity.class);
            startActivity(intent);
        });

        dash_btn = findViewById(R.id.dash_button);
        dash_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganizerWaitlistActivity.class);
            startActivity(intent);
        });

        profile_img = findViewById(R.id.profile_image);
        profile_img.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button organizerViewButton = findViewById(R.id.organizerView);
        organizerViewButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganizerViewActivity.class);
            String organizerId = deviceId; // Hardcoded organizer ID
            intent.putExtra("organizerId", organizerId);
            Log.d("MainActivity", "Organizer ID: " + organizerId);  // Check if it's passed correctly
            startActivity(intent);
        });

    }
}
