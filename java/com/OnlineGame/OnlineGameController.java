package com.OnlineGame;

import com.AuthenticationService;
import com.ChessGame.ChessMove;
import com.GameUser.GameUser;
import com.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling online chess game requests.
 * This controller provides endpoints for managing online chess games, including submitting player moves.
 */
@RestController
@RequestMapping(path = "game/online/")
public class OnlineGameController {

    private final OnlineGameService chessGameService;
    private final AuthenticationService authenticationService;

    /**
     * Constructor for OnlineGameController.
     *
     * @param chessGameService      Service for managing online chess games
     * @param authenticationService Service for authentication and authorization
     */
    @Autowired
    public OnlineGameController(OnlineGameService chessGameService, AuthenticationService authenticationService) {
        this.chessGameService = chessGameService;
        this.authenticationService = authenticationService;
    }

    /**
     * Endpoint for submitting a player move in an online chess game.
     * Checks if the user is valid to submit the move into that specific game.
     *
     * @param gameUser   The GameUser object representing the player
     * @param playerMove The ChessMove object representing the player's move
     * @param gameID     The ID of the online chess game
     * @return A ServerResponse indicating the outcome of the move submission
     */
    @PutMapping(path = "{gameID}")
    public ServerResponse submitPlayerMove(@RequestBody GameUser gameUser,
                                           @RequestBody ChessMove playerMove,
                                           @PathVariable("gameID") int gameID) {
        // Check if the user is authorized to submit the move into that game
        if (authenticationService.isUserAuthorizedToSubmitMove(gameID, gameUser))
            return chessGameService.submitPlayerMove(gameUser, playerMove, gameID);
        else
            return new ServerResponse(HttpStatus.BAD_REQUEST);
    }
}
