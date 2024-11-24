package com.example.aethoneventsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerViewActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private EventAdapter adapter;
    private List<String> eventList = new ArrayList<>();
    private List<Event> ListOfEvents = new ArrayList<>();
    private Button organizerButton;
    private String deviceId;
    private String eventIdToUpload;
    private int eventIndexToUpload;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, String> eventIdMap = new HashMap<>(); // Map to store event details and their corresponding event IDs

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        organizerButton = findViewById(R.id.button_organizer);
        organizerButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerViewActivity.this, OrganizerActivity.class);
            intent.putExtra("organizerId", deviceId);
            startActivity(intent);
        });

        listViewEvents = findViewById(R.id.ListViewEvents);
        adapter = new EventAdapter(this, ListOfEvents);
        listViewEvents.setAdapter(adapter);

        // Get organizerId from intent
        String organizerId = getIntent().getStringExtra("organizerId");
        if (organizerId == null) {
            Log.e("OrganizerViewActivity", "Organizer ID is null");
            Toast.makeText(this, "Organizer ID not found", Toast.LENGTH_SHORT).show();
            return;  // Prevent crash if organizer ID is null
        } else {
            Log.d("OrganizerViewActivity", "Organizer ID: " + organizerId);
            fetchEvents(organizerId);
        }

        // Set OnItemClickListener to handle item clicks
        listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
            String eventDetails = eventList.get(position);
            Log.d("OrganizerViewActivity", "Selected Event Details: " + eventDetails);
            String eventId = extractEventId(eventDetails);
            Log.d("OrganizerViewActivity", "Event ID: " + eventId);
            if (eventId != null) {
                Intent intent = new Intent(OrganizerViewActivity.this, OrganizerWaitlistActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnItemLongClickListener to handle long-click events
        listViewEvents.setOnItemLongClickListener((parent, view, position, id) -> {
            String eventDetails = eventList.get(position);
            String eventId = extractEventId(eventDetails);
            eventIdToUpload = eventId;
            eventIndexToUpload = position;
            if (eventId != null) {
                Log.d("OrganizerViewActivity", "Long-clicked on Event ID: " + eventId);

                // Show the confirmation dialog
                showConfirmationDialog(eventId);
                return true; // Indicate that the long-click was handled
            } else {
                Toast.makeText(this, "Event ID not found for long click", Toast.LENGTH_SHORT).show();
                return false; // Not handled if Event ID is null
            }
        });
    }

    private void showConfirmationDialog(String eventId) {
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
        dialogTitle.setText("Confirm Image Upload");
        dialogMessage.setText("Are you sure you want to update the image for the event?");


        // Access the buttons defined in the layout
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        // Create the dialog
        AlertDialog alertDialog = dialogBuilder.create();

        // Set the OnClickListener for the "Yes" button (positive button)
        positiveButton.setOnClickListener(v -> {
            // On clicking "Yes", prompt for image upload
            openImagePicker();
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

    private void openImagePicker() {
        // Open the image picker to allow the user to select an image
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the image URI from the result
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Upload the image to Firebase
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Create a reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a unique path for the image
        StorageReference imageRef = storageRef.child("event_images/" + System.currentTimeMillis() + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Now, update the Firestore document with the new image URL
                        updateEventImageUrl(imageUrl );
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OrganizerViewActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEventImageUrl(String imageUrl) {
        // Update the event document with the new image URL
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .document(eventIdToUpload)
                .update("imageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(OrganizerViewActivity.this, "Image updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OrganizerViewActivity.this, "Failed to update event image", Toast.LENGTH_SHORT).show();
                });
        // updating the id of the event in the url
        Event event = ListOfEvents.get(eventIndexToUpload);
        event.setUrl(imageUrl);
        ListOfEvents.set(eventIndexToUpload, event);
        eventIndexToUpload = -1;
        eventIdToUpload = "";
        adapter.notifyDataSetChanged();

    }
    private void fetchEvents(String organizerId) {
        try {
            db.collection("Events")
                    .whereEqualTo("organizerId", organizerId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String eventName = document.getString("name");
                                    String eventDate = document.getString("eventDate");
                                    String location = document.getString("location");
                                    String eventId = document.getLong("eventId").toString();
                                    String url = document.getString("imageUrl");
                                    Event event = new Event(Integer.parseInt(eventId), eventName,location, organizerId, eventDate);
                                    event.setUrl(url);
                                    ListOfEvents.add(event);
                                    String eventDetails = eventName + " - " + eventDate + " @ " + location + " (Event ID: " + eventId + ")";;
                                    eventList.add(eventDetails);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("OrganizerViewActivity", "Error getting events: ", task.getException());
                            Toast.makeText(this, "Error loading events", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("OrganizerViewActivity", "Error during event fetch: ", e);
            Toast.makeText(this, "Error fetching events", Toast.LENGTH_SHORT).show();
        }

    }

    private String extractEventId(String eventDetails) {
        // Assuming the format is "eventName - eventDate @ location (Event ID: eventId)"
        int startIndex = eventDetails.indexOf("(Event ID: ") + 11;
        int endIndex = eventDetails.indexOf(")", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return eventDetails.substring(startIndex, endIndex).trim();
        }
        return null;
    }

}
