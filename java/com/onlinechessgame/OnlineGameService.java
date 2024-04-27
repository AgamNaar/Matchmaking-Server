package com.onlinechessgame;

import com.ServerResponse;
import com.chessgame.ChessGame;
import com.chessgame.ChessMove;
import com.example.chessfrontend.servercommunication.GamePlayController;
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
    private final GamePlayController gamePlayController;

    private static final String GAME_NOT_EXIST = "Game not found";
    private static final String INVALID_MOVE_ERROR_MSG = "Invalid move";
    private static final String NOT_PLAYER_TURN_TO_PLAY_ERROR_MSG = "Not player turn to play";

    /**
     * Constructs an OnlineGameService with the specified repositories.
     *
     * @param onlineChessGameRepository Repository for accessing online chess game data.
     * @param gameUserRepository        Repository for accessing game user data.
     * @param liveGameRepository        Repository for managing live chess games.
     * @param gamePlayController        Controller for the RMI connection to the players
     */
    @Autowired
    public OnlineGameService(OnlineChessGameRepository onlineChessGameRepository,
                             GameUserRepository gameUserRepository, LiveGameRepository liveGameRepository, GamePlayController gamePlayController) {
        this.onlineChessGameRepository = onlineChessGameRepository;
        this.gameUserRepository = gameUserRepository;
        this.liveGameRepository = liveGameRepository;
        this.gamePlayController = gamePlayController;
    }

    /**
     * Retrieves the players involved in a match identified by the given game ID.
     *
     * @param gameID The ID of the game/match to retrieve player information.
     * @return A ServerResponse containing the usernames of the white and black players,
     * or an error response if the game does not exist.
     */
    public ServerResponse getPlayerOfMatch(int gameID) {
        // Find the game/match with the specified ID
        OnlineChessGame game = liveGameRepository.findGameByID(gameID);
        // If the game does not exist, return a bad request status with an error message
        if (game == null)
            return new ServerResponse(GAME_NOT_EXIST, HttpStatus.BAD_REQUEST);

        // Get the usernames of the white and black players from the game
        String whitePlayersName = game.getWhiteUserName();
        String blackPlayerName = game.getBlackUserName();

        // Find the GameUser objects corresponding to the white and black players
        GameUser whitePlayer = gameUserRepository.findByUserName(whitePlayersName);
        GameUser blackPlayer = gameUserRepository.findByUserName(blackPlayerName);

        // Concatenate the usernames of the white and black players and return them along with OK status
        return new ServerResponse(whitePlayer + "," + blackPlayer, HttpStatus.OK);
    }


    /**
     * Handles a resignation in a chess game.
     *
     * @param userWhoResign The user who is resigning.
     * @param gameID        The ID of the game where resignation occurs.
     * @return A ServerResponse indicating the status of the resignation operation.
     */
    public ServerResponse resign(GameUser userWhoResign, int gameID) {
        // Find the game by its ID
        OnlineChessGame game = liveGameRepository.findGameByID(gameID);

        // Determine the username of the opponent (the player who did not resign)
        String secondPlayerName = userWhoResign.getUserName().equals(game.getWhiteUserName()) ?
                game.getBlackUserName() : game.getWhiteUserName();

        // Set the winner's name to the opponent's name
        game.setWinnerName(secondPlayerName);

        // Update the Elo ratings of the players
        updatePlayerElo(secondPlayerName, userWhoResign.getUserName(), ChessGame.CHECKMATE);

        // Remove the game from the live game repository and save it in game repository
        liveGameRepository.deleteByID(game.getGameID());
        onlineChessGameRepository.save(game);

        // Notify the other player that player resigned
        gamePlayController.enemyResigned(userWhoResign, game);
        return new ServerResponse(HttpStatus.OK);
    }

    /**
     * Processes a player's move in an online chess game.
     * The function executes the move,
     * checks if it's the player's turn, and handles different
     * outcomes such as invalid moves, check, checkmate,
     * or draw. If a move was player, notify the other player
     * It returns a ServerResponse indicating the outcome of the move submission.
     *
     * @param gameUser   The player making the move.
     * @param playerMove The move to be executed.
     * @param gameID     The ID of the game.
     * @return A response indicating the outcome of the move submission.
     */
    public ServerResponse submitPlayerMove(GameUser gameUser, ChessMove playerMove, int gameID) {
        // Find the game by ID
        OnlineChessGame game = liveGameRepository.findGameByID(gameID);

        // Check if it's the player's turn to play
        if (game.getPlayerColor(gameUser) == game.getPlayerToPlay())
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
                gamePlayController.sendMove(playerMove, gameUser, game);
                return new ServerResponse(HttpStatus.OK);
            case ChessGame.DRAW:
            case ChessGame.CHECKMATE:
                // If the game has finished (draw or checkmate), handle the game ending
                return afterGameFinishedHandler(gameUser, game, result, playerMove);
        }
        return null; // This should not happen, return null as a default
    }


    /**
     * Handles the aftermath of a finished chess game. Updates player Elo ratings,
     * determines the winner, saves game data, and deletes the game from live game repository.
     * Notify other player that the game has been finished.
     *
     * @param gameUser   The player involved in the game.
     * @param game       The online chess game.
     * @param result     The result of the game (DRAW, CHECKMATE, etc.).
     * @param playerMove The move that has been played by the player
     * @return A ServerResponse indicating the winner of the game.
     */
    private ServerResponse afterGameFinishedHandler(GameUser gameUser, OnlineChessGame game, int result,
                                                    ChessMove playerMove) {
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
        gamePlayController.sendMove(playerMove, gameUser, game);
        return new ServerResponse(HttpStatus.OK);
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
