package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends NavActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private Button signUpButton;
    private Button switchOrg;
    private CheckBox notifCheck;
    private FirebaseFirestore db;
    private String deviceId;
    private UserProfile user;
    Map<String, Object> documentData; // For storing the entire document's data
    String fieldValue = null;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.profile_page, findViewById(R.id.container));
        db = FirebaseFirestore.getInstance();

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("Profile Activity", "Successfully retrieved deviceId");
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                        // Store entire document as a map
                        documentData = document.getData();
                        Log.d("Firestore", "Document Data: " + documentData);
                        // Or store specific fields if needed
                        fieldValue = document.getString("deviceId");
                        name = document.getString("name");
                        Log.d("Firestore", name);
                        Log.d("Firestore", "Document ID: " + document.getId() + ", Field1 Value: " + fieldValue);
                    } else {
                        Log.d("Firestore", "No matching document found.");
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting document", e));


        // Temporarily disabling all fields
        editName = findViewById(R.id.edit_name);
        editName.setEnabled(false);

        editEmail = findViewById(R.id.edit_email);
        editEmail.setEnabled(false);

        editPhone = findViewById(R.id.edit_phone);
        editPhone.setEnabled(false);

        notifCheck = findViewById(R.id.checkBox);
        notifCheck.setEnabled(false);

        signUpButton = findViewById(R.id.save_btn);
        signUpButton.setEnabled(false);

        switchOrg = findViewById(R.id.switch_org);

        switchOrg.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrganizerViewActivity.class);
            intent.putExtra("organizerId", deviceId);
            startActivity(intent);
        });

        // Checks if all fields are filled, only then creates a new user on firebase
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user forms
                String name = editName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String phone = editPhone.getText().toString().trim();

                if (name.isEmpty()) {
                    editName.setError("Name is required!");
                    editName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editEmail.setError("Email is required!");
                    editEmail.requestFocus();
                    return;
                }

                if (phone.isEmpty()) {
                    editPhone.setError("Phone number is required");
                    editPhone.requestFocus();
                    return;
                }
                user = new UserProfile(getApplicationContext(), name, email, phone, true);
                registerUser(user);
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    /**
     * registerUser takes in a UserProfile object and adds it to Firebase
     * @param user UserProfile instance
     */
    private void registerUser(UserProfile user) {
        Map<String, Object> userData = new HashMap<>();
        Map<String, Object> device = new HashMap<>();
        userData.put("name", user.name);
        userData.put("email", user.email);
        userData.put("phone", user.phoneNumber);
        userData.put("isOrganizer", false);
        userData.put("isAdmin", false);
        userData.put("deviceId", user.getDeviceId());
        userData.put("enableNotifications", user.enableNotifications);
        // Website that creates Profile Pic Monograms using characters we give
        userData.put("profilePicture", "https://ui-avatars.com/api/?name="+user.name.charAt(0));
        db.collection("users")
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Registering UserProfile with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error registering UserProfile", e);
                });
        device.put("deviceId", user.getDeviceId());
        db.collection("devices").add(device);
    }

}