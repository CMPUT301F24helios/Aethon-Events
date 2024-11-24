package com.example.aethoneventsapp;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;

public class ExpandableListViewActivity extends AppCompatActivity {

    private List<String> categories;
    private Map<String, List<String>> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize data
        categories = new ArrayList<>();
        categories.add("Accepted");
        categories.add("Declined");
        categories.add("Pending");
        categories.add("Waitlist");

        participants = new HashMap<>();
        participants.put("Accepted", List.of("Person A", "Person B"));
        participants.put("Declined", List.of("Person C"));
        participants.put("Pending", List.of("Person D", "Person E"));
        participants.put("Waitlist", List.of(
                "Alexander Theodosius",
                "Catherine Volkov",
                "Napoleon Bonaparte"
        ));

        // Find ExpandableListView
        ExpandableListView expandableListView = findViewById(R.id.entrantsExpandableList);

        // Set adapter
        EntrantsExpandableListAdapter adapter = new EntrantsExpandableListAdapter(this, categories, participants);
        expandableListView.setAdapter(adapter);

        // Find Pool button
        Button poolButton = findViewById(R.id.poolButton);
        poolButton.setOnClickListener(v -> {
            List<String> waitlist = participants.get("Waitlist");
            if (waitlist != null && !waitlist.isEmpty()) {
                Random random = new Random();
                String randomParticipant = waitlist.get(random.nextInt(waitlist.size()));
                Toast.makeText(ExpandableListViewActivity.this, "Selected: " + randomParticipant, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ExpandableListViewActivity.this, "Waitlist is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}