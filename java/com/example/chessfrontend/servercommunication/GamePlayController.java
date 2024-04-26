package com.example.chessfrontend.servercommunication;

import com.chessgame.ChessMove;
import com.gameuser.GameUser;
import com.onlinechessgame.OnlineChessGame;
import org.springframework.stereotype.Component;

import java.rmi.Naming;

import static com.example.chessfrontend.servercommunication.GamePlayService.PORT;

/**
 * This class handles communication with the remote GamePlayService using RMI (Remote Method Invocation).
 * It provides methods to send chess moves and handle opponent resignations.
 */
@Component
public class GamePlayController {

    /**
     * Sends a chess move to the remote GamePlayService.
     *
     * @param chessMove The chess move to be sent.
     * @param gameUser  The user information to identify the player sending the move.
     * @param game      The OnlineChessGame object representing the current game.
     */
    public void sendMove(ChessMove chessMove, GameUser gameUser, OnlineChessGame game) {
        // Determine the URL to send the move based on the game and player information
        String urlToSend = getPlayerToSendURL(game, gameUser);
        try {
            // Lookup the remote GamePlayService using RMI registry
            GamePlayService service = (GamePlayService) Naming.lookup(urlToSend);

            // Send the chess move to the remote service
            service.sendMove(chessMove.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies the remote GamePlayService that the opponent has resigned from the game.
     *
     * @param gameUser The user information to identify the player initiating the resignation.
     * @param game     The OnlineChessGame object representing the current game.
     */
    public void enemyResigned(GameUser gameUser, OnlineChessGame game) {
        // Determine the URL to send the resignation notification based on the game and player information
        String urlToSend = getPlayerToSendURL(game, gameUser);
        try {
            // Lookup the remote GamePlayService using RMI registry
            GamePlayService service = (GamePlayService) Naming.lookup(urlToSend);

            // Notify the remote service about the opponent's resignation
            service.enemyResigned();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the URL to send requests to the remote GamePlayService based on the game and player information.
     *
     * @param game        The OnlineChessGame object representing the current game.
     * @param otherPlayer The GameUser object representing the opponent player.
     * @return The URL to send requests to the remote GamePlayService.
     */
    public String getPlayerToSendURL(OnlineChessGame game, GameUser otherPlayer) {
        String secondPlayerIP;
        String secondPlayerToken;
        // Determine the IP address and token of the opponent player based on the game
        if (game.getPlayerColor(otherPlayer)) {
            secondPlayerIP = game.getBlackPlayerIP();
            secondPlayerToken = game.getBlackPlayerToken();
        } else {
            secondPlayerIP = game.getWhitePlayerIP();
            secondPlayerToken = game.getWhitePlayerToken();
        }

        // Construct and return the URL using the opponent's IP address and token
        return "rmi://" + secondPlayerIP + ":" + PORT + "/" + secondPlayerToken;
    }
}
