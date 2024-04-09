package com.GameUser;

import com.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service class for managing game users.
 */
@Service
public class GameUserService {

    // Error message constant
    private static final String USER_INFO_EXISTS = "User info already exist in the server";

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
     * Method for creating a new account.
     *
     * @param gameUser The game user object representing the account to be created
     * @return ServerResponse indicating the outcome of the operation
     */
    public ServerResponse createAnAccount(GameUser gameUser) {
        // Verify that new account doesn't exist in repository
        if (isUserInfoAlreadyInUse(gameUser))
            return new ServerResponse(USER_INFO_EXISTS, HttpStatus.BAD_REQUEST);

        // Try to add new account to the DB
        try {
            gameUserRepository.save(gameUser);
            return new ServerResponse(HttpStatus.OK);
        } catch (Exception exception) {
            return new ServerResponse(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method for deleting an account, after verifying email.
     *
     * @param gameUser The game user object representing the account to be deleted
     * @return ServerResponse indicating the outcome of the operation
     */
    public ServerResponse deleteAnAccount(GameUser gameUser) {
        // Try to delete the account from the DB
        try {
            gameUserRepository.delete(gameUser);
            return new ServerResponse(HttpStatus.OK);
        } catch (Exception exception) {
            return new ServerResponse(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if the user information (userID, userName, email) is already in use.
     *
     * @param gameUser The game user object to check
     * @return true if any of the user information is already in use, false otherwise
     */
    private boolean isUserInfoAlreadyInUse(GameUser gameUser) {
        if (gameUserRepository.findByUserID(gameUser.getUserID()) != null)
            return true;

        if (gameUserRepository.findByUserName(gameUser.getUserName()) != null)
            return true;

        return gameUserRepository.findByEmail(gameUser.getEmail()) != null;
    }
}
