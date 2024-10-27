package com.example.aethoneventsapp;
/**
 * The GeolocationService class is responsible for obtaining the device's
 * location and verifying locations against the entrants' locations.
 */
public class GeolocationService {
    /**
     * Retrieves the current device location.
     *
     * @return an array containing the latitude and longitude of the device's location
     */
    public double[] getDeviceLocation() {
        //TODO: Add the code to  Simulate getting device location
        return new double[]{37.7749, -122.4194}; // Random
    }

    /**
     * Verifies if the device's location matches the entrant's location within a
     * specified tolerance.
     *
     * @param deviceLocation the latitude and longitude of the device's location
     * @param entrantLocation the latitude and longitude of the entrant's location
     * @return true if the device location matches the entrant's location,
     *         false otherwise
     */
    public boolean verifyLocation(double[] deviceLocation, double[] entrantLocation) {
        // TODO: Add the Logic to verify if the device location matches the entrant's location
        // Here we can include some tolerance level for verification
        double tolerance = 0.01; // Example tolerance for location matching
        return Math.abs(deviceLocation[0] - entrantLocation[0]) < tolerance &&
                Math.abs(deviceLocation[1] - entrantLocation[1]) < tolerance;
    }
}