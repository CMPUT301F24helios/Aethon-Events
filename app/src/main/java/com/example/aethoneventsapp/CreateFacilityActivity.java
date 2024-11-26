package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * The Create facility activity.
 */
public class CreateFacilityActivity extends AppCompatActivity {
    // Facility ID = Device ID
    // Organizer ID = Device ID
    // See where I'm going with this :)  [That's how we link the both]
    private EditText editName;
    private EditText editLocation;
    private EditText editCapacity;
    private EditText editDescription;
    private Button submitButton;

    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_facility);
        db = FirebaseFirestore.getInstance();

        editName = findViewById(R.id.edit_name);
        editLocation = findViewById(R.id.edit_address);
        editCapacity = findViewById(R.id.edit_capacity);
        editDescription = findViewById(R.id.edit_description);
        submitButton = findViewById(R.id.submit_btn);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String location = editLocation.getText().toString().trim();
                int capacity;
                try {
                    capacity = Integer.parseInt(editCapacity.getText().toString().trim());
                } catch (NumberFormatException e) {
                    capacity = -1;
                }

                String description = editDescription.getText().toString().trim();

                if (name.isEmpty()) {
                    editName.setError("Facility Name is required!");
                    editName.requestFocus();
                    return;
                }

                if (location.isEmpty()) {
                    editLocation.setError("Facility Location is required!");
                    editLocation.requestFocus();
                    return;
                }

                if (editCapacity.getText().toString().trim().isEmpty()) {
                    editCapacity.setError("Facility Capacity is required");
                    editCapacity.requestFocus();
                    return;
                }

                if (description.isEmpty()) {
                    editDescription.setError("Facility Description is required");
                    editDescription.requestFocus();
                    return;
                }
                Facility facility = new Facility(deviceId, name, location, capacity, description, deviceId);
                registerFacility(facility);
                startActivity(new Intent(CreateFacilityActivity.this, MainActivity.class));
            }
        });
    }

    private void registerFacility(Facility f) {
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", f.getFacilityId());
        facilityData.put("name", f.getName());
        facilityData.put("location", f.getLocation());
        facilityData.put("capacity", f.getCapacity());
        facilityData.put("description", f.getDescription());
        facilityData.put("organizerId", f.getOrganizer());

        // setting ID of document as DeviceID. This automatically makes it unique
        // and enforces 1 facility per user.
        db.collection("facilities").document(deviceId)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Registering Facility with ID: "+ deviceId);
                    Toast.makeText(CreateFacilityActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateFacilityActivity.this, ProfileActivity.class));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error registering facilty");
                    Toast.makeText(CreateFacilityActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateFacilityActivity.this, ProfileActivity.class));
                });
    }
}

