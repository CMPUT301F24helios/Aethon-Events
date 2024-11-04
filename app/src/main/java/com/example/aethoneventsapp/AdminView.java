package com.example.aethoneventsapp;

/**
 * AdminView class represents the view for the admin.
 */
public class AdminView {

    /**
     * Display admin details
     * @param admin
     */
    public void displayAdminDetails(Admin admin) {
        System.out.println("Admin Details:");
        System.out.println("ID: " + admin.getAdminId());
        System.out.println("Name: " + admin.getAdminName());
        System.out.println("Email: " + admin.adminEmail());
        System.out.println("Device ID: " + admin.getDeviceId());
    }

    /**
     * Display admin managed events
     * @param admin
     */
    public void displayAdminEvents(Admin admin) {
        System.out.println("Admin Managed Events:");
        for (String eventId : admin.getEvents()) {
            System.out.println("Event ID: " + eventId);
        }
    }

    /**
     * Show delete event confirmation
     * @param eventId
     */
    public void showDeleteEventConfirmation(String eventId) {
        System.out.println("Are you sure you want to delete event with ID: " + eventId + "?");
    }
}
