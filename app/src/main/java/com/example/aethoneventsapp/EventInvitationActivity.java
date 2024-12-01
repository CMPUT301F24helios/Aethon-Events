package com.example.aethoneventsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventInvitationActivity extends AppCompatActivity {
    private Button acceptButton;
    private Button declineButton;
    private TextView statusTextView;
    private String eventId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        acceptButton = findViewById(R.id.accept_button);
        declineButton = findViewById(R.id.decline_button);
        statusTextView = findViewById(R.id.invited_status);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");
        String entrantId = getIntent().getStringExtra("entrantId");
        

        fetchEventDetails(selectedListItems -> {
            Log.d("EventInvitation", "selectedListItems: " + selectedListItems);
            if (selectedListItems.contains(entrantId)) {
                statusTextView.setText("You are invited!");
                acceptButton.setEnabled(true);
                declineButton.setEnabled(true);
            } else {
                statusTextView.setText("You are not invited.");
            }
        });

        acceptButton.setOnClickListener(v -> acceptInvite());
        declineButton.setOnClickListener(v -> declineInvite());
    }

    private void fetchEventDetails(OnSelectedListFetchedListener listener) {
        db.collection("Events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("EventInvitation", "Event details fetched successfully");

                    // Retrieve the selectedList collection
                    db.collection("Events").document(eventId).collection("SelectedList")
                            .get()
                            .addOnCompleteListener(listTask -> {
                                if (listTask.isSuccessful()) {
                                    List<String> selectedListItems = new ArrayList<>();
                                    for (DocumentSnapshot listDocument : listTask.getResult()) {
                                        selectedListItems.add(listDocument.getId());
                                    }
                                    Log.d("EventInvitation", "selectedList fetched successfully: " + selectedListItems);

                                    // Pass the selectedListItems to the callback
                                    listener.onFetched(selectedListItems);

                                } else {
                                    Log.d("EventInvitation", "Failed to fetch selectedList", listTask.getException());
                                }
                            });

                } else {
                    Log.d("EventInvitation", "No such event found");
                }
            } else {
                Log.d("EventInvitation", "Failed to fetch event details", task.getException());
            }
        });
    }

    // Define a callback interface
    public interface OnSelectedListFetchedListener {
        void onFetched(List<String> selectedListItems);
    }

    private void acceptInvite() {
        String entrantId = getIntent().getStringExtra("entrantId");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference acceptedDocRef = db.collection("Events").document(eventId).collection("Accepted").document(entrantId);
        DocumentReference pendingDocRef = db.collection("Events").document(eventId).collection("Pending").document(entrantId);

        // Add the entrant to the "Accepted" collection
        acceptedDocRef.set(new HashMap<String, Object>())
                .addOnSuccessListener(aVoid -> {
                    // Remove the entrant from the "Pending" collection
                    pendingDocRef.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Invite accepted!", Toast.LENGTH_SHORT).show();
                                // Optionally, disable buttons or update UI
                                acceptButton.setEnabled(false);
                                declineButton.setEnabled(false);
                                statusTextView.setText("You have accepted the invitation.");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to remove from Pending list.", Toast.LENGTH_SHORT).show();
                                Log.w("EventInvitation", "Error removing entrant from Pending list", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to accept invite.", Toast.LENGTH_SHORT).show();
                    Log.w("EventInvitation", "Error adding entrant to Accepted list", e);
                });
    }

    private void declineInvite() {
        String entrantId = getIntent().getStringExtra("entrantId");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference declinedDocRef = db.collection("Events").document(eventId).collection("Declined").document(entrantId);
        DocumentReference pendingDocRef = db.collection("Events").document(eventId).collection("Pending").document(entrantId);

        // Add the entrant to the "Declined" collection
        declinedDocRef.set(new HashMap<String, Object>())
                .addOnSuccessListener(aVoid -> {
                    // Remove the entrant from the "Pending" collection
                    pendingDocRef.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Invite declined!", Toast.LENGTH_SHORT).show();
                                // Optionally, disable buttons or update UI
                                acceptButton.setEnabled(false);
                                declineButton.setEnabled(false);
                                statusTextView.setText("You have declined the invitation.");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to remove from Pending list.", Toast.LENGTH_SHORT).show();
                                Log.w("EventInvitation", "Error removing entrant from Pending list", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to decline invite.", Toast.LENGTH_SHORT).show();
                    Log.w("EventInvitation", "Error adding entrant to Declined list", e);
                });
    }
}