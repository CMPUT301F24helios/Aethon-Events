package com.example.aethoneventsapp;

import android.util.Log;

import com.google.common.collect.Sets;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The WaitingList class represents a waitlist for an event, including details
 * about the waitlist entry ID, associated event ID, entrant ID, and status.
 */
public class WaitingList {
    private String waitlistId; // Unique identifier for the waitlist entry
    private String eventId;    // Identifier of the event associated with this waitlist
    private String status;     // Status of the entrant in the waitlist (e.g., "waiting", "selected")
    private ArrayList<String> waitList = new ArrayList<>(); // The WaitingList
    private ArrayList<String> selected = new ArrayList<>();
    private int maxWaitlistSize = 1000000;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // No-argument constructor
    public WaitingList() {
        this.waitList = new ArrayList<>();
        this.selected = new ArrayList<>();
    }

    // Constructor with waitlist max size
    public WaitingList(String waitlistId, String eventId, int maxWaitlistSize) {
        this.waitlistId = waitlistId;
        this.eventId = eventId;
        this.status = "waiting";
        this.maxWaitlistSize = maxWaitlistSize;
    }

    // Constructor without waitlist max size
    public WaitingList(String waitlistId, String eventId) {
        this.waitlistId = waitlistId;
        this.eventId = eventId;
        this.status = "waiting";
    }

    /**Entrant
     * Adds an entrant to the waitlist for a specific event.
     *
     * @param eventId   the ID of the event to join the waitlist for
     * @param entrantId the ID of the entrant joining the waitlist
     */
    public void addEntrantToWaitlist(String eventId, String entrantId) {
        // adding entrant to the waiting list
        Log.d("Firestore", "maxWaitlistSize: " + maxWaitlistSize + " waitList.size(): " + waitList.size());
        if (waitList.size() < maxWaitlistSize) {
            waitList.add(entrantId);
            Log.d("Firestore", "Entrant " + entrantId + " added to waitlist for event " + eventId);
            System.out.println("Entrant " + entrantId + " added to waitlist for event " + eventId);
        }
        else {
            System.out.println("Waitlist is full for event " + eventId);
        }
        updateFirestore();

    }

    /**
     * Removes an entrant from the waitlist based on the waitlist ID.
     *
     * @param entrantId the entrant to be removed
     */
    public void removeEntrantFromWaitlist(String entrantId) {
        waitList.remove(entrantId);
        System.out.println("Removed entrant with entrant ID: " + entrantId);
        updateFirestore();
    }

    private void updateFirestore() {
        db.collection("Events").document(eventId)
                .collection("WaitingList").document(waitlistId)
                .update("waitList", waitList)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Waitlist updated successfully!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update waitlist", e));

    }

    /**
            * Sets up a real-time listener to update the waitlist when new entrants are added to Firestore.
     */
    public void addToWaitlist() {
        db.collection("Events").document(eventId)
                .collection("WaitingList")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    waitList.add(dc.getDocument().getId());
                                    Log.d("Firestore", "New entrant: " + dc.getDocument().getId());
                                    break;
                                case REMOVED:
                                    waitList.remove(dc.getDocument().getId());
                                    Log.d("Firestore", "Removed entrant: " + dc.getDocument().getId());
                                    break;
                                default:
                                    break;
                            }
                        }
                        Log.d("Firestore", "Current waitlist: " + waitList);
                    }
                });
    }


    /**
     * Manages selection of entrants from the waitlist for a specific event.
     *
     * @param eventId the ID of the event for which entrants will be selected from the waitlist
     */
    public ArrayList<String> manageEntrantSelection(String eventId,int vacancies) {
//        Random random = new Random();
//        ArrayList<String> selected = new ArrayList<>();
//        Set<Integer> usedIndices = new HashSet<>();
//        System.out.println("Selecting entrants for event: " + eventId);
////        for (int i = 0; i< vacancies;i++ ){
////            int min = 0;
////            int max = waitList.size() - 1;
////            int randomNumber = 0;
////
////            // Generate a unique random number not yet used
////            do {
////                randomNumber = random.nextInt((max - min) + 1) + min;
////            } while (usedIndices.contains(randomNumber));
////
////            // Add the unique number to usedIndices and to the selected list
//////            usedIndices.add(randomNumber);
//////            selected.add(waitList.get(randomNumber));
////        }
//        selected.add("1");
//
//
//        this.setStatus("selected");
//        return selected ;

        ArrayList<String> selected = new ArrayList<>();
        Log.d("Waitlist", "Selecting entrants for event: " + eventId);
        Log.d("Waitlist", "Waitlist Size: " + waitList.size());
        if (waitList.size() <= vacancies) {
            selected.addAll(waitList);
        } else {
            ArrayList<String> tempList = new ArrayList<>(waitList);
            Collections.shuffle(tempList);
            for (int i = 0; i < vacancies; i++) {
                selected.add(tempList.get(i));
            }
        }
        this.selected = selected;
        this.setStatus("selected");
        return selected;
    }

    // Getters and Setters for each attribute

    /**
     * Gets the waitlist ID.
     *
     * @return the waitlist ID
     */
    public String getWaitlistId() {
        return waitlistId;
    }

    /**
     * Sets the waitlist ID.
     *
     * @param waitlistId the waitlist ID to set
     */
    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    /**
     * Gets the event ID.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }



    /**
     * Gets the status of the entrant.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the entrant.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the list of entrants on the waitlist.
     *
     * @return the waitlist
     */
    public ArrayList<String> getWaitList() {
        return waitList;
    }

    /**
     * Sets the list of entrants on the waitlist.
     */
    public void setWaitList(ArrayList<String> waitList) {
        this.waitList = waitList;
    }
    /**
     * Gets the list of entrants on the selected list.
     *
     * @return the selected
     */
        public ArrayList<String> getSelected() {
        return selected;
    }

    /**
     * Gets the max waitlist size.
     *
     * @return the maxWaitlistSize
     */
    public int getMaxWaitlistSize() {
        return maxWaitlistSize;
    }

}
