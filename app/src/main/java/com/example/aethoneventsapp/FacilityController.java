package com.example.aethoneventsapp;

/**
 * The FacilityController class manages facility operations such as creation, update, deletion, and violation checks.
 */
public class FacilityController {

    /**
     * Creates a new Facility object and outputs a creation confirmation.
     *
     * @param facilityId  the ID of the facility
     * @param name        the name of the facility
     * @param location    the location of the facility
     * @param capacity    the maximum capacity of the facility
     * @param description a brief description of the facility
     * @param organizer   the organizer responsible for the facility
     * @return the created Facility object
     */
    public Facility createNewFacility(String facilityId, String name, String location, int capacity, String description, String organizer) {
        Facility facility = new Facility(facilityId, name, location, capacity, description, organizer);
        facility.createFacility();
        return facility;
    }

    /**
     * Updates the details of an existing facility.
     *
     * @param facility    the facility object to update
     * @param name        the updated name of the facility
     * @param location    the updated location of the facility
     * @param capacity    the updated capacity of the facility
     * @param description the updated description of the facility
     * @param organizer   the updated organizer of the facility
     */
    public void updateExistingFacility(Facility facility, String name, String location, int capacity, String description, String organizer) {
        facility.updateFacility(name, location, capacity, description, organizer);
    }


    /**
     * Checks if the facility violates any rules (e.g., exceeding capacity).
     *
     * @param facility the facility to check
     * @return true if there is a violation, false otherwise
     */
    public boolean checkFacilityForViolation(Facility facility) {
        // TODO: add the condition for violation
        return false;
    }
}