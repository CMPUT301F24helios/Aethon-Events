package com.example.aethoneventsapp;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

/**
 * The Event class represents an event with various attributes such as name, location,
 * and capacity. It provides functionality for event creation, updates, waitlist management,
 * QR code generation, and entrant handling.
 */
public class Event {

    // Attributes
    private int eventId;
    private String name;
    private String location;
    private int capacity;
    private String description;
    private String waitlistId;
    private String entrantId;
    private String organizerId;
    private String url;
    private String qrCodeBase64;
    private String eventDate;


    // Constructor
    public Event(int eventId, String name, String location, String organizerId, String eventDate) {
        this.eventId = eventId;
        this.name = name;
        this.location = location;
        this.description = description;
        this.organizerId = organizerId;
        this.eventDate = eventDate;
        createUrl();
    }
    public void createUrl(){
        // create a custom url for the event
        this.url = "event/" + this.eventId;
    }
    public String getUrl() {
        return url;
    }
    public String getEventDate() { // Add this method
        return eventDate;
    }
    /**
     * Creates an event and assigns it to the specified organizer.
     *
     * @param data         Data to create the event.
     * @param organizerId  The ID of the organizer creating the event.
     */
    public void createEvent(String data, int organizerId) {
        // Store the event in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        String eventIdString = String.valueOf(this.eventId);
        databaseReference.child(eventIdString).setValue(this)
                .addOnSuccessListener(aVoid -> {
                    // Event successfully created in Firebase
                   //generateQRcode(eventIdString);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }


    /**
     * Updates the event details if the specified organizer is the organizer of the event.
     *
     * @param organizerId  The ID of the organizer attempting to update the event.
     */
    public void updateEvent(int organizerId) {
        // Check if organizerId matches and update event
    }

    /**
     * Assigns a waitlist to the event.
     *
     * @param waitlistId The ID of the waitlist to assign.
     */
    public void assignWaitlist(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    /**
     * Generates a QR code for the event.
     */
//    private void generateQRcode(String eventId) {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        try {
//            // Generate QR code as Bitmap
//            Bitmap bitmap = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, 200, 200);
//            // Convert Bitmap to Base64 String
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//            this.qrCodeBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            // You can now store this Base64 string in the Firebase database if needed
//        } catch (WriterException e) {
//            // Handle exception
//        }
//    }
    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    /**
     * Assigns an organizer to the event. Called from createEvent().
     *
     * @param eventId The ID of the event.
     */
    private void assignOrganizerToEvent(int eventId) {
        // Assign the organizer to the event based on eventId
    }

    /**
     * Assigns a QR code to the event. Called from createEvent().
     *
     * @param eventId The ID of the event.
     */
    private void assignQRcode(int eventId) {
        // Assign QR code to event based on eventId
    }

    /**
     * Retrieves event information for a specific entrant, checking eligibility.
     *
     * @param eventId    The ID of the event.
     * @param entrantId  The ID of the entrant requesting information.
     * @return           Event data if eligible, null otherwise.
     */
    public String getInfo(int eventId, int entrantId) {
        // Check eligibility and return event information
        return "Event Info"; // Replace with actual information
    }

    /**
     * Sets information for the specified event.
     *
     * @param eventId The ID of the event to update.
     * @param data    The data to set for the event.
     */
    public void setInfo(int eventId, String data) {
        // Set information for the event based on eventId
    }

    /**
     * Retrieves a list of entrants selected in the waitlist.
     *
     * @param waitlistId The ID of the waitlist.
     * @return           List of entrants in the waitlist.
     */
    public List<Integer> getEntrantsInWaitlistEvent(int waitlistId) {
        // Retrieve and return entrants in the specified waitlist
        return null; // Replace with actual list of entrants
    }

    /**
     * Deletes the specified event.
     *
     * @param eventId The ID of the event to delete.
     */
    public void deleteEvent(int eventId) {
        // Implementation for deleting the event
    }

    /**
     * Sets a report flag on the event if it breaks any violations.
     *
     * @param eventId The ID of the event to flag.
     */
    public void reportSetter(int eventId) {
        // Set report flag on event based on eventId
    }

    /**
     * Adds an entrant to the specified waitlist.
     *
     * @param entrantId  The ID of the entrant to add.
     * @param waitlistId The ID of the waitlist to add the entrant to.
     */
    public void addEntrantToWaitlist(int entrantId, int waitlistId) {
        // Add entrant to waitlist
    }

    // Getters and Setters for attributes
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

