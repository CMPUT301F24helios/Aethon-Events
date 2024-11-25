package com.example.aethoneventsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditEventActivity extends AppCompatActivity {
    private EditText nameEditText, dateEditText, locationEditText, capacityEditText, descriptionEditText, imageUrlEditText;
    private Button saveButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId; // Event ID to identify the event being edited

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize views
        nameEditText = findViewById(R.id.edit_event_name);
        dateEditText = findViewById(R.id.edit_event_date);
        locationEditText = findViewById(R.id.edit_event_location);
        capacityEditText = findViewById(R.id.edit_event_capacity);
        descriptionEditText = findViewById(R.id.edit_event_description);
        imageUrlEditText = findViewById(R.id.edit_event_image_url);
        saveButton = findViewById(R.id.save_button);

        // Get event details from intent
        eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("name");
        String eventDate = getIntent().getStringExtra("date");
        String location = getIntent().getStringExtra("location");
        int capacity = getIntent().getIntExtra("capacity", 0);
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Populate fields with event details
        nameEditText.setText(eventName);
        dateEditText.setText(eventDate);
        locationEditText.setText(location);
        capacityEditText.setText(String.valueOf(capacity));
        descriptionEditText.setText(description);
        imageUrlEditText.setText(imageUrl);

        // Save button click listener
        saveButton.setOnClickListener(v -> validateAndSaveEvent());
    }

    private void validateAndSaveEvent() {
        String updatedName = nameEditText.getText().toString().trim();
        String updatedDate = dateEditText.getText().toString().trim();
        String updatedLocation = locationEditText.getText().toString().trim();
        String capacityString = capacityEditText.getText().toString().trim();
        String updatedDescription = descriptionEditText.getText().toString().trim();
        String updatedImageUrl = imageUrlEditText.getText().toString().trim();

        // Validate inputs
        if (updatedName.isEmpty() || updatedDate.isEmpty() || updatedLocation.isEmpty() || capacityString.isEmpty()) {
            Toast.makeText(this, "All fields are required except description.", Toast.LENGTH_SHORT).show();
            return;
        }

        int updatedCapacity;
        try {
            updatedCapacity = Integer.parseInt(capacityString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Capacity must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for Firestore update
        updateEventInFirestore(updatedName, updatedDate, updatedLocation, updatedCapacity, updatedDescription, updatedImageUrl);
    }

    private void updateEventInFirestore(String name, String date, String location, int capacity, String description, String imageUrl) {
        db.collection("Events").document(eventId)
                .update(
                        "name", name,
                        "eventDate", date,
                        "location", location,
                        "capacity", capacity,
                        "description", description,
                        "imageUrl", imageUrl
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event updated successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving
                })
                .addOnFailureListener(e -> {
                    Log.e("EditEventActivity", "Error updating event", e);
                    Toast.makeText(this, "Failed to update event. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }
}
