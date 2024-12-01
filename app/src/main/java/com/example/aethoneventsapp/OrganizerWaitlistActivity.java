package com.example.aethoneventsapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.aethoneventsapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class OrganizerWaitlistActivity extends AppCompatActivity {

    private ListView listViewWaitlist;
    private ExpandableListView expandableListView;
    private ArrayAdapter<String> adapter;
    private Button poolButton;

    private Button MapButton;

    private Button QRButton;
    private List<String> selectedList;
    private WaitingList waitingList;
    private List<String> pendingList;
    private List<String> acceptedList;
    private List<String> declinedList;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference eventsRef;
    private String eventId;
    private String waitlistId;
    private int capacity;
    private List<String> categories;
    private Map<String, List<String>> participants;
    private static final String TAG = "OrganizerWaitlistActivity";
    private TextView eventTitle;
    private TextView eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_layout);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        eventsRef = db.collection("Events");

        // Find TextViews
        eventTitle = findViewById(R.id.eventTitle);
        eventDate = findViewById(R.id.eventDate);

        // Find ExpandableListView
        expandableListView = findViewById(R.id.entrantsExpandableList);

        expandableListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get the list of entrants for the selected category
            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
            String category = categories.get(groupPosition);
            List<String> entrantsList = participants.get(category);
            Log.d(TAG, "Entrants for category " + category + ": " + entrantsList);
            if (entrantsList.size() > 0) {
                showCustomMessageDialog(entrantsList, category);
            }
            else {
                Toast.makeText(OrganizerWaitlistActivity.this, "No entrants found for this category", Toast.LENGTH_SHORT).show();
            }
            return true;
        });



        poolButton = findViewById(R.id.poolButton);
        MapButton = findViewById(R.id.MapButton);
        QRButton = findViewById(R.id.QRButton);
        selectedList = new ArrayList<>();
        pendingList = new ArrayList<>();
        acceptedList = new ArrayList<>();
        declinedList = new ArrayList<>();

        // Get eventId from intent
        eventId = getIntent().getStringExtra("eventId");
        Log.d(TAG, "Event ID: " + eventId);
        if (eventId == null || eventId.isEmpty()) {
            Log.e(TAG, "Event ID is null or empty");
            finish(); // Close the activity if eventId is null
            return;
        }
        fetchEventDetails();

        poolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        MapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });
    }
    private void showMap() {
        // Check if location permissions are granted before showing the map
        if (checkPermissions()) {
            // Start MapActivity
            Intent mapIntent = new Intent(OrganizerWaitlistActivity.this, mapActivity.class);
            mapIntent.putExtra("eventId", eventId); // Pass eventId if needed
            startActivity(mapIntent);
        } else {
            requestPermissions(); // Request necessary permissions if not granted
        }
    }
    private boolean checkPermissions() {
        // Check if location permissions are granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle QR button click
                Intent intent = new Intent(OrganizerWaitlistActivity.this, QRCodeActivity.class);
                Log.d(TAG, "Event ID: " + eventId);
                intent.putExtra("eventId", Integer.parseInt(eventId));
                startActivity(intent);
            }
        });
    }

    private void requestPermissions() {
        // Request location permissions if not granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
    private void updateUIWithEntrants(List<String> categories, Map<String, List<String>> participants) {
        // Assign fetched categories and participants to the global variables
        this.categories = categories;
        this.participants = participants;

        // Set up the ExpandableListAdapter
        EntrantsExpandableListAdapter adapter = new EntrantsExpandableListAdapter(this, categories, participants, eventId);
        expandableListView.setAdapter(adapter);
    }
    private void fetchEventDetails() {
        db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String title = document.getString("name");
                    String date = document.getString("eventDate");
                    waitlistId = document.getString("waitlistId");
                    capacity = document.getLong("capacity").intValue();
                    waitingList = new WaitingList(waitlistId, eventId); // Initialize with existing waitlistId and eventId
                    // Update UI with event details
                    eventTitle.setText(title);
                    eventDate.setText(date);
                    initializeEntrantsLists();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void initializeEntrantsLists() {
        // Define the categories to be initialized
        String[] categoriesArray = {"Accepted", "Declined", "Pending","Waitlist"};
        Map<String, List<String>> fetchedParticipants = new HashMap<>(); // Temporary storage for fetched data

        for (String category : categoriesArray) {
            String updatedCategory = category.equals("Waitlist") ? "WaitingList" : category;
            db.collection("Events")
                    .document(eventId)
                    .collection(updatedCategory) // Reference the subcollection
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            List<String> entrantsList = new ArrayList<>();

                            // Add existing entrants to the list
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                if ("WaitingList".equals(updatedCategory)) {
                                    String documentId = document.getId(); // Document ID
                                    waitingList.addEntrantToWaitlist(eventId, documentId);
                                    entrantsList.add(documentId);
                                }
                                    else {
                                    String entrantId = document.getId();
                                    if (entrantId != null) {
                                        entrantsList.add(entrantId);
                                    }
                                    if ("Accepted".equals(updatedCategory)) {
                                        acceptedList = entrantsList;
                                    }
                                    if ("Declined".equals(updatedCategory)) {
                                        declinedList = entrantsList;
                                    }
                                    if ("Pending".equals(updatedCategory)) {
                                        pendingList = entrantsList;
                                    }
                                }
                            }

                            // Add entrants to the map for this category
                            fetchedParticipants.put(updatedCategory, entrantsList);

                            // After all categories are initialized, update the adapter
                            if (fetchedParticipants.size() == categoriesArray.length) {
                                categories = new ArrayList<>(fetchedParticipants.keySet());
                                participants = fetchedParticipants;
                                updateUIWithEntrants(categories, fetchedParticipants);
                                // Set up the ExpandableListAdapter
                                EntrantsExpandableListAdapter adapter = new EntrantsExpandableListAdapter(
                                        this,
                                        categories,
                                        participants,
                                        eventId
                                );
                                expandableListView.setAdapter(adapter);
                            }
                        } else {
                            Log.d(TAG, "Error fetching " + category + " documents: ", task.getException());
                        }
                    });
        }
    }

    private void clearWaitlist() {
        db.collection("Events").document(eventId).collection("WaitingList").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit().addOnSuccessListener(aVoid -> Log.d(TAG, "Waitlist cleared"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error clearing waitlist", e));
                    } else {
                        Log.w(TAG, "Error getting waitlist documents", task.getException());
                    }
                });
    }

    private void uploadSelectedListToFirestore() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        for (String entrantId : selectedList) {
            eventRef.collection("SelectedList").document(entrantId).set(new HashMap<String, Object>())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant added to SelectedList: " + entrantId))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding entrant to SelectedList", e));
            eventRef.collection("Pending").document(entrantId).set(new HashMap<String, Object>())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant added to Pending: " + entrantId))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding entrant to Pending", e));
        }
    }
    private void showConfirmDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        // Create the AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        // Get the dialog elements
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        // Set up the dialog
        AlertDialog alertDialog = dialogBuilder.create();

        // Set the positive button click listener
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitingList != null) {
                    selectedList = waitingList.manageEntrantSelection(eventId, capacity);
                    uploadSelectedListToFirestore();
                    clearWaitlist();
                    // Update UI or perform further actions with selectedList
                }
                alertDialog.dismiss();
            }
        });

        // Set the negative button click listener
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Show the dialog
        alertDialog.show();
    }

    private void showCustomMessageDialog(List<String> entrantsList, String category) {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notif, null);

        // Create the AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        // Get the dialog elements
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        EditText customMessageInput = dialogView.findViewById(R.id.customMessageInput);
        Button sendButton = dialogView.findViewById(R.id.sendButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set up the dialog
        AlertDialog alertDialog = dialogBuilder.create();

        // Set the send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customMessage = customMessageInput.getText().toString().trim();
                if (!customMessage.isEmpty()) {
                    sendCustomMessageToEntrants(entrantsList, customMessage, category);
                    Toast.makeText(OrganizerWaitlistActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    customMessageInput.setError("Message cannot be empty");
                }
            }
        });

        // Set the negative button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Show the dialog
        alertDialog.show();
    }

    private void sendCustomMessageToEntrants(List<String> entrantsList, String message, String category) {
        CollectionReference notificationsRef = db.collection("Events").document(eventId).collection("Notifications");

        // Determine the prefix based on the category
        String prefix;
        switch (category) {
            case "Accepted":
                prefix = "A-";
                break;
            case "Declined":
                prefix = "D-";
                break;
            case "Pending":
                prefix = "P-";
                break;
            case "Waitlist":
            case "WaitingList":
                prefix = "W-";
                break;
            default:
                prefix = "";
                break;
        }

        // Add the prefix to the message
        String prefixedMessage = prefix + message;

        for (String entrantId : entrantsList) {
            DocumentReference entrantDocRef = notificationsRef.document(entrantId);
            entrantDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, append to the messages list
                        List<String> messages = (List<String>) document.get("messages");
                        if (messages == null) {
                            messages = new ArrayList<>();
                        }
                        messages.add(prefixedMessage);
                        entrantDocRef.update("messages", messages)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Message appended for entrant: " + entrantId))
                                .addOnFailureListener(e -> Log.w(TAG, "Error appending message for entrant", e));
                    } else {
                        // Document does not exist, create a new document with the messages list
                        Map<String, Object> data = new HashMap<>();
                        List<String> messages = new ArrayList<>();
                        messages.add(prefixedMessage);
                        data.put("messages", messages);
                        entrantDocRef.set(data)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Message created for entrant: " + entrantId))
                                .addOnFailureListener(e -> Log.w(TAG, "Error creating message for entrant", e));
                    }
                } else {
                    Log.w(TAG, "Error getting entrant document", task.getException());
                }
            });
        }
    }



}