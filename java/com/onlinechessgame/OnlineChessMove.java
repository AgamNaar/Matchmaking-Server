package com.onlinechessgame;

import com.example.chessfrontend.modulus.ChessMove;
import com.gameuser.GameUser;

/**
 * Represents a chess move made by a player in an online chess game.
 * It contains information about the chess move and the player who made the move.
 */
public class OnlineChessMove {
    private ChessMove chessMove; // The chess move made by the player
    private GameUser gameUser;   // The player who made the move

    /**
     * Constructs a new OnlineChessMove object with the specified chess move and player.
     *
     * @param chessMove The chess move made by the player.
     * @param gameUser  The player who made the move.
     */
    public OnlineChessMove(ChessMove chessMove, GameUser gameUser) {
        this.chessMove = chessMove;
        this.gameUser = gameUser;
    }

    /**
     * Sets the chess move for this OnlineChessMove object.
     *
     * @param chessMove The chess move to set.
     */
    public void setChessMove(ChessMove chessMove) {
        this.chessMove = chessMove;
    }

    /**
     * Sets the player for this OnlineChessMove object.
     *
     * @param gameUser The player to set.
     */
    public void setGameUser(GameUser gameUser) {
        this.gameUser = gameUser;
    }

    /**
     * Gets the chess move from this OnlineChessMove object.
     *
     * @return The chess move made by the player.
     */
    public ChessMove getChessMove() {
        return chessMove;
    }

    /**
     * Gets the player from this OnlineChessMove object.
     *
     * @return The player who made the move.
     */
    public GameUser getGameUser() {
        return gameUser;
    }
}
