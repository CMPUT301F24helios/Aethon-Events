package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventActivity extends AppCompatActivity {
    private RecyclerView recyclerViewEvents;
    private List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_view);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(this, eventList, event -> {
            Toast.makeText(this, "Edit clicked for: " + event.getName(), Toast.LENGTH_SHORT).show();
        }, db);
        recyclerViewEvents.setAdapter(adapter);

//        fetchEvents();

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                filterEvents(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchEventFromFirestore();

    }
    private void filterEvents(String query) {
        List<Event> filteredList = new ArrayList<>();

        for (Event event : eventList) {
            if (event.getName().toLowerCase().contains(query) ||
                    event.getEventDate().toLowerCase().contains(query) ||
                    event.getLocation().toLowerCase().contains(query)) {
                filteredList.add(event);
            }
        }

        adapter.setEventList(filteredList);
    }

    private void fetchEventFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")  // Listen to changes in the "users" collection
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminActivity", "Error fetching users", error);
                        return;
                    }
                    if (value != null) {
                        eventList.clear();  // Clear the list before adding updated data
                        for (QueryDocumentSnapshot document : value) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event);  // Add each user to the list
                        }
                        adapter.notifyDataSetChanged();  // Notify adapter that the data has changed
                    }
                });
    }



    private int findEventPosition(Event updatedEvent) {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getEventId() == updatedEvent.getEventId()) {
                return i;
            }
        }
        return -1; // Return -1 if not found
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Event updatedEvent = (Event) data.getSerializableExtra("updatedEvent");
            int position = findEventPosition(updatedEvent); // Method to locate the event position
            if (position != -1) {
                eventList.set(position, updatedEvent); // Update the list
                adapter.notifyItemChanged(position); // Notify adapter about the change
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    private void fetchEvents() {
        db.collection("Events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventName = document.getString("name");
                    String eventDate = document.getString("eventDate");
                    String location = document.getString("location");
                    String imageUrl = document.getString("imageUrl"); // Retrieve image URL
                    int eventId = document.getLong("eventId").intValue();

                    // Optional fields
                    int capacity = document.getLong("capacity") != null ? document.getLong("capacity").intValue() : 0;
                    String description = document.getString("description");
                    Object waitlistIdObject = document.get("waitlistId");
                    String waitlistId = (waitlistIdObject instanceof String) ? (String) waitlistIdObject : null;
                    Object entrantIdObject = document.get("entrantId");
                    String entrantId = (entrantIdObject instanceof String) ? (String) entrantIdObject : null;
                    String organizerId = document.getString("organizerId");

                    eventList.add(new Event(eventId, eventName, location,  capacity,  description,  waitlistId,  entrantId,  organizerId,  eventDate, imageUrl ));
                }
                adapter.notifyDataSetChanged();
                Log.d("FetchEvents", "Event list updated: " + eventList.size());
            } else {
                Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
