package com.matchmaking;

import com.AuthenticationService;
import com.ServerResponse;
import com.gameuser.GameUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * Endpoint to find an online match for a given user.
     * <p>
     * This endpoint requires a valid authentication token and a username.
     *
     * @param gameUser The user requesting a match. This should be provided as JSON in the request body.
     *                 It must contain the user's authentication token and username.
     * @param request  The HttpServletRequest object containing the request information.
     * @return A server response indicating the success or failure of the matchmaking process.
     * If successful, the response contains the game ID of the match that was found.
     * If the authentication token is invalid, the response returns an error indicating the token is invalid.
     */
    @PostMapping(path = "/find-match")
    public ServerResponse findOnlineMatch(@RequestBody GameUser gameUser, HttpServletRequest request) {
        // Check if the user has a valid token
        if (userAuthentication.isValidToken(gameUser))
            return matchmakingService.findOnlineMatch(gameUser, request.getRemoteAddr());
        else
            return new ServerResponse(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
    }
}
