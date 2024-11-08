package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private Button signUpButton;
    private FirebaseFirestore db;
    private String deviceId;
    private UserProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup);

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        signUpButton = findViewById(R.id.signup_btn);

        db = FirebaseFirestore.getInstance();

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
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
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
