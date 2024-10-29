package com.example.aethoneventsapp;

import java.util.List;

/**
 * Admin class represents the admin entity in the system.
 * @author Dhruv Kairon
 */
public class Admin {
    private String adminId;
    private String deviceId;
    private String adminName;
    private String adminEmail;
    private List<String> events;

    /**
     * Default constructor for the Admin class.
     * @param adminId
     * @param deviceId
     * @param adminName
     * @param adminEmail
     * @param events
     */
    public Admin(String adminId, String deviceId, String adminName, String adminEmail, List<String> events) {
        this.adminId = adminId;
        this.deviceId = deviceId;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.events = events;
    }

    /**
     * Getter method for the adminId.
     * @return adminId
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     * Setter method for the adminId.
     * @param adminId
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    /**
     * Getter method for the deviceId.
     * @return deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Setter method for the deviceId.
     * @param deviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Getter method for the adminName.
     * @return adminName
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * Setter method for the adminName.
     * @param adminName
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * Getter method for the adminEmail.
     * @return adminEmail
     */
    public String adminEmail() {
        return adminEmail;
    }

    /**
     * Setter method for the adminEmail.
     * @param adminEmail
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * Getter method for the events.
     * @return events
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * Setter method for the events.
     * @param events
     */
    public void setEvents(List<String> events) {
        this.events = events;
    }

    /**
     * Method to remove an event to the events list.
     * @param eventId
     */
    public void deleteEvent(String eventId) {
        events.remove(eventId);
    }

}


