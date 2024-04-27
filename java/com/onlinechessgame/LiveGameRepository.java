package com.onlinechessgame;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;

/**
 * Repository for managing live chess games.
 */
@Repository
public class LiveGameRepository {
    // Constant indicating game not found.
    public static final int GAME_NOT_FOUND = -1;


    // List to store live chess games.
    private final LinkedList<OnlineChessGame> liveGameList = new LinkedList<>();

    /**
     * Adds new game to the repository.
     *
     * @param newGame The new game to be created.
     */
    public void addGame(OnlineChessGame newGame) {
        liveGameList.add(newGame);
    }

    /**
     * Finds a game in the repository by its ID.
     *
     * @param gameID The ID of the game to find.
     * @return The game if found, otherwise null.
     */
    public OnlineChessGame findGameByID(int gameID) {
        for (OnlineChessGame game : liveGameList)
            if (game.getGameID() == gameID)
                return game;

        return null;
    }

    /**
     * Deletes a game from the repository by its ID.
     *
     * @param gameID The ID of the game to delete.
     */
    public void deleteByID(int gameID) {
        liveGameList.removeIf(game -> game.getGameID() == gameID);
    }

    /**
     * Finds the ID of a game by the player's username.
     *
     * @param userName The username of the player.
     * @return The ID of the game if found, otherwise GAME_NOT_FOUND constant.
     */
    public int findGameIdByPlayerUserName(String userName) {
        for (OnlineChessGame game : liveGameList)
            if (game.getBlackUserName().equals(userName) || game.getWhiteUserName().equals(userName))
                return game.getGameID();

        return GAME_NOT_FOUND;
    }

}
