package com.example.aethoneventsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private EditText editName, editEmail, editPhone;
    private Button signUpButton, switchOrg, changePhoto, removePhoto;
    private ImageView profileImageView;
    private CheckBox notifCheck;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String deviceId;
    private Uri imageUri;
    private UserProfile user;

    // Profile Data
    private String name, email, phNo, profilePic;
    private Boolean userNotifSetting, isOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Initialize views
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        notifCheck = findViewById(R.id.checkBox);
        signUpButton = findViewById(R.id.save_btn);
        switchOrg = findViewById(R.id.switch_org);
        changePhoto = findViewById(R.id.changePhoto);
        profileImageView = findViewById(R.id.profileImage);
        removePhoto = findViewById(R.id.removePhoto);


        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch user profile
        fetchUserProfile();

        // Button to switch to Organizer Mode
        switchOrg.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrganizerViewActivity.class);
            intent.putExtra("organizerId", deviceId);
            startActivity(intent);
        });

        // Button to change profile photo
        changePhoto.setOnClickListener(v -> openImagePicker());

        //Button to remove profile photo.
        removePhoto.setOnClickListener(v -> removeProfileImage());

        // Button to save user profile
        signUpButton.setOnClickListener(v -> saveUserProfile());
    }

    private void removeProfileImage(){
        //implementation of deterministically generated profile picture.
        String formattedName = name.replace(" ", "+");
        profilePic = "https://ui-avatars.com/api/?name=" + formattedName +"&background=3C0753&color=ffffff&size=512";
        Picasso.get()
                .load(profilePic)
                .placeholder(R.drawable.baseline_person_24) // Optional placeholder
                .into(profileImageView);

    }
    private void fetchUserProfile() {
        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                        // Populate fields with user data
                        name = document.getString("name");
                        email = document.getString("email");
                        phNo = document.getString("phone");
                        profilePic = document.getString("profilePicture");
                        userNotifSetting = document.getBoolean("enableNotifications");
                        isOrganizer = document.getBoolean("isOrganizer");

                        editName.setText(name);
                        editEmail.setText(email);
                        editPhone.setText(phNo);
                        notifCheck.setChecked(userNotifSetting != null && userNotifSetting);

                        if (profilePic != null && !profilePic.isEmpty()) {
                            Picasso.get()
                                    .load(profilePic)
                                    .placeholder(R.drawable.baseline_person_24) // Optional placeholder
                                    .into(profileImageView);
                        }

                        if (Boolean.TRUE.equals(isOrganizer)) {
                            switchOrg.setText("Switch to Organizer Mode");
                        }
                    } else {
                        Log.d("Firestore", "No matching user found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));
    }

    private void saveUserProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        boolean notifPref = notifCheck.isChecked();

        // Validate inputs
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
            editPhone.setError("Phone number is required!");
            editPhone.requestFocus();
            return;
        }

        user = new UserProfile(getApplicationContext(), name, email, phone, notifPref);

        if (imageUri != null) {
            uploadImage(deviceId, imageUri);
        } else {
            registerUser(user, profilePic); // Use existing profilePic if no new image is selected
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void uploadImage(String deviceId, Uri imageUri) {
        StorageReference storageRef = storage.getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + deviceId + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> registerUser(user, uri.toString()))
                        .addOnFailureListener(e -> Log.e("Firebase", "Failed to get download URL", e)))
                .addOnFailureListener(e -> Log.e("Firebase", "Image upload failed", e));
    }

    private void registerUser(UserProfile user, String profileImageUri) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.name);
        userData.put("email", user.email);
        userData.put("phone", user.phoneNumber);
        userData.put("deviceId", user.getDeviceId());
        userData.put("enableNotifications", user.enableNotifications);
        userData.put("profilePicture", profileImageUri);

        db.collection("users")
                .whereEqualTo("deviceId", user.getDeviceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    db.collection("users").document(documentId).update(userData)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data updated successfully."))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating user data", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error querying users", e));
    }
}
