package com.example.aethoneventsapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    Button scan_btn;
    Button signup_btn;
    Button dash_btn;
    ImageView profile_img;
    String deviceId;
    private FirebaseFirestore db;
    boolean notificationPreference;

    // Request notification permission from user using this
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if (o) {
                Toast.makeText(MainActivity.this, "Post notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Post notification permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    });



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
        setContentView(R.layout.activity_main);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        db = FirebaseFirestore.getInstance();

        // call for notifications function.
        handleNotifications();

        scan_btn = findViewById(R.id.scanner);
        scan_btn.setOnClickListener(v -> {
            // Start QR code scanner activity
            Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
            startActivity(intent);
        });

        profile_img = findViewById(R.id.profile_image);
        profile_img.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void handleNotifications(){
        Log.d("TestingNoti", "Inside handle notifications");
        //NotificationController notificationController = new NotificationController(deviceId);

        //List<String> allMessages = notificationController.pullAndDeleteAllNotifications();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                String description = "Example Notification";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("test", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);


            }
        }

        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                    notificationPreference = document.getBoolean("enableNotifications");
                    Log.d("TestingNoti1", String.valueOf(notificationPreference));
                    if (notificationPreference){
                        List<String> allEvents = new ArrayList<>();
                        List<String> allMessages = new ArrayList<>();
                        AtomicInteger _id = new AtomicInteger();
                        db.collection("users")
                                .whereEqualTo("deviceId", deviceId)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(querySnapshot1 -> {
                                    QueryDocumentSnapshot document2 = (QueryDocumentSnapshot) querySnapshot1.getDocuments().get(0);

                                    if (document2.contains("Events")){
                                        Log.d("TestingNoti2", "contains events");
                                        List<String> _tmp = (List<String>) document2.get("Events");
                                        if (_tmp != null && !_tmp.isEmpty()){
                                            Log.d("TestingNoti3", "event list is not null");
                                            allEvents.addAll(_tmp);
                                            Log.d("TestingNoti4", "tmp len = " + _tmp.size() + " allEvents len = " + allEvents.size());
                                            Log.d("TestingNoti5", "allEvents len = " + allEvents.size());

                                            for (String eventId: allEvents){
                                                Log.d("TestingNoti6", deviceId);

                                                db.collection("Events")
                                                        .document(eventId)
                                                        .collection("Notifications")
                                                        .document(deviceId)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            if (documentSnapshot.exists()) {
                                                                // Handle the retrieved document data
                                                                List<String> _tmp_messages = (List<String>) documentSnapshot.get("messages");

                                                                if (_tmp_messages != null && !_tmp_messages.isEmpty()){
                                                                    //allMessages.addAll(_tmp_messages);

                                                                    for (String message: _tmp_messages){
                                                                        Log.e("TestingNoti7", message);
                                                                        char title_code = message.charAt(0);
                                                                        String title = getTitle(title_code);
                                                                        String body = message.substring(2);

                                                                        // create a notification with message.
                                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "test")
                                                                                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                                                                                .setContentTitle(title)
                                                                                .setContentText(body)
                                                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                                        //notificationManager.notify();
                                                                        int notId = _id.addAndGet(1);
                                                                        notificationManager.notify(notId, builder.build());


                                                                    }
                                                                    _tmp_messages.clear();
                                                                    documentSnapshot.getReference().update("messages", _tmp_messages)
                                                                            .addOnSuccessListener(aVoid -> {
                                                                                Log.d("Firestore", "Messages updated successfully");
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                Log.e("Firestore", "Failed to update messages", e);
                                                                            });
                                                                }
                                                            } else {
                                                                Log.d("Firestore", "No document found for the given deviceId.");
                                                            }
                                                        })

                                                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch the event", e));
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));


                    }

                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));
    }

    public String getTitle(char firstChar){
        // TODO: add all values
        if (firstChar == 'W'){
            return "Waiting List";
        }
        return "Aethon-Events";
    }
}