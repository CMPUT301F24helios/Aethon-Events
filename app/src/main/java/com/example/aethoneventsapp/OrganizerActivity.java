package com.example.aethoneventsapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;

public class OrganizerActivity extends AppCompatActivity {

    private EditText editTextName, editTextLocation, editTextCapacity, editTextDescription, editTextLimit, editTextEventDate;
    private Button buttonSubmit, buttonUploadImage;
    private ImageView imageViewEventImage;
    private RadioGroup radioGroupGeolocation;
    private Uri imageUri;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private FirebaseStorage storage;
    private int limitCapacity;
    private boolean geolocationNeeded;

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
        // Set OnClickListener to show DatePickerDialog - CHANGED
        // Set the EditText as not editable so users can't type anything in it
        editTextEventDate.setFocusable(false);
        editTextEventDate.setClickable(true);  // Make it clickable so DatePicker can be triggered

        // Set OnClickListener to show DatePickerDialog
        editTextEventDate.setOnClickListener(v -> showDatePicker());
        imageViewEventImage = findViewById(R.id.imageViewEventImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        editTextLimit = findViewById(R.id.editTextLimit);  // New field for limit capacity
        radioGroupGeolocation = findViewById(R.id.radioGroupGeolocation);

        buttonUploadImage.setOnClickListener(v -> openImagePicker());
        buttonSubmit.setOnClickListener(v -> createEvent());
    }

    private void showDatePicker() {
        // Get the current date to set as the default date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date as YYYY-MM-DD and set it to the EditText
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editTextEventDate.setText(date);
                },
                year,
                month,
                day
        );

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        // Show the dialog
        datePickerDialog.show();
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
        // Update this validation to check YYYY-MM-DD format if DatePicker sets it that way
        if (!eventDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Please enter a valid date in YYYY-MM-DD format", Toast.LENGTH_SHORT).show();
            return;
        }
        // Generate unique IDs for each type
        String entrantId = generateUniqueId("ENTRANT");
        String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String waitlistId = generateUniqueId("WAITLIST");

        limitCapacity = Integer.parseInt(editTextLimit.getText().toString());
        geolocationNeeded = radioGroupGeolocation.getCheckedRadioButtonId() == R.id.radioYes;

        // Store eventId and waitlistId in GlobalDataStore
        GlobalDataStore.getInstance().setData("eventId", eventId);
        GlobalDataStore.getInstance().setData("waitlistId", waitlistId);

        // need to add Malhar code
        Event event = new Event(eventId, name, location, organizerId, eventDate);
        event.setDescription(description);
        event.setWaitlistId(waitlistId);
        event.setCapacity(capacity);
        event.setEntrantId(entrantId);

        // Initialize a new WaitingList for this event
        // Determine if limitCapacity is provided
        WaitingList waitingList;
        if (editTextLimit.getText().toString().isEmpty()) {
            waitingList = new WaitingList(waitlistId, String.valueOf(eventId));
        } else {
            int limitCapacity = Integer.parseInt(editTextLimit.getText().toString());
            waitingList = new WaitingList(waitlistId, String.valueOf(eventId), limitCapacity);
        }
        // Check if image is uploaded
        if (imageUri != null) {
            uploadImage(eventId, event, imageUri, waitingList); // Pass the event object here
        } else {
            addEventToFirestore(eventId, createEventData(event, null),waitingList);
        }
    }

    private void uploadImage(int eventId, Event event, Uri imageUri,WaitingList waitingList) { // Accept Event object
        StorageReference storageRef = storage.getReference();
        StorageReference eventImageRef = storageRef.child("event_images/" + eventId + ".jpg");

        eventImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            eventImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                addEventToFirestore(eventId, createEventData(event, uri.toString()),waitingList);
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
        eventData.put("url", event.getImageUrl());
        eventData.put("eventDate", event.getEventDate());
        eventData.put("waitlistId", event.getWaitlistId());
        eventData.put("limitCapacity", limitCapacity);  // Add limit capacity to Firestore
        eventData.put("geolocationNeeded", geolocationNeeded);

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

    private void addEventToFirestore(int eventId, Map<String, Object> eventData, WaitingList waitingList) {
        eventsRef.document(String.valueOf(eventId)).set(eventData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event successfully added!");

                    // Add the waiting list to Firestore
                    eventsRef.document(String.valueOf(eventId))
                            .collection("WaitingList")
                            .document(waitingList.getWaitlistId())
                            .set(waitingList)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Event and waitlist created successfully!", Toast.LENGTH_SHORT).show();
                                clearForm();
                                Intent intent = new Intent(OrganizerActivity.this, QRCodeActivity.class);
                                intent.putExtra("eventId", eventId);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Failed to add waitlist", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add event", e));
    }
    private void clearForm() {
        editTextName.setText("");
        editTextLocation.setText("");
        editTextCapacity.setText("");
        editTextDescription.setText("");
        editTextEventDate.setText("");
        editTextLimit.setText("");
        // Clear image view
        imageViewEventImage.setImageResource(R.drawable.event1); // Reset to default image
    }
    // Generate a unique alphanumeric ID based on current timestamp and ID type
    private String generateUniqueId(String idType) {
        // Generate base ID using current timestamp
        String baseId = Long.toString(System.currentTimeMillis(), 36).toUpperCase();

        // Add a suffix based on idType to ensure uniqueness
        switch (idType) {
            case "ENTRANT":
                return baseId + "E";
            case "ORGANIZER":
                return baseId + "O";
            case "WAITLIST":
                return baseId + "W";
            default:
                throw new IllegalArgumentException("Invalid ID type");
        }
    }


    // Generate a unique numeric event ID based on current timestamp
    private int generateEventId() {
        long timestamp = System.currentTimeMillis();
        String base36String = Long.toString(timestamp, 36).toUpperCase();

        // Convert the base36 alphanumeric string back to a number (if you need strictly numeric IDs)
        return base36String.hashCode() & Integer.MAX_VALUE; // Ensure positive integer
    }
}

