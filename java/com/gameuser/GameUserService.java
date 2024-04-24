package com.gameuser;

import com.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for managing game users.
 * This service provides functionality for creating, deleting, and logging into user accounts in the game system.
 */
@Service
public class GameUserService {

    // Error message constants
    private static final String WRONG_USER_INFO = "Username or password isn't correct";
    private static final String INVALID_USER_INFO = "User info is invalid";

    private final GameUserRepository gameUserRepository;
    private final GameUserInfoValidation gameUserInfoValidation;

    /**
     * Constructor for GameUserService.
     *
     * @param gameUserRepository     Repository for accessing game user data
     * @param gameUserInfoValidation Service to validate game user info
     */
    @Autowired
    public GameUserService(GameUserRepository gameUserRepository, GameUserInfoValidation gameUserInfoValidation) {
        this.gameUserRepository = gameUserRepository;
        this.gameUserInfoValidation = gameUserInfoValidation;
    }

    /**
     * Method for creating a new user account.
     * Check if user info is valid, if yes create new user in DB.
     *
     * @param gameUser The GameUser object representing the account to be created
     * @return A ServerResponse indicating the outcome of the operation, including the user Token.
     */
    public ServerResponse createAnAccount(GameUser gameUser) {
        // Verify that new game user info is valid, and not in use in the DB
        if (gameUserInfoValidation.isGameUserInfoInvalid(gameUser))
            return new ServerResponse(INVALID_USER_INFO, HttpStatus.BAD_REQUEST);

        // Try to add the new account to the database
        try {
            // Generate a token for the user, and save the new user in the DB
            gameUser.setToken(UUID.randomUUID().toString());
            gameUserRepository.save(gameUser);
            return new ServerResponse(gameUser.getToken(), HttpStatus.OK);
        } catch (Exception exception) {
            return new ServerResponse(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method for deleting an existing user account.
     * Checks if the given username and password match on the DB, if yes delete the account.
     *
     * @param gameUser The GameUser object representing the account to be deleted
     * @return A ServerResponse indicating the outcome of the operation
     */
    public ServerResponse deleteAnAccount(GameUser gameUser) {
        // Check if the provided username and password are correct
        if (isUserInfoWrong(gameUser.getUserName(), gameUser.getPassword()))
            return new ServerResponse(WRONG_USER_INFO, HttpStatus.BAD_REQUEST);

        // Try to delete the account from the database
        try {
            gameUserRepository.delete(gameUser);
            return new ServerResponse(HttpStatus.OK);
        } catch (Exception exception) {
            return new ServerResponse(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method for logging into a user account.
     * Check if the password match the userName according to the DB
     * If yes, create and return new token for the player
     *
     * @param gameUser The GameUser object representing the account to log into
     * @return A ServerResponse indicating the outcome of the operation, including user new token.
     */
    public ServerResponse logIntoAccount(GameUser gameUser) {
        // Check if the provided username and password are matching
        if (isUserInfoWrong(gameUser.getUserName(), gameUser.getPassword()))
            return new ServerResponse(INVALID_USER_INFO, HttpStatus.BAD_REQUEST);

        // Try to save the account in the database with a new token
        try {
            // Generate a new token for the account and save it in the DB
            String newToken = UUID.randomUUID().toString();
            gameUser.setToken(newToken);
            gameUserRepository.save(gameUser);
            return new ServerResponse(newToken, HttpStatus.OK);
        } catch (Exception exception) {
            return new ServerResponse(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves the top 4 players with the highest rating and returns them as a string representation
     * in the response.
     *
     * @return A ServerResponse containing the string representation of the top 4 players
     */
    public ServerResponse getTopFourRatedPlayer() {
        // Returns the string representation in a ServerResponse with HTTP status OK
        return new ServerResponse(String.valueOf(gameUserRepository.findTop4Players()), HttpStatus.OK);
    }

    /**
     * Checks if the provided username and password combination is incorrect, according to the DB.
     *
     * @param userName       The username to check
     * @param passwordByUser The password provided by the user
     * @return true if the username and password combination is incorrect, false otherwise
     */
    private boolean isUserInfoWrong(String userName, String passwordByUser) {
        GameUser gameUserInDB = gameUserRepository.findByUserName(userName);
        return gameUserInDB == null || !gameUserInDB.getPassword().equals(passwordByUser);
    }
}
