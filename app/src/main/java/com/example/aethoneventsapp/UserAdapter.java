package com.example.aethoneventsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        refreshUserList();  // Ensure that the list is populated with all users.
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    public void updateUserList(List<User> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.nameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
        holder.phoneTextView.setText(user.getPhone());

        // Load profile picture with Glide and check for null/empty URL
        String profilePictureUrl = user.getProfilePicture();
        Log.d("UserAdapter", "Profile Picture URL: " + profilePictureUrl);

        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Glide.with(context)
                    .load(profilePictureUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder) // Handle invalid URLs
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_profile_placeholder);
        }
        // Navigate to UserProfileActivity on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("user", user); // Pass the entire User object
            context.startActivity(intent);
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof android.app.Activity) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteUser(user, position))
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public View deleteButton;
        TextView nameTextView, emailTextView, phoneTextView;
        ImageView profileImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userName);
            emailTextView = itemView.findViewById(R.id.userEmail);
            phoneTextView = itemView.findViewById(R.id.userPhone);
            profileImageView = itemView.findViewById(R.id.profileImage);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
    private void deleteUser(User user, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // First delete the user
        db.collection("users")
                .whereEqualTo("deviceId", user.getDeviceId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot userDoc : querySnapshot) {
                            db.collection("users").document(userDoc.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("UserAdapter", "User deleted successfully: " + user.getDeviceId());
                                        // Then delete the device
                                        deleteDevice(db, user, position);
                                    })
                                    .addOnFailureListener(e -> Log.e("UserAdapter", "Error deleting user", e));
                        }
                    } else {
                        Log.e("UserAdapter", "No user found with deviceId: " + user.getDeviceId());
                    }
                })
                .addOnFailureListener(e -> Log.e("UserAdapter", "Error finding user", e));
    }
    private void deleteDevice(FirebaseFirestore db, User user, int position) {
        db.collection("devices")
                .whereEqualTo("deviceId", user.getDeviceId())
                .get()
                .addOnSuccessListener(deviceSnapshot -> {
                    if (!deviceSnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot deviceDoc : deviceSnapshot) {
                            db.collection("devices").document(deviceDoc.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("UserAdapter", "Device with ID " + user.getDeviceId() + " deleted");
                                    })
                                    .addOnFailureListener(e -> Log.e("UserAdapter", "Error deleting device", e));
                        }
                    } else {
                        Log.e("UserAdapter", "No device found with ID: " + user.getDeviceId());
                    }
                })
                .addOnFailureListener(e -> Log.e("UserAdapter", "Error finding device", e));
    }


    // Refresh the user list by querying Firestore
    private void refreshUserList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        notifyDataSetChanged();  // Notify the adapter that the data has changed
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserAdapter", "Error refreshing user list", e);
                });
    }
}
