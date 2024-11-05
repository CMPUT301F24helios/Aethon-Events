package com.example.aethoneventsapp;

/**
 * EntrantController class responsible for managing entrant-related operations.
 */
public class EntrantController {

    /**
     * Create a new entrant
     * @param entrant
     * @return
     */
    public Entrant createEntrant(Entrant entrant) {
        // Business logic to create a new entrant
        System.out.println("Creating entrant: " + entrant.getName());
        return entrant;
    }

    /**
     * Update an existing entrant's profile
     * @param entrant
     * @param name
     * @param email
     * @param phone
     * @param posalCode
     */
    public void updateEntrantProfile(Entrant entrant, String name, String email, String phone, String posalCode) {
        entrant.updateEntrant(name, email, phone, posalCode);
    }

    /**
     * Process a waitlist join event
     * @param entrant
     * @param event
     */
    public void processWaitlistJoin(Entrant entrant, Event event){
        entrant.joinWaitlist(event.getEventId());
    }

    /**
     * Process a waitlist leave event
     * @param entrant
     * @param event
     */
    public void processWaitlistLeave(Entrant entrant, Event event){
        entrant.leaveWaitlist(event.getEventId());
    }

    /**
     * Handle an invitation accept event
     * @param entrant
     * @param event
     */
    public void handleInvitationAccept(Entrant entrant, Event event) {
        entrant.acceptInvitation(event.getEventId());
    }

    /**
     * Handle an invitation decline event
     * @param entrant
     * @param event
     */
    public void handleInvitationDecline(Entrant entrant, Event event) {
        entrant.declineInvitation(event.getEventId());
    }

    /**
     * Scan QR code
     * @param entrant
     * @param qrCode
     */
    public void scanQRCode(Entrant entrant, String qrCode) {
        // Business logic to scan a QR code
        System.out.println("Scanning QR code: " + qrCode);
    }
}
