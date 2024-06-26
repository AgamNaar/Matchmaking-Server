package com;

import com.gameuser.GameUser;
import com.gameuser.GameUserRepository;
import com.onlinechessgame.LiveGameRepository;
import com.onlinechessgame.OnlineChessGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component for providing authentication and authorization services.
 * This component handles user authentication and authorization for accessing game features.
 */
@Component
public class AuthenticationService {

    private final GameUserRepository gameUserRepository;
    private final LiveGameRepository liveGameRepository;

    /**
     * Constructs an AuthenticationService with the specified repositories.
     *
     * @param gameUserRepository Repository for accessing game user data.
     * @param liveGameRepository Repository for accessing live game data.
     */
    @Autowired
    public AuthenticationService(GameUserRepository gameUserRepository,
                                 LiveGameRepository liveGameRepository) {
        this.gameUserRepository = gameUserRepository;
        this.liveGameRepository = liveGameRepository;
    }

    /**
     * Validates the user token against the token stored in the database.
     *
     * @param gameUser The GameUser object containing the user information and token to be validated
     * @return true if the provided token is valid, false otherwise
     */
    public boolean isValidToken(GameUser gameUser) {
        // Retrieve the token stored in the database for the user
        GameUser gameUserInDB = gameUserRepository.findByUserName(gameUser.getUserName());

        // Check that the account exist in the DB
        if (gameUserInDB == null)
            return false;

        String userToken = gameUser.getToken();
        String DBUserToken = gameUserInDB.getToken();

        // Compare the two tokens to determine if they match
        return userToken.equals(DBUserToken);
    }

    /**
     * Checks if the user is authorized to submit a move to the game with the given game ID.
     * Checks if the game exists in the Live game repository, and if that user is authorized to submit the move,
     * by checking that the username and token is matching to that of the game.
     *
     * @param gameID   The ID of the online chess game
     * @param gameUser The GameUser object representing the user attempting to submit the move
     * @return true if the user is valid to submit a move, false otherwise
     */
    public boolean isUserAuthorizedToAccessGame(int gameID, GameUser gameUser) {
        // Retrieve the online chess game from the repository by its ID
        OnlineChessGame game = liveGameRepository.findGameByID(gameID);

        // If game is null, the game with the specified ID does not exist
        if (game == null)
            return false;

        // Check if the user is the white player and has the correct token
        if (game.getWhiteUserName().equals(gameUser.getUserName())
                && game.getWhitePlayerToken().equals(gameUser.getToken()))
            return true;

        // Check if the user is the black player and has the correct token
        return game.getBlackUserName().equals(gameUser.getUserName())
                && game.getBlackPlayerToken().equals(gameUser.getToken());
    }
}
