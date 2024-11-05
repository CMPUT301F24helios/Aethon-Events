import java.util.UUID;

/**
 * This is a class to Manage Media
 */
public class MediaManager {
    String mediaId;
    String image;
    Boolean isProfilePic;
    Boolean isEventPoster;

    /**
     * Constructor for MediaManager class
     * @param image Image encoded as a string
     * @param isProfilePic Boolean representing if image is Profile Pic
     * @param isEventPoster Boolean representing if image is Event Poster
     */
    public MediaManager(String image, Boolean isProfilePic, Boolean isEventPoster) {
        // randomly generated UUID
        this.mediaId = UUID.randomUUID().toString();
        this.image = image;
        this.isProfilePic = isProfilePic;
        this.isEventPoster = isEventPoster;
    }

    /**
     * Gets Media Id
     * @return Media Id
     */
    public String getMediaId() {
        return mediaId;
    }

    /**
     * Gets Image string representation
     * @return image
     */
    public String getImage() {
        return image;
    }

    /**
     * Checks if media is profile pic
     * @return boolean
     */
    public Boolean getIsProfilePic() {
        return isProfilePic;
    }

    /**
     * Checks if media is Event Poster
     * @return boolean
     */
    public Boolean getIsEventPoster() {
        return isEventPoster;
    }

    /**
     * Sets Image in the class
     * @param image string rep of image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Sets whether Image is a Profile Pic
     * @param profilePic bool representing whether Profile Pic
     */
    public void setIsProfilePic(Boolean profilePic) {
        isProfilePic = profilePic;
    }

    /**
     * Sets whether Image is an Event Poster
     * @param eventPoster bool representing whether Event Poster
     */
    public void setIsEventPoster(Boolean eventPoster) {
        isEventPoster = eventPoster;
    }
}
