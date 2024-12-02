package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminFacilityActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFacilities;
    public List<Facility> facilityList = new ArrayList<>();
    private FacilityAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facilities_view);

        // Initialize views
        recyclerViewFacilities = findViewById(R.id.recyclerViewFacilities);
        searchBar = findViewById(R.id.searchBar);

        // Set up RecyclerView
        recyclerViewFacilities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FacilityAdapter(this, facilityList);
        recyclerViewFacilities.setAdapter(adapter);

        // Add a listener to the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                filterFacilities(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Fetch facilities from Firestore
        fetchFacilityFromFirestore();
        // Initialize Back Button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminFacilityActivity.this, AdminMainActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish current activity to prevent going back to it
        });
    }

    private void filterFacilities(String query) {
        List<Facility> filteredList = new ArrayList<>();
        for (Facility facility : facilityList) {
            if (facility.getName().toLowerCase().contains(query) ||
                    facility.getLocation().toLowerCase().contains(query)) {
                filteredList.add(facility);
            }
        }
        adapter.setFacilityList(filteredList); // Update the adapter with filtered data
    }

    private void fetchFacilityFromFirestore() {
        db.collection("facilities")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminFacilityActivity", "Error fetching facilities", error);
                        return;
                    }

                    if (value != null) {
                        facilityList.clear(); // Clear the list before adding updated data
                        for (QueryDocumentSnapshot document : value) {
                            Facility facility = document.toObject(Facility.class);
                            facilityList.add(facility); // Add each facility to the list
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter that the data has changed
                    }
                });
    }
}
