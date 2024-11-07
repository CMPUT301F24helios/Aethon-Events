package com.example.aethoneventsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventInvitationActivity extends AppCompatActivity {
    private Button acceptButton;
    private Button declineButton;
    private TextView statusTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        acceptButton = findViewById(R.id.accept_button);
        declineButton = findViewById(R.id.decline_button);
        statusTextView = findViewById(R.id.invited_status);
        String entrant = "1234567890";
        // Retrieve eventId and waitlistId
        Integer storedEventId = (Integer) GlobalDataStore.getInstance().getData("eventId"); // Retrieve as Integer
        String eventId = storedEventId.toString(); // Convert to String if needed

        String waitlistId = (String) GlobalDataStore.getInstance().getData("waitlistId"); // waitlistId is already a String        String waitlistId = (String) GlobalDataStore.getInstance().getData("waitlistId");

        if (eventId != null && waitlistId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Events").document(eventId)
                    .collection("WaitingList").document(waitlistId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            WaitingList waitingList = documentSnapshot.toObject(WaitingList.class);
                            ArrayList<String> waitList = waitingList.getWaitList();
                            if (waitList.contains(entrant)){
                                statusTextView.setText("Invited to Event");
                            };
                        } else {
                            Log.d("Firestore", "Waiting List not found");
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error fetching Waiting List", e));
        } else {
            Log.w("Intent", "No WAITLIST_ID or eventId passed to EventInvitationActivity");
        }

        // Todo add functionality to accept and decline invite
//        acceptButton.setOnClickListener(v -> {
//
//        });
//        declineButton.setOnClickListener(v -> {});

    }
}
