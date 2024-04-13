package com.OnlineGame;

import com.ChessGame.ChessGame;
import jakarta.persistence.*;

/**
 * Represents an online chess game.
 */
@Entity
@Table
public class OnlineChessGame extends ChessGame {

    @Id
    @SequenceGenerator(name = "online_chess_game_id_sequence",
            sequenceName = "online_chess_game_id_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "online_chess_game_id_sequence")
    private int gameID;
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
     */
    public OnlineChessGame(String whitePlayerToken,
                           String blackPlayerToken,
                           String whiteUserName,
                           String blackUserName) {
        this.whitePlayerToken = whitePlayerToken;
        this.blackPlayerToken = blackPlayerToken;
        this.whiteUserName = whiteUserName;
        this.blackUserName = blackUserName;
    }

    public int getGameID() {
        return gameID;
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
}
