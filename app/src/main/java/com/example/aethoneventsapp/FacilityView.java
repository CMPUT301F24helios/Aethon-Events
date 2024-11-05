package com.example.aethoneventsapp;
/**
 * The FacilityView class handles the presentation of facility information to the user.
 */
public class FacilityView {

    /**
     * Displays detailed information about a facility.
     *
     * @param facility the facility whose details are displayed
     */
    public void displayFacilityDetails(Facility facility) {
        System.out.println("Facility Details:");
        System.out.println("ID: " + facility.getFacilityId());
        System.out.println("Name: " + facility.getName());
        System.out.println("Location: " + facility.getLocation());
        System.out.println("Capacity: " + facility.getCapacity());
        System.out.println("Description: " + facility.getDescription());
        System.out.println("Organizer: " + facility.getOrganizer());
    }

    /**
     * Shows the form for creating a new facility.
     */
    public void showCreateFacilityForm() {
        // TODO
    }

    /**
     * Shows the form for updating an existing facility.
     *
     * @param facility the facility to update
     */
    public void showUpdateFacilityForm(Facility facility) {
        // TODO
        System.out.println("Displaying form to update facility: " + facility.getName());
    }

    /**
     * Shows a delete confirmation for a specific facility.
     *
     * @param facility the facility to delete
     */
    public void showDeleteConfirmation(Facility facility) {
        // TODO
        System.out.println("Are you sure you want to delete facility: " + facility.getName() + "?");
    }

    /**
     * Shows a violation warning message for a specific facility.
     *
     * @param facility the facility with the violation
     */
    public void showViolationWarning(Facility facility) {
        //TODO
        System.out.println("Warning: Facility " + facility.getName() + " has a violation.");
    }
}