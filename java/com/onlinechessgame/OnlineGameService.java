package com.onlinechessgame;

import com.ServerResponse;
import com.chessgame.ChessGame;
import com.chessgame.ChessMove;
import com.gameuser.GameUser;
import com.gameuser.GameUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service class for managing online chess games. Handles player moves, turn validations,
 * and game result processing. It interacts with repositories to access game and player data.
 */
@Service
public class OnlineGameService {

    private static final double CONSTANT = 32;
    private final OnlineChessGameRepository onlineChessGameRepository;
    private final GameUserRepository gameUserRepository;
    private final LiveGameRepository liveGameRepository;

    private static final String GAME_NOT_EXIST = "Game not found";
    private static final String INVALID_MOVE_ERROR_MSG = "Invalid move";
    private static final String NOT_PLAYER_TURN_TO_PLAY_ERROR_MSG = "Not player turn to play";

    /**
     * Constructs an OnlineGameService with the specified repositories.
     *
     * @param onlineChessGameRepository Repository for accessing online chess game data.
     * @param gameUserRepository        Repository for accessing game user data.
     * @param liveGameRepository        Repository for managing live chess games.
     */
    @Autowired
    public OnlineGameService(OnlineChessGameRepository onlineChessGameRepository,
                             GameUserRepository gameUserRepository, LiveGameRepository liveGameRepository) {
        this.onlineChessGameRepository = onlineChessGameRepository;
        this.gameUserRepository = gameUserRepository;
        this.liveGameRepository = liveGameRepository;
    }

    /**
     * Processes a player's move in an online chess game.
     * The function executes the move,
     * checks if it's the player's turn, and handles different
     * outcomes such as invalid moves, check, checkmate,
     * or draw. It returns a ServerResponse indicating the outcome
     * of the move submission.
     *
     * @param gameUser   The player making the move.
     * @param playerMove The move to be executed.
     * @param gameID     The ID of the game.
     * @return A response indicating the outcome of the move submission.
     */
    public ServerResponse submitPlayerMove(GameUser gameUser, ChessMove playerMove, int gameID) {
        // Find the game by ID
        OnlineChessGame game = liveGameRepository.findGameByID(gameID);

        // If the game doesn't exist, return a bad request response
        if (game == null)
            return new ServerResponse(GAME_NOT_EXIST, HttpStatus.BAD_REQUEST);

        // Check if it's the player's turn to play
        if (!isPlayerTurnToPlay(gameUser, game))
            return new ServerResponse(NOT_PLAYER_TURN_TO_PLAY_ERROR_MSG, HttpStatus.BAD_REQUEST);

        // Execute the player's move and get the result
        int result = game.executeMove(playerMove);

        // Handle different outcomes based on the result of the move execution
        switch (result) {
            case ChessGame.MOVE_NOT_EXECUTED:
                // If the move was not executed successfully, return a bad request response
                return new ServerResponse(INVALID_MOVE_ERROR_MSG, HttpStatus.BAD_REQUEST);
            case ChessGame.NORMAL:
            case ChessGame.CHECK:
                // If the move was executed successfully, notify the second player and return an OK response
                notifySecondPlayerOnMove(playerMove);
                return new ServerResponse(HttpStatus.OK);
            case ChessGame.DRAW:
            case ChessGame.CHECKMATE:
                // If the game has finished (draw or checkmate), handle the game ending
                return afterGameFinishedHandler(gameUser, game, result);
        }
        return null; // This should not happen, return null as a default
    }


    /**
     * Checks if it is the player's turn to make a move in the given online chess game.
     *
     * @param gameUser The player attempting to make a move.
     * @param game     The online chess game.
     * @return True if it is the player's turn, otherwise false.
     */
    private boolean isPlayerTurnToPlay(GameUser gameUser, OnlineChessGame game) {
        // If its white's turn to play, check if the user summiting the move matches the white player token
        if (game.getPlayerToPlay() && game.getWhitePlayerToken().equals(gameUser.getToken()))
            return true;
        // If its black's turn to play, check if the user summiting the move matches the black player token
        return !game.getPlayerToPlay() && game.getBlackPlayerToken().equals(gameUser.getToken());
    }

    private void notifySecondPlayerOnMove(ChessMove move) {
        //TODO: finish
        //notify
    }

    /**
     * Handles the aftermath of a finished chess game. Updates player Elo ratings,
     * determines the winner, saves game data, and deletes the game from live game repository.
     *
     * @param gameUser The player involved in the game.
     * @param game     The online chess game.
     * @param result   The result of the game (DRAW, CHECKMATE, etc.).
     * @return A ServerResponse indicating the winner of the game.
     */
    private ServerResponse afterGameFinishedHandler(GameUser gameUser, OnlineChessGame game, int result) {
        // Get the second player's name
        String secondPlayerName = gameUser.getUserName().equals(game.getWhiteUserName()) ?
                game.getBlackUserName() : game.getWhiteUserName();

        // If the game is not a draw, update the winner's name
        if (result != ChessGame.DRAW)
            game.setWinnerName(gameUser.getUserName());

        updatePlayerElo(gameUser.getUserName(), secondPlayerName, result);

        // Remove the game from the live game repository and add it to the finished game repository
        liveGameRepository.deleteByID(game.getGameID());
        onlineChessGameRepository.save(game);
        return new ServerResponse(game.getWinnerName(), HttpStatus.OK);
    }

    /**
     * Updates the Elo ratings of two players based on the outcome of a chess game.
     *
     * @param firstPlayerID  The ID of the first player.
     * @param secondPlayerID The ID of the second player.
     * @param result         The result of the game (DRAW, CHECKMATE, etc.).
     */
    private void updatePlayerElo(String firstPlayerID, String secondPlayerID, int result) {
        // Retrieve the GameUser objects for both players
        GameUser playerA = gameUserRepository.getReferenceById(firstPlayerID);
        GameUser playerB = gameUserRepository.getReferenceById(secondPlayerID);

        // Retrieve the current Elo ratings of both players
        double ratingA = playerA.getRating();
        double ratingB = playerB.getRating();

        // Calculate the expected scores for both players using the Elo rating formula
        double expectedScoreA = 1 / (1 + Math.pow(10, (ratingB - ratingA) / 400.0));
        double expectedScoreB = 1 / (1 + Math.pow(10, (ratingA - ratingB) / 400.0));

        // Determine the actual scores based on the game result
        double actualScoreA = result == OnlineChessGame.DRAW ? 0.5 : 1;
        double actualScoreB = result == OnlineChessGame.DRAW ? 0.5 : 0;

        // Calculate the change in ratings for both players
        playerA.setRating((int) (ratingA + CONSTANT * (actualScoreA - expectedScoreA)));
        playerB.setRating((int) (ratingB + CONSTANT * (actualScoreB - expectedScoreB)));

        // Save the updated Elo ratings for both players
        gameUserRepository.save(playerA);
        gameUserRepository.save(playerB);
    }

}
