package com.example.aethoneventsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class OrganizerActivity extends AppCompatActivity {

    private EditText editTextName, editTextLocation, editTextCapacity, editTextDescription, editTextEventDate;
    private Button buttonSubmit, buttonUploadImage;
    private ImageView imageViewEventImage;
    private Uri imageUri;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        eventsRef = db.collection("Events");
        editTextName = findViewById(R.id.editTextName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextEventDate = findViewById(R.id.editTextEventDate);
        imageViewEventImage = findViewById(R.id.imageViewEventImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        buttonUploadImage.setOnClickListener(v -> openImagePicker());
        buttonSubmit.setOnClickListener(v -> createEvent());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewEventImage.setImageURI(imageUri);
            imageViewEventImage.setVisibility(View.VISIBLE);
        }
    }

    private void createEvent() {
        // Validate required fields
        if (!validateFields()) {
            return;
        }

        int eventId = generateEventId(); // Implement this method to get a unique ID
        String name = editTextName.getText().toString();
        String location = editTextLocation.getText().toString();
        int capacity = Integer.parseInt(editTextCapacity.getText().toString());
        String description = editTextDescription.getText().toString();
        String eventDate = editTextEventDate.getText().toString();
        String entrantId = generateUniqueId();
        String organizerId = generateUniqueId();
        String waitlistId = generateUniqueId();

        Event event = new Event(eventId, name, location, capacity, description, entrantId, organizerId, waitlistId, eventDate);

        // Check if image is uploaded
        if (imageUri != null) {
            uploadImage(eventId, event, imageUri); // Pass the event object here
        } else {
            addEventToFirestore(eventId, createEventData(event, null));
        }
    }

    private void uploadImage(int eventId, Event event, Uri imageUri) { // Accept Event object
        StorageReference storageRef = storage.getReference();
        StorageReference eventImageRef = storageRef.child("event_images/" + eventId + ".jpg");

        eventImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            eventImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                addEventToFirestore(eventId, createEventData(event, uri.toString()));
            }).addOnFailureListener(e -> Log.e("Firebase", "Failed to get download URL", e));
        }).addOnFailureListener(e -> Log.e("Firebase", "Image upload failed", e));
    }

    private Map<String, Object> createEventData(Event event, String imageUrl) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", event.getName());
        eventData.put("location", event.getLocation());
        eventData.put("capacity", event.getCapacity());
        eventData.put("description", event.getDescription());
        eventData.put("entrantId", event.getEntrantId());
        eventData.put("eventId", event.getEventId());
        eventData.put("organizerId", event.getOrganizerId());
        eventData.put("url", event.getUrl());
        eventData.put("eventDate", event.getEventDate());
        eventData.put("waitlistId", event.getWaitlistId());

        if (imageUrl != null) {
            eventData.put("imageUrl", imageUrl);  // Store the image URL in Firestore
        }
        return eventData;
    }

    private boolean validateFields() {
        if (editTextName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Event name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextLocation.getText().toString().isEmpty()) {
            Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextCapacity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Capacity is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextDescription.getText().toString().isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextEventDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Event date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addEventToFirestore(int eventId, Map<String, Object> eventData) {
        eventsRef.document(String.valueOf(eventId)).set(eventData);
        eventsRef.document(String.valueOf(eventId))
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event successfully added!");
                    Toast.makeText(this, "Event submitted successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    // Navigate to QRCodeActivity and pass the eventId
                    Intent intent = new Intent(OrganizerActivity.this, QRCodeActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding event", e));
    }
    private void clearForm() {
        editTextName.setText("");
        editTextLocation.setText("");
        editTextCapacity.setText("");
        editTextDescription.setText("");
        editTextEventDate.setText("");
    }
    // Generate a unique alphanumeric ID based on current timestamp
    private String generateUniqueId() {
        return Long.toString(System.currentTimeMillis(), 36).toUpperCase();
    }

    // Generate a unique numeric event ID based on current timestamp
    private int generateEventId() {
        long timestamp = System.currentTimeMillis();
        String base36String = Long.toString(timestamp, 36).toUpperCase();

        // Convert the base36 alphanumeric string back to a number (if you need strictly numeric IDs)
        return base36String.hashCode() & Integer.MAX_VALUE; // Ensure positive integer
    }
}

