package com.matchmaking;

import com.ServerResponse;
import com.gameuser.GameUser;
import com.gameuser.GameUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.UUID;

import static com.matchmaking.MatchmakingMonitor.FAILED_TO_CREATE_GAME;

/**
 * Service class for matchmaking operations.
 * This service facilitates finding online matches for users by interacting with the matchmaking monitor and
 * accessing user data from the repository. It provides a method to find an online match for a given user.
 */
@Service
public class MatchmakingService {

    private static final String INVALID_GAME_CODED_ID = "No game challenge with that game code ID has been found";
    private static final String GAME_STARTED_OR_NOT_FOUND = "Couldn't cancel the challenge: " +
            "game started or don't exist";
    private final MatchmakingMonitor matchmakingMonitor;
    private final GameUserRepository gameUserRepository;
    private final LinkedList<ChallengeInviteInfo> pendingChallengesInfoList = new LinkedList<>();

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

    /**
     * Creates a challenge game invitation with the provided user and IP address.
     *
     * @param gameUser The user initiating the game.
     * @param userIP   The IP address of the user.
     * @return A ServerResponse indicating the status of the operation,
     * including the game code ID to join that game.
     */
    public ServerResponse createGame(GameUser gameUser, String userIP) {
        // Generate a unique game code ID
        String gameCodeID = UUID.randomUUID().toString().substring(0, 4);
        // Create game info object
        ChallengeInviteInfo gameInfo = new ChallengeInviteInfo(gameUser, userIP, gameCodeID);
        // Add game info to the list of pending challenges
        pendingChallengesInfoList.add(gameInfo);
        // Return a ServerResponse with the game code ID and OK status
        return new ServerResponse(gameCodeID, HttpStatus.OK);
    }

    /**
     * Join a specific game challenge by its game code ID.
     *
     * @param gameUser   The user attempting to join the game.
     * @param gameCodeID The code ID of the game to join.
     * @param userIP     The IP address of the user.
     * @return A ServerResponse indicating the status of the operation.
     * if successful, return the game ID of the new game
     */
    public ServerResponse joinGame(GameUser gameUser, String gameCodeID, String userIP) {
        // Iterate through pending challenges to find the matching game code ID
        for (ChallengeInviteInfo gameInfo : pendingChallengesInfoList) {
            if (gameInfo.getGameCodeID().equals(gameCodeID)) {
                // Add the game to the matchmaking and retrieve game ID
                int gameID = matchmakingMonitor.addToMatchmaking(gameUser, userIP,
                        gameInfo.getUserWhoCreated(), gameInfo.getIpUserWhoCreated());
                // Check if failed to create game
                if (gameID == FAILED_TO_CREATE_GAME)
                    // Return ServerResponse with internal server error status
                    return new ServerResponse(HttpStatus.INTERNAL_SERVER_ERROR);
                // Return ServerResponse with game ID and OK status
                return new ServerResponse(String.valueOf(gameID), HttpStatus.OK);
            }
        }
        // No matching game code ID found, return bad request status
        return new ServerResponse(INVALID_GAME_CODED_ID, HttpStatus.BAD_REQUEST);
    }

    /**
     * Cancels a challenge game invention created by the specified user.
     *
     * @param gameUser The user canceling the game.
     * @return A ServerResponse indicating the status of the operation.
     */
    public ServerResponse cancelGame(GameUser gameUser) {
        // Iterate through pending challenges to find the game created by the user
        for (ChallengeInviteInfo info : pendingChallengesInfoList) {
            if (info.getUserWhoCreated().getToken().equals(gameUser.getToken())) {
                // Remove game info from the list of pending challenges
                pendingChallengesInfoList.remove(info);
                // Return ServerResponse with OK status
                return new ServerResponse(HttpStatus.OK);
            }
        }
        // No game found created by the user, return conflict status
        return new ServerResponse(GAME_STARTED_OR_NOT_FOUND, HttpStatus.CONFLICT);
    }

}
