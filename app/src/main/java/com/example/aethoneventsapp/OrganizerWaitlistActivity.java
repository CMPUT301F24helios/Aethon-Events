package com.example.aethoneventsapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aethoneventsapp.R;
import com.example.aethoneventsapp.WaitingList;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrganizerWaitlistActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    Button poolButton;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> selectedList;
    WaitingList waitingList;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference eventsRef;
    private String eventId;
    private String waitlistId;
    private int capacity;
    private static final String TAG = "OrganizerWaitlistActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitlist_layout);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        eventsRef = db.collection("Events");

        expandableListView = findViewById(R.id.entrantsListView);
        poolButton = findViewById(R.id.poolButton);
        selectedList = new ArrayList<>();
        eventId = "725174624";

        fetchEventDetails();

        poolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    private void fetchEventDetails() {
        db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    waitlistId = document.getString("waitlistId");
                    capacity = document.getLong("capacity").intValue();
                    waitingList = document.toObject(WaitingList.class);
                    Log.d(TAG, "Waitlist maxWaitlistSize: " + waitingList.getMaxWaitlistSize());
                    initializeWaitingList(waitingList);



                    Log.d(TAG, "Event details fetched successfully");
                    Log.d(TAG, "Waitlist ID: " + waitlistId);
                    Log.d(TAG, "Capacity: " + capacity);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void initializeWaitingList(WaitingList waitingList) {
        // Fetch the waitlist from Firestore
        db.collection("Events").document(eventId).collection("WaitingList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Waitlist fetched successfully");
                QuerySnapshot querySnapshot = task.getResult();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String entrantId = document.getId();
                    waitingList.addEntrantToWaitlist(eventId, entrantId);
                }

                com.example.yourapp.ExpandableListAdapter listAdapter = new com.example.yourapp.ExpandableListAdapter(this, listDataHeader, listDataChild);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void uploadSelectedListToFirestore() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        Log.d(TAG, "Selected list size: " + selectedList.size());
        for (String entrantId : selectedList) {
            Log.d(TAG, "Uploading entrant to SelectedList: " + entrantId);
            eventRef.collection("SelectedList").document(entrantId).set(new HashMap<String, Object>())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant added to SelectedList: " + entrantId))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding entrant to SelectedList", e));
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
                Log.d(TAG, "Positive button clicked");
                Log.d(TAG, "WaitistId: " + waitingList.getEventId());
                Log.d(TAG, "Waitlist: " + waitingList.getWaitList());
                if (waitingList != null) {
                    selectedList = waitingList.manageEntrantSelection(eventId, capacity);
                    Log.d(TAG, "Selected list size: " + selectedList.size());
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

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add("Group 1");
        listDataHeader.add("Group 2");
        listDataHeader.add("Group 3");

        // Adding child data
        List<String> group1 = new ArrayList<>();
        group1.add("Entrant 1");
        group1.add("Entrant 2");
        group1.add("Entrant 3");

        List<String> group2 = new ArrayList<>();
        group2.add("Entrant 4");
        group2.add("Entrant 5");

        List<String> group3 = new ArrayList<>();
        group3.add("Entrant 6");
        group3.add("Entrant 7");
        group3.add("Entrant 8");

        listDataChild.put(listDataHeader.get(0), group1);
        listDataChild.put(listDataHeader.get(1), group2);
        listDataChild.put(listDataHeader.get(2), group3);

        // Add entrants to the waiting list
        for (String entrant : group1) {
            waitingList.addEntrantToWaitlist("event1", entrant);
        }
        for (String entrant : group2) {
            waitingList.addEntrantToWaitlist("event1", entrant);
        }
        for (String entrant : group3) {
            waitingList.addEntrantToWaitlist("event1", entrant);
        }
    }
}