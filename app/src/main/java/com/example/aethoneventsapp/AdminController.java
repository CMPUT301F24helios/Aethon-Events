package com.example.aethoneventsapp;

import java.util.List;

/**
 * AdminController class responsible for admin related operations.
 */
public class AdminController {

    /**
     * Creates a new admin.
     * @param admin
     * @return
     */
    public Admin createAdmin(Admin admin) {
        // Business logic to create a new entrant
        System.out.println("Creating entrant: " + admin.getAdminName());
        return admin;
    }

    /**
     * Deletes an admin.
     * @param admin
     */
    public void processDeleteEvent(Admin admin, String eventId) {
        admin.deleteEvent(eventId);
        System.out.println("Deleting event with ID: " + eventId);
    }

    /**
     * Processes a browse event request.
     * @param admin
     */
    public void processBrowseEvents(Admin admin) {
        List<String> events = admin.getEvents();
        System.out.println("Browsing events:");
    }

    /**
     * Processes a delete image request.
     * @param admin
     * @param imageName
     */
    public void processDeleteImage(Admin admin, String imageName) {
//        TODO: delete image from database
    }

    /**
     * Processes a browse image request.
     * @param admin
     */
    public void processBrowseImages(Admin admin) {
//        TODO: get images from database
    }

    /**
     * Processes a delete profile request.
     * @param admin
     * @param profileName
     */
    public void processDeleteProfiles(Admin admin, String profileName) {
//        TODO: delete profile from database
    }

    /**
     * Processes a browse profile request.
     * @param admin
     */
    public void processBrowseProfiles(Admin admin) {
//        TODO: get profiles from database
    }

    /**
     * Processes a delete QR hash request.
     * @param admin
     * @param qrHash
     */
    public void processDeleteQRHash(Admin admin, String qrHash) {
//        TODO: delete qr hash from database
    }


    /**
     * Processes a delete facility request.
     * @param admin
     * @param facilityName
     */
    public void processDeleteFacility(Admin admin, String facilityName) {
//        TODO: delete facility from database
    }

}
