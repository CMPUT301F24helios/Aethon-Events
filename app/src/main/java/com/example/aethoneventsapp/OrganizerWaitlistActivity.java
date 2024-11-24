package com.example.aethoneventsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aethoneventsapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerWaitlistActivity extends AppCompatActivity {

    private ListView listViewWaitlist;
    private ExpandableListView expandableListView;
    private ArrayAdapter<String> adapter;
    private Button poolButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_layout);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        eventsRef = db.collection("Events");

        // Find ExpandableListView
        expandableListView = findViewById(R.id.entrantsExpandableList);



        poolButton = findViewById(R.id.poolButton);
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
    }

    private void updateUIWithEntrants(List<String> categories, Map<String, List<String>> participants) {
        // Assign fetched categories and participants to the global variables
        this.categories = categories;
        this.participants = participants;

        // Set up the ExpandableListAdapter
        EntrantsExpandableListAdapter adapter = new EntrantsExpandableListAdapter(this, categories, participants);
        expandableListView.setAdapter(adapter);
    }
    private void fetchEventDetails() {
        db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    waitlistId = document.getString("waitlistId");
                    capacity = document.getLong("capacity").intValue();
                    waitingList = new WaitingList(waitlistId, eventId); // Initialize with existing waitlistId and eventId
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
                                    List<String> waitlist = (List<String>) document.get("waitlist"); // Retrieve the "waitlist" field

                                    if (waitlist != null) {
                                        Log.d(TAG, "Waitlist for document " + documentId + ": " + waitlist);
                                        // Process the waitlist as needed
                                        for (String entrantID : waitlist) {
                                            waitingList.addEntrantToWaitlist(eventId, entrantID);
                                            entrantsList.add(entrantID);
                                            Log.d(TAG, "Entrant ID: " + entrantID);
                                            // Add entrant to your local lists or UI updates
                                        }
                                    } else {
                                        Log.d(TAG, "No waitlist found for document " + documentId);
                                    }
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
                                        participants
                                );
                                expandableListView.setAdapter(adapter);
                            }
                        } else {
                            Log.d(TAG, "Error fetching " + category + " documents: ", task.getException());
                        }
                    });
        }
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
}