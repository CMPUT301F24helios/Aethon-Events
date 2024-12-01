package com.example.aethoneventsapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.aethoneventsapp.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;
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
    private static final String TAG = "DisplayActivity";
    private static final int LOCATION_REQUEST_CODE = 1001; // Arbitrary value for request code
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

        joinWaitlistButton.setOnClickListener(v -> {
            // Check if geolocation is required
            Log.e(TAG, "Join Waitlist button clicked.");
            db.collection("Events")
                    .document(qrCodeContent)  // Use qrCodeContent as the eventId
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get the geolocationNeeded field (boolean value)
                            Boolean needGeo = documentSnapshot.getBoolean("geolocationNeeded");

                            if (needGeo != null && needGeo) {
                                // If geolocation is required, show the confirmation dialog
                                showConfirmationDialog(qrCodeContent, deviceId);
                            } else {
                                // If geolocation is not required, proceed with adding to the waitlist
                                addEntrantToWaitlist(qrCodeContent, deviceId);
                            }
                        } else {
                            Log.e(TAG, "Event not found.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching event data", e);
                    });
        });
        leaveWaitlistButton.setOnClickListener(v -> removeEntrantFromWaitlist(qrCodeContent, deviceId)); // Replace "HARD_CODED_ENTRANT_ID" with a real entrant ID if available

    }
    private void fetchAndHandleUserLocation(String qrCodeContent, String deviceId) {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        // Initialize the location client
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Extract latitude and longitude
                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();
                        db.collection("users")
                                .whereEqualTo("deviceId", deviceId)  // Query the users collection where deviceId matches
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    if (!userSnapshot.isEmpty()) {  // Check if any document was found
                                        for (DocumentSnapshot document : userSnapshot.getDocuments()) {
                                            String docId = document.getId();  // Get the document ID
                                            String userId = document.getString("deviceId");  // Get the deviceId from the document

                                            if (userId != null && userId.equals(deviceId)) {  // Ensure deviceId matches and not null
                                                // Ensure latitude and longitude are valid
                                                if (latitude != null && longitude != null) {
                                                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);  // Create GeoPoint

                                                    // Prepare data to write
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("coordinates", geoPoint);

                                                    // Write GeoPoint to Firestore
                                                    db.collection("users").document(docId)
                                                            .set(data, SetOptions.merge())  // Merge to add new field without overwriting
                                                            .addOnSuccessListener(aVoid -> {
                                                                Log.d("FirestoreWrite", "GeoPoint written for userId: " + userId);
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("FirestoreError", "Error writing GeoPoint for userId: " + userId, e);
                                                            });
                                                } else {
                                                    Log.e("FirestoreWrite", "Latitude or Longitude is null for userId: " + userId);
                                                }
                                            }
                                        }
                                    } else {
                                        Log.e("FirestoreQuery", "No user found with deviceId: " + deviceId);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Error fetching users", e);
                                });
                        Log.d(TAG, "User's Location: Latitude = " + latitude + ", Longitude = " + longitude);

                    } else {
                        Log.e(TAG, "Location is null. Ensure location services are enabled.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get location", e);
                });
    }
    private void showConfirmationDialog(String eventId,String entrantId) {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        // Create the AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        // Access the TextViews and change the text dynamically
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);

        // Set custom text for the dialog title and message
        dialogTitle.setText("This event requires your location");
        dialogMessage.setText("Are you sure you want to continue?");


        // Access the buttons defined in the layout
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        // Create the dialog
        AlertDialog alertDialog = dialogBuilder.create();

        // Set the OnClickListener for the "Yes" button (positive button)
        positiveButton.setOnClickListener(v -> {
            // On clicking "Yes", prompt for image upload
            fetchAndHandleUserLocation( eventId, entrantId);
            addEntrantToWaitlist(eventId, entrantId);
            alertDialog.dismiss();
        });
        // Set the OnClickListener for the "No" button (negative button)
        negativeButton.setOnClickListener(v -> {
            // On clicking "No", dismiss the dialog
            alertDialog.dismiss();
        });
        // Show the dialog
        alertDialog.show();
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
