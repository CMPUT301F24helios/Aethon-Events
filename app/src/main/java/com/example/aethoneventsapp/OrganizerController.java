//package com.example.aethoneventsapp;
//
//public class OrganizerController {
//    private OrganizerModel organizerModel;
//    private OrganizerView organizerView;
//
//    // Controller constructor
//    public OrganizerController(OrganizerModel organizerModel, OrganizerView organizerView) {
//        this.organizerModel = organizerModel;
//        this.organizerView = organizerView;
//    }
//
//    // Limit entrants according to the max specified
//    public void limitEntrants(int maxEntrants) {
//        // Business logic to limit entrants
//    }
//
//    public void addEntrantToWaiting(Entrant entrant) {
//        // Add entrant to waiting list and update model
//    }
//
//    public void poolEntrants(List<Entrant> waitlist) {
//        // Pool entrants from the waitlist to the event list based on criteria
//    }
//
//    public void cancelEntrant(Entrant entrant) {
//        // Cancel entrant registration and update model
//    }
//
//    public void updateFacilityProfile(Facility updatedFacility) {
//        // Logic to update facility profile
//    }
//
//    public void updatePoster(String newPoster) {
//        organizerModel.updatePoster(newPoster);
//    }
//
//    // Methods to fetch data from Model and pass to View for display
//    public void showEventDetails(String eventId) {
//        Event event = organizerModel.getEventDetails(eventId);
//        organizerView.displayEventDetails(event);
//    }
//
//    public void showWaitingList() {
//        List<Entrant> waitingList = organizerModel.getWaitingList();
//        organizerView.displayWaitingList(waitingList);
//    }
//
//    public void showChosenEntrants() {
//        List<Entrant> chosenEntrants = organizerModel.getChosenEntrants();
//        organizerView.displayChosenEntrants(chosenEntrants);
//    }
//
//    public void showCancelledEntrants() {
//        List<Entrant> cancelledEntrants = organizerModel.getCancelledEntrants();
//        organizerView.displayCancelledEntrants(cancelledEntrants);
//    }
//}
