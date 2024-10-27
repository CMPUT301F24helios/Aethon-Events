package com.example.aethoneventsapp;

/**
 * The Facility class represents a facility with attributes such as ID, name, location, capacity, description, and organizer.
 */
public class Facility {
    private String facilityId; // Facility ID
    private String name; // Facility name
    private String location; // Facility location
    private int capacity; // Capacity of the facility
    private String description; // Description of the facility
    private String organizer; // Organizer of the facility

    /**
     * Constructs a new Facility with the specified details.
     *
     * @param facilityId  the unique ID of the facility
     * @param name        the name of the facility
     * @param location    the location of the facility
     * @param capacity    the maximum capacity of the facility
     * @param description a brief description of the facility
     * @param organizer   the organizer responsible for the facility
     */
    public Facility(String facilityId, String name, String location, int capacity, String description, String organizer) {
        this.facilityId = facilityId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.organizer = organizer;
    }

    /**
     * Gets the facility ID.
     *
     * @return the facility ID
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the facility ID.
     *
     * @param facilityId the facility ID
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Gets the facility name.
     *
     * @return the facility name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the facility name.
     *
     * @param name the facility name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the facility location.
     *
     * @return the facility location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the facility location.
     *
     * @param location the facility location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the capacity of the facility.
     *
     * @return the facility capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity of the facility.
     *
     * @param capacity the facility capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the facility description.
     *
     * @return the facility description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the facility description.
     *
     * @param description the facility description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the facility organizer.
     *
     * @return the facility organizer
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * Sets the facility organizer.
     *
     * @param organizer the facility organizer
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * Creates a new facility and outputs a confirmation message.
     */
    public void createFacility() {
        System.out.println("Facility created: " + name);
    }

    /**
     * Updates the facility with new details and outputs a confirmation message.
     *
     * @param name        the updated name of the facility
     * @param location    the updated location of the facility
     * @param capacity    the updated capacity of the facility
     * @param description the updated description of the facility
     * @param organizer   the updated organizer of the facility
     */
    public void updateFacility(String name, String location, int capacity, String description, String organizer) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.organizer = organizer;
        System.out.println("Facility updated: " + name);
    }
}