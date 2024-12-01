package com.example.aethoneventsapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditEventActivity extends AppCompatActivity {
    private TextView nameEditText, dateEditText, locationEditText, capacityEditText, descriptionEditText, imageUrlEditText;
    private Button removeImageBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId; // Event ID to identify the event being edited
    private ImageView eventImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize views
        nameEditText = findViewById(R.id.event_name);
        dateEditText = findViewById(R.id.event_date);
        locationEditText = findViewById(R.id.event_loc);
        eventImageView = findViewById(R.id.EventImageView);
        removeImageBtn = findViewById(R.id.removeEventPictureButton);

        // Get event details from intent
        eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("name");
        String eventDate = getIntent().getStringExtra("date");
        String location = getIntent().getStringExtra("location");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        Event eventObject = (Event) getIntent().getSerializableExtra("eventObject");


        // Populate fields with event details
        nameEditText.setText(eventName);
        dateEditText.setText(eventDate);
        locationEditText.setText(location);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(eventImageView);
        } else {
            eventImageView.setImageResource(R.drawable.ic_profile_placeholder);
        }

        removeImageBtn.setOnClickListener(v -> removeEventPicture(eventObject));
        Button backButton = findViewById(R.id.eventBackButton);
        backButton.setOnClickListener(v -> {
            // go back to admin view activity
            Intent intent = new Intent(this, AdminMainActivity.class);
            startActivity(intent);
        });
    }

    private void removeEventPicture(Event event) {
        event.setImageUrl("");
        db.collection("Events").document(eventId)
                .update("imageUrl", "")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event picture removed successfully.", Toast.LENGTH_SHORT).show();
                    // go back to admin view activity
                    Intent intent = new Intent(this, AdminEventActivity.class);
                    startActivity(intent);



                })
                .addOnFailureListener(e -> {
                    Log.e("EditEventActivity", "Error removing event picture", e);
                    Toast.makeText(this, "Failed to remove event picture. Please try again later.", Toast.LENGTH_SHORT).show();
                });



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
