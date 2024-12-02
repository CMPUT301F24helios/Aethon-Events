package com.example.aethoneventsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * SignUp Activity manages registration of users
 * It gets values from fields, then adds their info to Firebase
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private Button signUpButton;
    private FirebaseFirestore db;
    private String deviceId;
    private UserProfile user;
    private CircleImageView profilePic;
    private FirebaseStorage storage;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_address);
        editPhone = findViewById(R.id.edit_phone);
        signUpButton = findViewById(R.id.signup_btn);
        profilePic = findViewById(R.id.profileImage);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        // Checks if all fields are filled, only then updates the user on firebase
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Log.d("TestingSignup1", "1");
                if (imageUri != null){
                    uploadImage(deviceId, imageUri);
                } else {
                    registerUser(user, "");
                }
                Log.d("TestingSignup2", "2");
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        Log.d("TestingSignup5", "5");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
        }
    }

    private void uploadImage(String deviceId, Uri imageUri) {
        StorageReference storageRef = storage.getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + deviceId + ".jpg");

        Log.d("Firestore-10", "Image URI: " + imageUri);
        try {
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("ImageUpload", "Image uploaded successfully");
                        profileImageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("ImageUpload", "Download URL: " + uri);
                                    registerUser(user, uri.toString());
                                })
                                .addOnFailureListener(e -> Log.e("ImageDownload", "Failed to get download URL", e));
                    })
                    .addOnFailureListener(e -> Log.e("ImageUpload", "Image upload failed", e));
        } catch (Exception e) {
            Log.e("Firestore", "Exception during upload", e);
        }
    }

    /**
     * registerUser takes in a UserProfile object and adds it to Firebase
     *
     * @param user            UserProfile instance
     * @param profileImageStr ProfilePic as String.
     */
    private void registerUser(UserProfile user, String profileImageStr) {
        Log.d("Firestore33", "Here33");
        Map<String, Object> userData = new HashMap<>();
        Map<String, Object> device = new HashMap<>();
        userData.put("name", user.name);
        userData.put("email", user.email);
        userData.put("phone", user.phoneNumber);
        userData.put("isOrganizer", false);
        userData.put("isAdmin", false);
        userData.put("deviceId", user.getDeviceId());
        userData.put("enableNotifications", user.enableNotifications);

        Log.d("Firestore11", "Here11");
        if (profileImageStr != null && !profileImageStr.isEmpty()){
            userData.put("profilePicture", profileImageStr);
        } else {
            String formattedName = (user.name).replace(" ", "+");
            // Website that creates Profile Pic Monograms using characters we give
            userData.put("profilePicture", "https://ui-avatars.com/api/?name=" + formattedName +"&background=3C0753&color=ffffff&size=512");
        }
        device.put("deviceId", user.getDeviceId());

        Log.d("Firestore22", "Here22");
        db.collection("devices").add(device);

        db.collection("users")
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Registering UserProfile with ID: " + documentReference.getId());
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error registering UserProfile", e);
                });

        Log.d("TestingSignup4", "4");
    }


}
