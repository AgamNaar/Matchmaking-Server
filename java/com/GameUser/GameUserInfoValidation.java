package com.GameUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component class for validating game user information.
 * Check that the username is between 1-10 chars, only using letters and numbers.
 * Checks that the password is between 6-16 chars,
 * and that it has to have a number, lowercase letter, uppercase letter and special char.
 * Check that is a valid email.
 * Checks that username and email are not in use in DB.
 */
@Component
public class GameUserInfoValidation {
    private final GameUserRepository gameUserRepository;

    // Regular expression patterns for username, password, and email validation
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{1,10}$";
    private static final String PASSWORD_PATTERN
            = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,10}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,20}$";

    private final Pattern emailPattern;
    private final Pattern userNamePattern;
    private final Pattern passwordPattern;

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 3000;

    /**
     * Constructor for GameUserInfoValidation.
     *
     * @param gameUserRepository The repository for accessing game user data.
     */
    @Autowired
    public GameUserInfoValidation(GameUserRepository gameUserRepository) {
        this.gameUserRepository = gameUserRepository;
        this.userNamePattern = Pattern.compile(USERNAME_PATTERN);
        this.passwordPattern = Pattern.compile(PASSWORD_PATTERN);
        this.emailPattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Checks if the user information is valid.
     * Check if password, email and rating are valid.
     * Check that username and email are not in use by other user in the DB.
     *
     * @param gameUser The GameUser object to check
     * @return true if the user information is already in use, false otherwise
     */
    public boolean isGameUserInfoInvalid(GameUser gameUser) {
        // Check if it's a valid username
        if (doesItNotMatchToPattern(gameUser.getUserName(), userNamePattern))
            return true;

        // Check if it's a valid password
        if (doesItNotMatchToPattern(gameUser.getPassword(), passwordPattern))
            return true;

        // Check if it's a valid email
        if (doesItNotMatchToPattern(gameUser.getEmail(), emailPattern))
            return true;

        // Check if the rating is valid
        if (!(MIN_RATING < gameUser.getRating() && gameUser.getRating() < MAX_RATING))
            return true;

        // Check if the username exist on the DB
        if (gameUserRepository.findByUserName(gameUser.getUserName()) != null)
            return true;

        // Check if the email exist on the DB
        return gameUserRepository.findByEmail(gameUser.getEmail()) != null;
    }

    /**
     * Checks if the given string does not match the specified pattern.
     *
     * @param string  The string to be checked against the pattern.
     * @param pattern The regular expression pattern to be matched against.
     * @return true if the string does not match the pattern, false otherwise.
     */
    private boolean doesItNotMatchToPattern(String string, Pattern pattern) {
        // Create a matcher for the given string and pattern
        Matcher matcher = pattern.matcher(string);
        // Return true if the string does not match the pattern, false otherwise
        return !matcher.matches();
    }
}
