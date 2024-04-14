package com.matchmaking;

import com.gameuser.GameUser;
import com.onlinechessgame.OnlineChessGame;
import com.onlinechessgame.OnlineChessGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Monitors the matchmaking process and handles matchmaking operations.
 * This class coordinates the matchmaking process by managing the creation of new games,
 * waiting for available opponents, and returning matched game IDs or special codes if no match is found.
 */
@Component
public class MatchmakingMonitor {

    // Constants for maximum matchmaking attempts and timeout duration
    private static final int MAX_MATCHMAKING = 1;
    private static final long MATCH_MAKING_TIME_OUT = 20 * 1000;

    // Special return codes for indicating search status
    public static final int SEARCH_NOT_FINISHED = -2;
    public static final int DID_NOT_FIND_PLAYER_TO_PLAY_VS = -1;

    // List to store newly created games
    private static final LinkedList<OnlineChessGame> newGamesList = new LinkedList<>();

    private final MatchmakingRepository matchmakingRepository;
    private final OnlineChessGameRepository onlineChessGameRepository;

    private int currentMatchmaking = 0;

    /**
     * Constructor for MatchmakingMonitor.
     *
     * @param matchmakingRepository     The repository for matchmaking operations.
     * @param onlineChessGameRepository The repository for online chess games.
     */
    @Autowired
    public MatchmakingMonitor(MatchmakingRepository matchmakingRepository,
                              OnlineChessGameRepository onlineChessGameRepository) {
        this.matchmakingRepository = matchmakingRepository;
        this.onlineChessGameRepository = onlineChessGameRepository;
    }

    /**
     * Finds an online match for the given game user within a specified time limit.
     * This method check if it can start a new matchmaking search or if it should wait in matchmaking list.
     * If a suitable opponent is found within the timeout, it returns the ID of the matched game.
     * If no opponent is found within the timeout, it returns a special code indicating that no match was found.
     *
     * @param gameUser The game user requesting an online match.
     * @return The ID of the matched game or a special code if no match is found.
     * @throws InterruptedException If interrupted while waiting.
     */
    public synchronized int findOnlineMatch(GameUser gameUser) throws InterruptedException {
        int gameID;
        long startTime = System.currentTimeMillis();

        // Loop until timeout or match found
        while (System.currentTimeMillis() - startTime < MATCH_MAKING_TIME_OUT) {
            // Check if the repository is not empty and maximum matchmaking attempts not reached
            if (!matchmakingRepository.isEmpty() && currentMatchmaking < MAX_MATCHMAKING) {
                // Start a new search for a game and return its ID
                return startNewSearchAndReturnGameID(gameUser);
            } else {
                // Wait for a game to be available and return its ID
                gameID = waitForGameAndReturnGameID(gameUser);
                if (gameID != DID_NOT_FIND_PLAYER_TO_PLAY_VS)
                    return gameID;
            }
        }
        return DID_NOT_FIND_PLAYER_TO_PLAY_VS;
    }

    /**
     * This method adds the provided user to the matchmaking repository and waits until
     * notified by a newly created game. Once notified, it checks if the user has been
     * matched with an opponent by iterating through the list of newly created games.
     * If the user is matched, return the game ID of the new game and remove the game from the list.
     * If no game is found , it returns a special code indicating that no match was found.
     *
     * @param gameUser The user waiting for a game.
     * @return The ID of the game or a special code if no match is found.
     * @throws InterruptedException If interrupted while waiting.
     */
    private synchronized int waitForGameAndReturnGameID(GameUser gameUser) throws InterruptedException {
        try {
            // Add the user to the matchmaking repository
            matchmakingRepository.add(gameUser);
            // Wait until notified by a newly created game
            wait();
            // Iterate through the list of new games to find if the user get matched
            for (OnlineChessGame game : newGamesList) {
                if (gameUser.getUserName().equals(game.getBlackUserName())) {
                    newGamesList.remove(game);
                    return game.getGameID();
                }
            }
        } finally {
            // Delete the user from the repository if not matched
            matchmakingRepository.delete(gameUser);
        }
        return DID_NOT_FIND_PLAYER_TO_PLAY_VS;
    }

    /**
     * Starts a new matchmaking search and returns the ID of the newly created game.
     * Once the thread finishes and a new game is created, it adds the
     * game to the list and returns its ID. If no game is found within the timeout, it
     * returns a special code indicating that no match was found.
     *
     * @param gameUser The user initiating the matchmaking search.
     * @return The ID of the newly created game or a special code if no match is found.
     * @throws InterruptedException If interrupted while waiting.
     */
    private synchronized int startNewSearchAndReturnGameID(GameUser gameUser) throws InterruptedException {
        try {
            currentMatchmaking++;
            // Create a new matchmaking instance and start the thread
            Matchmaking matchmaking = new Matchmaking(matchmakingRepository, this, gameUser);
            matchmaking.start();
            // Wait for thread to finish running
            while (matchmaking.getNewGameID() == SEARCH_NOT_FINISHED)
                wait();
            // Save the new game on the repository and save it in the new game list
            OnlineChessGame newGame = matchmaking.getNewGame();
            onlineChessGameRepository.save(newGame);
            newGamesList.add(newGame);
            return newGame.getGameID();
        } finally {
            // Decrement the current matchmaking count and notify all waiting threads
            currentMatchmaking--;
            notifyAll();
        }
    }
}
