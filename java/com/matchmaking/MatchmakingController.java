package com.matchmaking;

import com.AuthenticationService;
import com.ServerResponse;
import com.gameuser.GameUser;
import jakarta.servlet.http.HttpServletRequest;
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
     * Endpoint to find an online match for a given user.
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


    /**
     * Endpoint for joining action challenge game invention
     *
     * @param gameUser   The user object containing necessary information for joining the game.
     * @param gameCodeID The unique identifier of the game.
     * @param request    The HTTP request object.
     * @return A ServerResponse indicating success or failure of joining the game,
     * if successful return the game ID
     */
    @PostMapping(path = "/join-invention/{gameCodeID}")
    public ServerResponse joinGame(@RequestBody GameUser gameUser,
                                   @PathVariable("gameCodeID") String gameCodeID,
                                   HttpServletRequest request) {
        // Check if the user has a valid token
        if (userAuthentication.isValidToken(gameUser))
            return matchmakingService.joinGame(gameUser, gameCodeID, request.getRemoteAddr());
        else
            return new ServerResponse(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint to creating a challenge game invention
     *
     * @param gameUser The user object creating the game.
     * @param request  The HTTP request object.
     * @return A ServerResponse indicating success or failure of creating the game.
     * if successful, return the game id of the new created game.
     */
    @PostMapping(path = "/create-invention")
    public ServerResponse createGame(@RequestBody GameUser gameUser, HttpServletRequest request) {
        // Check if the user has a valid token
        if (userAuthentication.isValidToken(gameUser))
            return matchmakingService.createGame(gameUser, request.getRemoteAddr());
        else
            return new ServerResponse(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint for canceling a challenge link invention
     *
     * @param gameUser The user object canceling the game.
     * @return A ServerResponse indicating success or failure of canceling the game.
     */
    @PostMapping(path = "/cancel-invention")
    public ServerResponse cancelGame(@RequestBody GameUser gameUser) {
        // Check if the user has a valid token
        if (userAuthentication.isValidToken(gameUser))
            return matchmakingService.cancelGame(gameUser);
        else
            return new ServerResponse(INVALID_TOKEN, HttpStatus.BAD_REQUEST);
    }

}
