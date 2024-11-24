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

public class ProfileActivity extends AppCompatActivity {
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

    // Profile Data
    String name;
    String email;
    String phNo;
    String profilePic;
    Boolean userNotifSetting;
    Boolean isOrganizer;

//    private class ProfileData{
//        String name;
//        String email;
//        String phNo;
//        public ProfileData(String name, String email, String phNo) {
//            this.name = name
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        notifCheck = findViewById(R.id.checkBox);
        signUpButton = findViewById(R.id.save_btn);
        switchOrg = findViewById(R.id.switch_org);


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
                        email = document.getString("email");
                        phNo = document.getString("phone");
                        profilePic = document.getString("profilePicture");
                        userNotifSetting = document.getBoolean("enableNotifications");
                        isOrganizer = document.getBoolean("isOrganizer");

                        editName.setText(name);
                        editEmail.setText(email);
                        editPhone.setText(phNo);
                        notifCheck.setEnabled(userNotifSetting);
                        if (isOrganizer){
                            switchOrg.setText("Switch to Organizer Mode");
                        }


                        Log.d("FirestoreName", name);
                        Log.d("Firestore", "Document ID: " + document.getId() + ", Field1 Value: " + fieldValue);
                    } else {
                        Log.d("Firestore", "No matching document found.");
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting document", e));


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
                Boolean notifPref = notifCheck.isChecked();

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
                user = new UserProfile(getApplicationContext(), name, email, phone, notifPref);
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

        // update: instead of adding new data, we update the fields in the old doc.
        // TODO: optimise by not changing the (for sure)-unchanged fields.
        // TODO: updating profile picture.

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
                .whereEqualTo("deviceId", user.getDeviceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    db.collection("users").document(documentId)
                            .update(userData)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data updated successfully."))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating user data", e));
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error querying users", e));
    }

}