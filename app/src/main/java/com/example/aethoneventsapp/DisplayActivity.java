package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aethoneventsapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DisplayActivity extends NavActivity {


    Button scan_btn;

    TextView textView;
    private ActivityMainBinding binding;
    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventLocation;
    private TextView eventDate;
    private TextView eventDescription;
    private Button joinWaitlistButton;
    private Button leaveWaitlistButton;
    private Button signupsButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.event_display, findViewById(R.id.container));
        db = FirebaseFirestore.getInstance();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize UI components
        eventImage = findViewById(R.id.event_image);
        eventTitle = findViewById(R.id.event_title);
        eventLocation = findViewById(R.id.event_location);
        eventDate = findViewById(R.id.event_date);
        eventDescription = findViewById(R.id.event_description);
        // Retrieve QR code content from Intent
        String qrCodeContent = getIntent().getStringExtra("qrCodeContent");
        updateEventDescription(qrCodeContent); // Pass the qrCodeContent instead of hard-coded ID
        // Set up button to add entrant to Firestore waitlist
        joinWaitlistButton = findViewById(R.id.join_waitlist_button);
        leaveWaitlistButton = findViewById(R.id.leave_waitlist_button);
        signupsButton = findViewById(R.id.button_signup);
        signupsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayActivity.this, EventInvitationActivity.class);
            intent.putExtra("eventId", qrCodeContent);
            intent.putExtra("entrantId", deviceId);
            startActivity(intent);
        });

        joinWaitlistButton.setOnClickListener(v -> addEntrantToWaitlist(qrCodeContent, deviceId)); // Replace "HARD_CODED_ENTRANT_ID" with a real entrant ID if available
        leaveWaitlistButton.setOnClickListener(v -> removeEntrantFromWaitlist(qrCodeContent, deviceId)); // Replace "HARD_CODED_ENTRANT_ID" with a real entrant ID if available

    }

    private void removeEntrantFromWaitlist(String eventUniqueID, String entrantId) {
        // Access the event's "WaitingList" collection and remove the entrant
        db.collection("Events")
                .document(eventUniqueID)
                .collection("WaitingList")
                .document(entrantId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DisplayActivity.this, "Successfully removed from waitlist!", Toast.LENGTH_SHORT).show();
                    removeEventToUserList(eventUniqueID, entrantId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DisplayActivity.this, "Failed to remove from waitlist.", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error removing entrant from waitlist", e);
                });
    }

    /**
     * Method to add an entrant to the waitlist in Firestore.
     *
     * @param eventUniqueID The unique ID of the event in Firestore.
     * @param entrantId     The ID of the entrant to be added to the waitlist.
     */
    private void addEntrantToWaitlist(String eventUniqueID, String entrantId) {
        // Access the event's document to retrieve the limitCapacity
        db.collection("Events").document(eventUniqueID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long limitCapacity = document.getLong("limitCapacity");
                    if (limitCapacity != null) {
                        // Check the current size of the waitlist
                        db.collection("Events").document(eventUniqueID).collection("WaitingList").get()
                                .addOnCompleteListener(waitlistTask -> {
                                    if (waitlistTask.isSuccessful()) {
                                        int currentWaitlistSize = waitlistTask.getResult().size();
                                        if (currentWaitlistSize < limitCapacity) {
                                            // Add the entrant to the waitlist
                                            Map<String, Object> entrantData = new HashMap<>();
                                            entrantData.put("entrantId", entrantId);
                                            entrantData.put("timestamp", System.currentTimeMillis()); // Optional: Add timestamp if needed

                                            db.collection("Events")
                                                    .document(eventUniqueID)
                                                    .collection("WaitingList")
                                                    .document(entrantId)
                                                    .set(entrantData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(DisplayActivity.this, "Successfully added to waitlist!", Toast.LENGTH_SHORT).show();
                                                        addEventToUserList(eventUniqueID, entrantId);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(DisplayActivity.this, "Failed to add to waitlist.", Toast.LENGTH_SHORT).show();
                                                        Log.e("Firestore", "Error adding entrant to waitlist", e);
                                                    });
                                        } else {
                                            Toast.makeText(DisplayActivity.this, "Waitlist is full. Cannot add entrant.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(DisplayActivity.this, "Failed to check waitlist size.", Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore", "Error checking waitlist size", waitlistTask.getException());
                                    }
                                });
                    } else {
                        Toast.makeText(DisplayActivity.this, "Event limit capacity not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DisplayActivity.this, "Event not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DisplayActivity.this, "Failed to retrieve event details.", Toast.LENGTH_SHORT).show();
                Log.e("Firestore", "Error retrieving event details", task.getException());
            }
        });
    }
    private void removeEventToUserList(String eventUniqueID, String entrantId) {
        // Search for the user document with the matching deviceId
        db.collection("users")
                .whereEqualTo("deviceId", entrantId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first matching document
                        String userDocId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Check if the 'Events' list exists
                        db.collection("users")
                                .document(userDocId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists() && documentSnapshot.contains("Events")) {
                                        // Update the existing list by removing the event
                                        db.collection("users")
                                                .document(userDocId)
                                                .update("Events", FieldValue.arrayRemove(eventUniqueID))
                                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event removed from user's Events list."))
                                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to remove event from Events list.", e));
                                    } else {
                                        Log.d("Firestore", "User document exists but 'Events' list does not contain the event.");
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user document.", e));
                    } else {
                        Log.e("Firestore", "No user found with the given deviceId.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error querying users collection.", e));
    }

    private void addEventToUserList(String eventUniqueID, String entrantId) {
        // Search for the user document with the matching deviceId
        db.collection("users")
                .whereEqualTo("deviceId", entrantId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first matching document
                        String userDocId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Check if the 'Events' list exists
                        db.collection("users")
                                .document(userDocId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        if (documentSnapshot.contains("Events")) {
                                            // Update the existing list
                                            db.collection("users")
                                                    .document(userDocId)
                                                    .update("Events", FieldValue.arrayUnion(eventUniqueID))
                                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event added to user's Events list."))
                                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to update Events list.", e));
                                        } else {
                                            // Create a new 'Events' list
                                            Map<String, Object> eventsMap = new HashMap<>();
                                            eventsMap.put("Events", Arrays.asList(eventUniqueID));
                                            db.collection("users")
                                                    .document(userDocId)
                                                    .set(eventsMap, SetOptions.merge())
                                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "New Events list created for user."))
                                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to create Events list.", e));
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user document.", e));
                    } else {
                        Log.e("Firestore", "No user found with the given deviceId.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error querying users collection.", e));
    }
    /**
     * Fetches event data from Firestore based on the unique event ID and updates the UI.
     *
     * @param eventUniqueID The unique ID of the event document in Firestore.
     */
    private void updateEventDescription(String eventUniqueID) {
        // Access the 'events' collection and get the document with the specified event ID
        db.collection("Events")
                .document(eventUniqueID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve data from the document
                        String title = documentSnapshot.getString("name");
                        String location = documentSnapshot.getString("location");
//                        String date = documentSnapshot.getString("eventDate");  // Ensure date exists in Firestore
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        // get date from the firebase as well
                        String date = documentSnapshot.getString("eventDate");

                        // Update UI components
                        eventTitle.setText(title);
                        eventLocation.setText(location);
//                        eventDate.setText(date);
                        eventDescription.setText(description);
                        eventDate.setText(date);

                        // Load image using Picasso if imageUrl exists
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(eventImage);
                        }
                    } else {
                        // Handle case where event document doesn't exist
                        eventDescription.setText("Event not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    eventDescription.setText("Failed to load event details.");
                });
    }


}
