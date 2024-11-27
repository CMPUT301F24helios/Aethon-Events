package com.example.aethoneventsapp;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationController {

    private String deviceId;
    private boolean notificationPreference;
    private FirebaseFirestore db;


    public NotificationController(String deviceId) {
        this.deviceId = deviceId;
    }

    private List<String> pullAndDeleteAllNotifications(){
        List<String> allMessages = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                    notificationPreference = document.getBoolean("enableNotifications");

                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));


        if (notificationPreference){
            List<String> allEvents = new ArrayList<>();
            db.collection("users")
                    .whereEqualTo("deviceId", deviceId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                        if (document.contains("Events")){
                            List<String> _tmp = (List<String>) document.get("Events");
                            if (_tmp != null && !_tmp.isEmpty()){
                                allEvents.addAll(_tmp);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user profile", e));

            for (String eventId: allEvents){
                db.collection("events")
                        .document(eventId)
                        .collection("Notifications")
                        .document(deviceId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Handle the retrieved document data
                                List<String> _tmp_messages = (List<String>) documentSnapshot.get("messages");
                                if (_tmp_messages != null && !_tmp_messages.isEmpty()){
                                    allMessages.addAll(_tmp_messages);
                                }
                            } else {
                                Log.d("Firestore", "No document found for the given deviceId.");
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch the event", e));
            }
        }
        return allMessages;
    }

    private String getTitle(char firstChar){
        // TODO: add all values
        if (firstChar == 'W'){
            return "Waiting List";
        }
        return "Aethon-Events";
    }


    public void makeNotifications(){
        List<String> allMessages = pullAndDeleteAllNotifications();
        for (String message: allMessages){
            char title_code = message.charAt(0);
            String title = getTitle(title_code);
            String body = message.substring(2);

            // create a notification with message.

        }
    }



}