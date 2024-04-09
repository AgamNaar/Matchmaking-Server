package com.GameUser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entity class representing a game user.
 */
@Entity
@Table
public class GameUser {

    @Id
    private final String userID;
    private String userName;
    private String email;
    private int rating;

    /**
     * Constructor to create a new GameUser with provided parameters.
     *
     * @param userName The user's name
     * @param email    The user's email
     * @param rating   The user's rating
     */
    public GameUser(String userName, String email, int rating) {
        this.userID = UUID.randomUUID().toString();
        this.userName = userName;
        this.email = email;
        this.rating = rating;
    }

    /**
     * Default constructor. Generates a random userID.
     */
    public GameUser() {
        userID = UUID.randomUUID().toString();
    }

    /**
     * Constructor to deserialize a GameUser object from JSON.
     *
     * @param userID   The user's ID
     * @param email    The user's email
     * @param rating   The user's rating
     * @param userName The user's name
     */
    @JsonCreator
    public GameUser(@JsonProperty("userID") String userID,
                    @JsonProperty("email") String email,
                    @JsonProperty("rating") int rating,
                    @JsonProperty("userName") String userName) {
        this.userID = UUID.randomUUID().toString();
        this.email = email;
        this.rating = rating;
        this.userName = userName;
    }

    /**
     * Getter for user ID.
     *
     * @return The user's ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Getter for username.
     *
     * @return The user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter for user email.
     *
     * @return The user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for user rating.
     *
     * @return The user's rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Setter for username.
     *
     * @param userName The new username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Setter for user email.
     *
     * @param email The new user email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for user rating.
     *
     * @param rating The new user rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * toString method overridden to provide a string representation of the GameUser object.
     *
     * @return String representation of the GameUser object
     */
    @Override
    public String toString() {
        return "GameUser{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", rating=" + rating +
                '}';
    }
}
