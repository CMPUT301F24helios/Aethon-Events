package com.example.aethoneventsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.aethoneventsapp.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(NavActivity.this, MainActivity.class));
                        return true;
                    case R.id.navigation_scanner:
                        startActivity(new Intent(NavActivity.this, QRCodeScannerActivity.class));
                        return true;
                    case R.id.navigation_profile:
                        startActivity(new Intent(NavActivity.this, ProfileActivity.class));
                        return true;
                    case R.id.navigation_organizer:
                        startActivity(new Intent(NavActivity.this, OrganizerViewActivity.class));
                        return true;
                }
                return false;
            }
        });
    }
}
