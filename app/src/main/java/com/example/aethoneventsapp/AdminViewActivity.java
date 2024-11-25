package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminViewActivity extends AppCompatActivity {
    private RecyclerView recyclerViewEvents;
    private List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_view);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(this, eventList, event -> {
            Toast.makeText(this, "Edit clicked for: " + event.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewEvents.setAdapter(adapter);

        fetchEvents();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int eventId = Integer.parseInt(data.getStringExtra("eventId"));
            String updatedName = data.getStringExtra("name");
            String updatedDate = data.getStringExtra("date");
            String updatedLocation = data.getStringExtra("location");
            int updatedCapacity = data.getIntExtra("capacity", 0);
            String updatedDescription = data.getStringExtra("description");
            String updatedImageUrl = data.getStringExtra("imageUrl");

            // Update the specific event in the event list
            for (Event event : eventList) {
                if (event.getEventId() == eventId) {
                    event.setName(updatedName);
                    event.setEventDate(updatedDate);
                    event.setLocation(updatedLocation);
                    event.setCapacity(updatedCapacity);
                    event.setDescription(updatedDescription);
                    event.setImageUrl(updatedImageUrl);
                    break;
                }
            }

            // Notify the adapter to refresh the RecyclerView
            adapter.notifyDataSetChanged();
        }
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

                    eventList.add(new Event(eventId, eventName, eventDate, capacity, location, description, waitlistId, entrantId, organizerId, imageUrl));
                }
                adapter.notifyDataSetChanged();
                Log.d("FetchEvents", "Event list updated: " + eventList.size());
            } else {
                Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
