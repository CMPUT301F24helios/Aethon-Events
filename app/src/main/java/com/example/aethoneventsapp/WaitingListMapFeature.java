//package com.example.aethoneventsapp;
//
//import java.util.List;
//
///**
// * The WaitingListMapFeature class is responsible for displaying a map with
// * locations of entrants on the waiting list. It retrieves entrant location
// * data and optionally verifies the entrant's location using device geolocation.
// */
//public class WaitingListMapFeature {
//    private final MapService mapService;
//    private final GeolocationService geolocationService;
//
//    /**
//     * Constructs a WaitingListMapFeature with the specified MapService and
//     * GeolocationService.
//     *
//     * @param mapService the service responsible for map operations
//     * @param geolocationService the service responsible for geolocation operations
//     */
//    public WaitingListMapFeature(MapService mapService, GeolocationService geolocationService) {
//        this.mapService = mapService;
//        this.geolocationService = geolocationService;
//    }
//
//    /**
//     * Displays entrants on the map based on their location.
//     *
//     * @param entrants a list of entrants to be displayed on the map
//     */
//    public void displayEntrantsOnMap(List<Entrant> entrants) {
//        for (Entrant entrant : entrants) {
//            double[] location = entrant.getLocation();
//            mapService.addMarker(location, entrant.getName());
//        }
//    }
//
//    /**
//     * Updates the map with new entrants by adding markers for their locations.
//     *
//     * @param newEntrants a list of new entrants to be added to the map
//     */
//    public void updateMapWithNewEntrants(List<Entrant> newEntrants) {
//        for (Entrant entrant : newEntrants) {
//            double[] location = entrant.getLocation();
//            mapService.addMarker(location, entrant.getName());
//        }
//    }
//
//    /**
//     * Verifies the location of an entrant using the device's geolocation.
//     *
//     * @param entrant the entrant whose location is to be verified
//     * @return true if the entrant's location matches the device's location,
//     *         false otherwise
//     */
//    public boolean verifyEntrantLocation(Entrant entrant) {
//        double[] deviceLocation = geolocationService.getDeviceLocation();
//        return geolocationService.verifyLocation(deviceLocation, entrant.getLocation());
//    }
//}
//
//// TODO: Remove the following after the Entrant Class is added
////
////class Entrant {
////    private String name;
////    private double[] location; // Example: {latitude, longitude}
////
////    public Entrant(String name, double latitude, double longitude) {
////        this.name = name;
////        this.location = new double[]{latitude, longitude};
////    }
////    public String getName() {
////        return name;
////    }
////    public double[] getLocation() {
////        return location;
////    }
////}