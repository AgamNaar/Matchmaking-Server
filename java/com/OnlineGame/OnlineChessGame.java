package com.OnlineGame;

import com.ChessGame.ChessGame;

/**
 * Represents an online chess game.
 */
public class OnlineChessGame extends ChessGame {

    private final int gameID;
    private final String whitePlayerToken;
    private final String blackPlayerToken;
    private final String whiteUserName;
    private final String blackUserName;

    /**
     * Constructor for OnlineChessGame.
     *
     * @param whitePlayerToken The token of the white player.
     * @param blackPlayerToken The token of the black player.
     * @param whiteUserName    The username of the white player.
     * @param blackUserName    The username of the black player.
     * @param gameID           The unique ID of the game.
     */
    public OnlineChessGame(String whitePlayerToken,
                           String blackPlayerToken,
                           String whiteUserName,
                           String blackUserName,
                           int gameID) {
        this.whitePlayerToken = whitePlayerToken;
        this.blackPlayerToken = blackPlayerToken;
        this.whiteUserName = whiteUserName;
        this.blackUserName = blackUserName;
        this.gameID = gameID;
    }


    public String getWhitePlayerToken() {
        return whitePlayerToken;
    }

    public String getBlackPlayerToken() {
        return blackPlayerToken;
    }

    public String getWhiteUserName() {
        return whiteUserName;
    }

    public String getBlackUserName() {
        return blackUserName;
    }

    public int getGameID() {
        return gameID;
    }
}
