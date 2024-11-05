package com.example.aethoneventsapp;

/**
 * EntrantView class represents the view for the Entrant.
 */
public class EntrantView {

    /**
     * Displays the profile details of the entrant.
     * @param entrant
     */
    public void displayProfileDetails(Entrant entrant) {
        System.out.println("Profile Details:");
        System.out.println("ID: " + entrant.getEntrantId());
        System.out.println("Name: " + entrant.getName());
        System.out.println("Email: " + entrant.getEmail());
        System.out.println("Phone Number: " + entrant.getPhoneNumber());
        System.out.println("Postal Code: " + entrant.getPostalCode());
    }

    /**
     * Shows the update profile form.
     */
    public void showUpdateProfileForm() {
        System.out.println("Update Profile Form:");
        System.out.println("Enter new details to update your profile.");
    }

    /**
     * Displays the waitlisted events for the entrant.
     * @param entrant
     */
    public void displayWaitlistedEvents(Entrant entrant) {
        System.out.println("Waitlisted Events:");
        for (String eventId : entrant.getWaitlistedEvents()) {
            System.out.println("Event ID: " + eventId);
        }
    }

    /**
     * Displays the event invitations for the entrant.
     * @param entrant
     */
    public void showEventInvitations(Entrant entrant) {
        System.out.println("Event Invitations:");
        for (String eventId : entrant.getPendingInvitations()) {
            System.out.println("Event ID: " + eventId);
        }
    }

    /**
     * Displays the registered events for the entrant.
     * @param entrant
     */
    public void displayRegisteredEvents(Entrant entrant) {
        System.out.println("Registered Events:");
        for (String eventId : entrant.getRegisteredEvents()) {
            System.out.println("Event ID: " + eventId);
        }
    }

    /**
     * Displays the QR code scanner.
     */
    public void showQRCodeScanner() {
        System.out.println("QR Code Scanner is now active. Please scan the QR code.");
    }

    /**
     * Displays the event details for a given event ID.
     * @param eventId
     */
    public void displayEventDetails(String eventId) {
        // This is a placeholder. In a real application, you would fetch event details from a database or API.
        System.out.println("Displaying details for event ID: " + eventId);
        System.out.println("Event Name: Sample Event");
        System.out.println("Event Date: 2023-10-01");
        System.out.println("Event Location: Sample Location");
    }
}
