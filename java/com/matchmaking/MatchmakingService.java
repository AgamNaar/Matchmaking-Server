package com.matchmaking;

import com.ServerResponse;
import com.gameuser.GameUser;
import com.gameuser.GameUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service class for matchmaking operations.
 * This service facilitates finding online matches for users by interacting with the matchmaking monitor and
 * accessing user data from the repository. It provides a method to find an online match for a given user.
 */
@Service
public class MatchmakingService {

    private final MatchmakingMonitor matchmakingMonitor;
    private final GameUserRepository gameUserRepository;

    /**
     * Constructor for MatchmakingService.
     *
     * @param matchmakingMonitor The monitor for managing matchmaking process.
     * @param gameUserRepository The repository for accessing user data.
     */
    @Autowired
    public MatchmakingService(MatchmakingMonitor matchmakingMonitor, GameUserRepository gameUserRepository) {
        this.matchmakingMonitor = matchmakingMonitor;
        this.gameUserRepository = gameUserRepository;
    }

    /**
     * Find an online match for the given user.
     * This method attempts to find an online match, It returns a server response indicating the success or failure
     * of the matchmaking process. If successful, it returns the game ID of the match that was found.
     *
     * @param gameUser The user requesting a match.
     * @param userIP   The user's IP address
     * @return A server response indicating success or failure of the matchmaking process.
     * If successful, return the game ID of the match that was found.
     * If failed, return a server response with appropriate HTTP status code and game ID as -1,-2.
     */
    public ServerResponse findOnlineMatch(GameUser gameUser, String userIP) {
        ServerResponse serverResponse;
        try {
            // Attempt to find an online match for the user using the matchmaking monitor
            // Fetch user from repository to get his rating from the server
            int gameID = matchmakingMonitor.findOnlineMatch(gameUserRepository.findByUserName(gameUser.getUserName()),
                    userIP);

            // Check if a match was found or not
            if (gameID == MatchmakingMonitor.DID_NOT_FIND_PLAYER_TO_PLAY_VS)
                serverResponse = new ServerResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            else
                serverResponse = new ServerResponse(String.valueOf(gameID), HttpStatus.OK);
        } catch (Exception exception) {
            serverResponse = new ServerResponse(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return serverResponse;
    }
}
