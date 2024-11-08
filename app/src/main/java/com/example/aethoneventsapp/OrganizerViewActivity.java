package com.example.aethoneventsapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerViewActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private ArrayAdapter<String> adapter;
    private List<String> eventList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_view);

        listViewEvents = findViewById(R.id.ListViewEvents);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventList);
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

                                    String eventDetails = eventName + " - " + eventDate + " @ " + location;
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

}
