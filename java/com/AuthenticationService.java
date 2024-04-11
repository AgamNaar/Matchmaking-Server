package com;

import com.GameUser.GameUser;
import com.GameUser.GameUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component for authenticating user tokens.
 * This component provides functionality to validate user tokens against the database.
 */
@Component
public class AuthenticationService {

    private final GameUserRepository gameUserRepository;

    /**
     * Constructor for AuthenticationService.
     *
     * @param gameUserRepository Repository for accessing game user data
     */
    @Autowired
    public AuthenticationService(GameUserRepository gameUserRepository) {
        this.gameUserRepository = gameUserRepository;
    }

    /**
     * Checks if the provided token matches the token stored in the database for the given user.
     *
     * @param gameUser The GameUser object containing the user information and token to be validated
     * @return true if the provided token is valid, false otherwise
     */
    public boolean isValidToken(GameUser gameUser) {
        // Retrieve the token from the GameUser object
        String userToken = gameUser.getToken();

        // Retrieve the token stored in the database for the user
        String DBUserToken = gameUserRepository.findByUserName(gameUser.getUserName()).getToken();

        // Compare the two tokens to determine if they match
        return userToken.equals(DBUserToken);
    }
}

