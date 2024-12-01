package com.example.aethoneventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends NavActivity {

    private User user;
    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneTextView;
    private Button removeProfilePictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_user_profile, findViewById(R.id.container));

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        removeProfilePictureButton = findViewById(R.id.removeProfilePictureButton);

        // Get the passed user object
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            phoneTextView.setText(user.getPhone());

            String profilePictureUrl = user.getProfilePicture();
            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                Glide.with(this)
                        .load(profilePictureUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_placeholder);
            }

            // Handle Remove Profile Picture
            removeProfilePictureButton.setOnClickListener(v -> removeProfilePicture());
        }
    }

    private void removeProfilePicture() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String formattedName = user.getName().replace(" ", "+");
        String profilePic = "https://ui-avatars.com/api/?name=" + formattedName +"&background=3C0753&color=ffffff&size=512";
        db.collection("users")
                .whereEqualTo("deviceId", user.getDeviceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        db.collection("users")
                                .document(documentId)
                                .update("profilePicture", profilePic)
                                .addOnSuccessListener(aVoid -> {
                                    /*profileImageView.setImageResource(R.drawable.ic_profile_placeholder);
                                    user.setProfilePicture("");  // Update local user object*/
                                    Picasso.get()
                                            .load(profilePic)
                                            .placeholder(R.drawable.baseline_person_24) // Optional placeholder
                                            .into(profileImageView);


                                    Toast.makeText(UserProfileActivity.this,
                                            "Profile picture removed successfully.",
                                            Toast.LENGTH_SHORT).show();

                                    // Notify Firestore of the update
                                    db.collection("users").document(documentId).update("updated", true);
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast.makeText(UserProfileActivity.this,
                                            "Failed to remove profile picture.",
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(UserProfileActivity.this,
                                "No user found with the provided deviceId.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(UserProfileActivity.this,
                            "Error finding user document.",
                            Toast.LENGTH_SHORT).show();
                });
    }



}