package com.GameUser;

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

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRating() {
        return rating;
    }

    public String getToken() {
        return token;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "GameUser{" +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", rating=" + rating +
                '}';
    }
}
