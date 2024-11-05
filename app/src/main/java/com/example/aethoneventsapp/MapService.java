package com.example.aethoneventsapp;

/**
 * The MapService class provides methods to manage map-related operations,
 * such as adding markers for entrants and displaying the map.
 */
public class MapService {
    /**
     * Adds a marker on the map at the specified location for the given entrant.
     *
     * @param location an array containing the latitude and longitude of the marker
     * @param name the name of the entrant for whom the marker is added
     */
    public void addMarker(double[] location, String name) {
        // TODO: Add the Logic to add a marker on the map for the entrant
        System.out.println("Adding marker for " + name + " at location: " + location[0] + ", " + location[1]);
    }

    /**
     * Displays the map.
     */
    public void displayMap() {
        // TODO: Add the Logic to display the map
        System.out.println("Displaying the map.");
    }
}