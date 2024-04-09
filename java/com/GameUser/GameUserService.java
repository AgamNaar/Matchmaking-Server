package com.GameUser;

import com.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for managing game users.
 */
@Service
public class GameUserService {

    // Error message constants
    private static final String USER_INFO_EXISTS = "User info already exists on the server";
    private static final String WRONG_USER_INFO = "Username or password isn't correct";

    // Repository for accessing game user data
    private final GameUserRepository gameUserRepository;

    /**
     * Constructor for GameUserService.
     *
     * @param gameUserRepository Repository for accessing game user data
     */
    @Autowired
    public GameUserService(GameUserRepository gameUserRepository) {
        this.gameUserRepository = gameUserRepository;
    }

    /**
     * Method for creating a new user account.
     * Check if user info is already in use (userName or password)
     *
     * @param gameUser The GameUser object representing the account to be created
     * @return A ServerResponse indicating the outcome of the operation, including the user Token.
     */
    public ServerResponse createAnAccount(GameUser gameUser) {
        // Verify that the new account doesn't already exist in the repository
        if (isUserInfoAlreadyInUse(gameUser))
            return new ServerResponse(USER_INFO_EXISTS, HttpStatus.BAD_REQUEST);

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
     * Check if the password match the userName.
     *
     * @param gameUser The GameUser object representing the account to log into
     * @return A ServerResponse indicating the outcome of the operation
     */
    public ServerResponse logIntoAccount(GameUser gameUser) {
        // Check if the provided username and password are matching
        if (isUserInfoWrong(gameUser.getUserName(), gameUser.getPassword()))
            return new ServerResponse(WRONG_USER_INFO, HttpStatus.BAD_REQUEST);

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
     * Checks if the user information (username and email) is already in use.
     *
     * @param gameUser The GameUser object to check
     * @return true if the user information is already in use, false otherwise
     */
    private boolean isUserInfoAlreadyInUse(GameUser gameUser) {
        return gameUserRepository.findByUserName(gameUser.getUserName()) != null ||
                gameUserRepository.findByEmail(gameUser.getEmail()) != null;
    }

    /**
     * Checks if the provided username and password combination is incorrect, according to the DB.
     *
     * @param userName        The username to check
     * @param passwordByUser  The password provided by the user
     * @return true if the username and password combination is incorrect, false otherwise
     */
    private boolean isUserInfoWrong(String userName, String passwordByUser) {
        GameUser gameUserInDB = gameUserRepository.findByUserName(userName);
        return gameUserInDB == null || !gameUserInDB.getPassword().equals(passwordByUser);
    }
}
