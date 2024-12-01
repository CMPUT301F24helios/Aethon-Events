package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        userList = new ArrayList<>();
        adapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                filterUsers(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fetchUsersFromFirestore();

        // Initialize Back Button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminUserActivity.this, AdminMainActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish current activity to prevent going back to it
        });
    }

    private void filterUsers(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query) ||
                    user.getEmail().toLowerCase().contains(query) ||
                    user.getPhone().toLowerCase().contains(query)) {
                filteredList.add(user);
            }
        }

        if (filteredList.isEmpty()) {
            // Show "No results found" message (optional: use a Toast)
            Log.d("AdminActivity", "No results found for query: " + query);
        }

        adapter.updateUserList(filteredList);  // Update adapter with filtered data
    }

    private void fetchUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")  // Listen to changes in the "users" collection
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminActivity", "Error fetching users", error);
                        return;
                    }
                    if (value != null) {
                        userList.clear();  // Clear the list before adding updated data
                        for (QueryDocumentSnapshot document : value) {
                            User user = document.toObject(User.class);
                            userList.add(user);  // Add each user to the list
                        }
                        adapter.notifyDataSetChanged();  // Notify adapter that the data has changed
                    }
                });
    }

    private void addDummyUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        for (int i = 1; i <= 2; i++) { // Add 50 dummy users
            Map<String, Object> user = new HashMap<>();
            user.put("name", "Dummy User " + i);
            user.put("email", "dummyuser" + i + "@example.com");
            user.put("deviceId", "device" + i);
            user.put("age", 20 + (i % 10)); // Example additional field

            usersRef.document("device" + i) // Use deviceId as the document ID
                    .set(user)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Dummy user added"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding dummy user", e));
        }
    }

    private void removeDummyUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        // Loop through the dummy user IDs and delete them
        for (int i = 1; i <= 50; i++) { // Match the range of dummy user IDs
            String documentId = "device" + i; // Construct the document ID

            usersRef.document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Dummy user " + documentId + " deleted"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error deleting dummy user " + documentId, e));
        }
    }


}

