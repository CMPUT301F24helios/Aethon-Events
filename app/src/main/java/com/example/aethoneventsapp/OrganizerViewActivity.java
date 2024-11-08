package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerViewActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private ArrayAdapter<String> adapter;
    private List<String> eventList = new ArrayList<>();
    private Button organizerButton;
    private String deviceId;
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
        adapter = new ArrayAdapter<>(this, R.layout.activity_organizer_view_textview, eventList);
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
