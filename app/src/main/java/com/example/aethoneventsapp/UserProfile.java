package com.example.aethoneventsapp;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;

/**
* The UserProfile class represents a User's account
*/
public class UserProfile {
    String name;
    String email;
    String phoneNumber;
    Boolean isOrganizer;
    Boolean isAdmin;
    String deviceId;
    Boolean enableNotifications;
    // events
    // waitlists
    String profilePicture;


    /**
     * Constructs a UserProfile class instance
     * @param context Android App Context
     * @param name represents user's name
     * @param email represents user's email
     * @param phoneNumber represents user's phone number
     * @param enableNotifications boolean representing whether a user wants notifications
     */
    public UserProfile(Context context, String name, String email, String phoneNumber, Boolean enableNotifications) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enableNotifications = enableNotifications;
        this.deviceId = getDeviceId(context);
    }

    /**
     * get Android Device ID
     * @param context Android App Context
     * @return deviceId
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Set User's name
     * @param name User's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set User's Email
     * @param email User's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set User's phone number
     * @param phoneNumber User's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Set if user isOrganizer
     * @param organizer Whether user is an organizer or not
     */
    public void setOrganizer(Boolean organizer) {
        isOrganizer = organizer;
    }

    /**
     * Set if user is admin
     * @param admin Whether user is an admin
     */
    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    /**
     * Set whether user wants notifications
     * @param enableNotifications Whether user wants notifications
     */
    public void setEnableNotifications(Boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }

    /**
     * Set User's profile picture
     * @param profilePicture User Profile Pic string encoded version
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Generate and Set user's profile if no Profile Pic provided
     */
    public void setProfilePicture() {
        this.profilePicture = profilePicture;
    }

    /**
     * Get user's name
     * @return user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get user's email
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get user's phone number
     * @return user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Get bool representing if user is an organizer
     * @return isOrganizer
     */
    public Boolean getOrganizer() {
        return isOrganizer;
    }

    /**
     * Get bool representing if user is admin
     * @return isAdmin
     */
    public Boolean getAdmin() {
        return isAdmin;
    }

    /**
     * Get user's unique device id
     * @return deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Get bool representing if user wants notifications
     * @return enableNotifications
     */
    public Boolean getEnableNotifications() {
        return enableNotifications;
    }

    /**
     * Get User's profile pic, string encoded
     * @return profilePicture
     */
    public String getProfilePicture() {
        return profilePicture;
    }
}
