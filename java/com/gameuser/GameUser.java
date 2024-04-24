package com.gameuser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity class representing a game user.
 */
@Entity
@Table
public class GameUser {

    @Id
    private String userName;
    private String email;
    private String password;
    private int rating;
    private String token;

    /**
     * Constructor to deserialize a GameUser object from JSON.
     *
     * @param email    The user's email
     * @param rating   The user's rating
     * @param userName The user's name
     * @param password The user's password
     */
    @JsonCreator
    public GameUser(@JsonProperty("email") String email,
                    @JsonProperty("rating") int rating,
                    @JsonProperty("userName") String userName,
                    @JsonProperty("password") String password,
                    @JsonProperty("token") String token) {
        this.email = email;
        this.rating = rating;
        this.userName = userName;
        this.password = password;
        this.token = token;
    }

    /**
     * Default constructor for the GameUser class.
     */
    public GameUser() {
    }

    /**
     * Retrieves the user's username.
     *
     * @return The username of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Retrieves the user's email.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the user's password.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the user's rating.
     *
     * @return The rating of the user.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Retrieves the user's authentication token.
     *
     * @return The authentication token of the user.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the user's username.
     *
     * @param userName The username to be set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the user's email.
     *
     * @param email The email to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's password.
     *
     * @param password The password to be set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the user's rating.
     *
     * @param rating The rating to be set.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Sets the user's authentication token.
     *
     * @param token The authentication token to be set.
     */
    public void setToken(String token) {
        this.token = token;
    }


    /**
     * Returns a string representation of the GameUser object, including the username and rating.
     *
     * @return A string containing the username and rating of the GameUser object.
     */
    @Override
    public String toString() {
        return "GameUser{" +
                "userName='" + userName + '\'' +
                ", rating=" + rating +
                '}';
    }
}
