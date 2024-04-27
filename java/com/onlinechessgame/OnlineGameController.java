package com.onlinechessgame;

import com.AuthenticationService;
import com.ServerResponse;
import com.chessgame.ChessMove;
import com.gameuser.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling online chess game requests.
 * This controller provides endpoints for managing online chess games, including submitting player moves.
 */
@RestController
@RequestMapping(path = "game/online")
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
     * Checks if the user is authorized to submit the move into the specified game.
     *
     * @param onlineChessMove The request body containing the GameUser and ChessMove objects
     * @param gameID          The ID of the game where the move is to be submitted
     * @return A ServerResponse indicating the outcome of the move submission
     */
    @PostMapping(path = "/submit-move/{gameID}")
    public ServerResponse submitPlayerMove(@RequestBody OnlineChessMove onlineChessMove,
                                           @PathVariable("gameID") int gameID) {
        // Extracting GameUser and ChessMove from OnlineChessMove
        GameUser gameUser = onlineChessMove.getGameUser();
        ChessMove playerMove = onlineChessMove.getChessMove();

        // Check if the user is authorized to submit the move into that game
        if (authenticationService.isUserAuthorizedToAccessGame(gameID, gameUser))
            return chessGameService.submitPlayerMove(gameUser, playerMove, gameID);
        else
            return new ServerResponse(HttpStatus.BAD_REQUEST);
    }

    /**
     * Resigns the player from the specified chess game.
     *
     * @param userWhoResign The user who wants to resign from the game. This should be provided as JSON in the request body.
     * @param gameID        The ID of the chess game from which the user wants to resign.
     * @return A ServerResponse indicating the outcome of the move submission.
     */
    @PostMapping(path = "/resign/{gameID}")
    public ServerResponse getPlayerOfMatch(@RequestBody GameUser userWhoResign,
                                           @PathVariable("gameID") int gameID) {
        // Check if the user is authorized to resign from the game
        if (authenticationService.isUserAuthorizedToAccessGame(gameID, userWhoResign))
            return chessGameService.resign(userWhoResign, gameID);
        else
            return new ServerResponse(HttpStatus.BAD_REQUEST);
    }

    /**
     * Retrieves information about the players involved in the specified chess game.
     *
     * @param gameID The ID of the chess game for which player information is requested.
     * @return A ServerResponse indicating the outcome of the move submission
     */
    @GetMapping(path = "/get-players/{gameID}")
    public ServerResponse getPlayerOfMatch(@PathVariable("gameID") int gameID) {
        return chessGameService.getPlayerOfMatch(gameID);
    }

    /**
     * Retrieves the last 100 game of a player by his username
     *
     * @return A ServerResponse containing information about the last 100 games of a player
     */
    @GetMapping(path = "/match-history100")
    public ServerResponse getTopFiveRatedPlayer(@RequestBody GameUser gameUser) {
        return chessGameService.getLast100Games(gameUser);
    }


}
