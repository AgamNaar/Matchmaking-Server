package com.Matchmaking;

import com.GameUser.GameUser;
import com.ServerResponse;
import com.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for matchmaking related operations.
 * This controller provides endpoints for finding online matches for users.
 * If the game ID is -1 it indicates a game was not found.
 */
@RestController
@RequestMapping(path = "matchmaking")
public class MatchmakingController {

    private static final String INVALID_TOKEN = "The token provided was invalid";
    private final MatchmakingService matchmakingService;
    private final AuthenticationService userAuthentication;

    /**
     * Constructor for MatchmakingController.
     *
     * @param matchmakingService The service responsible for matchmaking operations.
     * @param userAuthentication The service responsible for user authentication.
     */
    @Autowired
    public MatchmakingController(MatchmakingService matchmakingService, AuthenticationService userAuthentication) {
        this.matchmakingService = matchmakingService;
        this.userAuthentication = userAuthentication;
    }

    /**
     * Endpoint to find an online match for a given user, require token and username.
     *
     * @param gameUser As JSON, The user requesting a match.
     * @return A server response indicating success or failure of the matchmaking process.
     * If successful, return the game ID of the match that was found.
     */
    @GetMapping
    public ServerResponse findOnlineMatch(@RequestBody GameUser gameUser) {
        // Check if the user have a valid token
        if (userAuthentication.isValidToken(gameUser))
            return matchmakingService.findOnlineMatch(gameUser);
        else
            return new ServerResponse(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
    }
}
