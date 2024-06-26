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
    private int whitePlayerRating;
    private int blackPlayerRating;

    public OnlineChessGame(String whitePlayerToken,
                           String blackPlayerToken,
                           String whiteUserName,
                           String blackUserName,
                           int whitePlayerRating,
                           int blackPlayerRating) {
        this.whitePlayerToken = whitePlayerToken;
        this.blackPlayerToken = blackPlayerToken;
        this.whiteUserName = whiteUserName;
        this.blackUserName = blackUserName;
        this.whitePlayerRating = whitePlayerRating;
        this.blackPlayerRating = blackPlayerRating;
    }

    public OnlineChessGame() {
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

    @Override
    public String toString() {
        return "OnlineChessGame{" +
                "gameID=" + gameID +
                ", whiteUserName='" + whiteUserName + '\'' +
                ", blackUserName='" + blackUserName + '\'' +
                ", winnerName='" + winnerName + '\'' +
                ", whitePlayerRating=" + whitePlayerRating +
                ", blackPlayerRating=" + blackPlayerRating +
                '}';
    }
}
