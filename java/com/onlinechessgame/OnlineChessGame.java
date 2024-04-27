package com.onlinechessgame;

import com.chessgame.ChessGame;
import com.gameuser.GameUser;
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
    @Transient
    private String whitePlayerToken;
    @Transient
    private String blackPlayerToken;
    private String whiteUserName;
    private String blackUserName;
    @Transient
    private String blackPlayerIP;
    @Transient
    private String whitePlayerIP;
    private String winnerName;

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

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setWhitePlayerToken(String whitePlayerToken) {
        this.whitePlayerToken = whitePlayerToken;
    }

    public void setBlackPlayerToken(String blackPlayerToken) {
        this.blackPlayerToken = blackPlayerToken;
    }

    public void setWhiteUserName(String whiteUserName) {
        this.whiteUserName = whiteUserName;
    }

    public void setBlackUserName(String blackUserName) {
        this.blackUserName = blackUserName;
    }

    public void setBlackPlayerIP(String blackPlayerIP) {
        this.blackPlayerIP = blackPlayerIP;
    }

    public void setWhitePlayerIP(String whitePlayerIP) {
        this.whitePlayerIP = whitePlayerIP;
    }

    public String getBlackPlayerIP() {
        return blackPlayerIP;
    }

    public String getWhitePlayerIP() {
        return whitePlayerIP;
    }

    public boolean getPlayerColor(GameUser gameUser) {
        return gameUser.getUserName().equals(whiteUserName);
    }
}
