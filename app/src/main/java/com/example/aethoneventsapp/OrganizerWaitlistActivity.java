package com.example.aethoneventsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class OrganizerWaitlistActivity extends AppCompatActivity {

    private ListView listViewWaitlist;
    private ArrayAdapter<String> adapter;
    private Button poolButton;
    private List<String> selectedList;
    private WaitingList waitingList;
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

        listViewWaitlist = findViewById(R.id.entrantsListView);
        poolButton = findViewById(R.id.poolButton);
        selectedList = new ArrayList<>();

        // Get eventId from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Log.e(TAG, "Event ID is null");
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

    private void fetchEventDetails() {
        db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    waitlistId = document.getString("waitlistId");
                    capacity = document.getLong("capacity").intValue();
                    waitingList = new WaitingList(waitlistId, eventId); // Initialize with existing waitlistId and eventId
                    initializeWaitingList();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void initializeWaitingList() {
        // Fetch the waitlist from Firestore
        db.collection("Events").document(eventId).collection("WaitingList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String entrantId = document.getId();
                    waitingList.addEntrantToWaitlist(eventId, entrantId);
                }

                // Set up the adapter after initializing the waitingList
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, waitingList.getWaitList());
                listViewWaitlist.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void uploadSelectedListToFirestore() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        for (String entrantId : selectedList) {
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