package com.example.aethoneventsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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

        eventId = "730587294";
        String entrantId = "1";  // This could be passed via an Intent or other data source

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
        Toast.makeText(this, "Invite accepted!", Toast.LENGTH_SHORT).show();
    }

    private void declineInvite() {
        Toast.makeText(this, "Invite declined!", Toast.LENGTH_SHORT).show();
    }
}