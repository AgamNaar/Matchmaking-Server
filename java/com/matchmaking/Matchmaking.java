package com.matchmaking;

import com.gameuser.GameUser;
import com.onlinechessgame.OnlineChessGame;

/**
 * Represents a matchmaking thread responsible for finding opponents for game users.
 * This class implements the matchmaking logic by continuously searching for suitable opponents
 * for the first player within a specified time limit and elo range. If a suitable opponent is found,
 * it creates a new game and notifies the monitor. If no opponent is found within the time limit,
 * it sets a special code and notifies the monitor.
 */
public class Matchmaking extends Thread {

    // Constants for search timeout, maximum elo range, initial elo range, and elo increment
    private static final long SEARCH_TIME_OUT = 20 * 1000;
    private static final int MAX_ELO_RANGE = 500;
    private static final int INITIAL_ELO_RANGE = 10;
    private static final int ELO_INCREMENT = 10;

    // Repository and monitor for matchmaking operations
    private final MatchmakingRepository matchmakingRepository;
    private final MatchmakingMonitor matchmakingMonitor;

    // Newly created game and its ID
    private OnlineChessGame newGame;
    private int newGameID = MatchmakingMonitor.SEARCH_NOT_FINISHED;

    // First player requesting matchmaking
    private final GameUser firstPlayer;

    /**
     * Constructor for Matchmaking.
     *
     * @param matchmakingRepository The repository for matchmaking operations.
     * @param matchmakingMonitor    The monitor for matchmaking operations.
     * @param gameUser              The user requesting matchmaking.
     */
    public Matchmaking(MatchmakingRepository matchmakingRepository, MatchmakingMonitor matchmakingMonitor,
                       GameUser gameUser) {
        this.matchmakingRepository = matchmakingRepository;
        this.matchmakingMonitor = matchmakingMonitor;
        this.firstPlayer = gameUser;
    }

    /**
     * Overrides the run method of the Thread class to implement the matchmaking logic.
     * This method continuously searches for suitable opponents for the first player
     * within a specified time limit and elo range.
     * If a suitable opponent is found, it creates a new game and notifies the monitor.
     * If no opponent is found within the time limit, it sets a special code and notifies the monitor.
     */
    @Override
    public void run() {
        // Initialize elo range and start time
        int eloRange = INITIAL_ELO_RANGE;
        long startTime = System.currentTimeMillis();

        // Timeout after SEARCH_TIME_OUT seconds
        while (System.currentTimeMillis() - startTime < SEARCH_TIME_OUT) {
            // Loop through the matchmaking repository to find opponents
            int i = 0;
            while (i < matchmakingRepository.size()) {
                // Get the next player from the repository
                GameUser secondPlayer = matchmakingRepository.pop(i);
                // Check if the matchmaking with the player is valid within the current elo range
                if (isValidMatchMaking(secondPlayer, eloRange)) {
                    // If a valid opponent is found, create a new game with them
                    createAndSaveNewGame(secondPlayer);
                    return;
                }
                // If not a valid opponent, add the player back to the repository and move to the next one
                matchmakingRepository.add(secondPlayer);
                i++;
            }
            // Increase elo range for the next iteration, but ensure it doesn't exceed the maximum
            eloRange = Math.min(eloRange + ELO_INCREMENT, MAX_ELO_RANGE);
        }
        // If no opponent found within the timeout, set a special code and notify the monitor
        newGameID = MatchmakingMonitor.DID_NOT_FIND_PLAYER_TO_PLAY_VS;
        synchronized (matchmakingMonitor) {
            matchmakingMonitor.notifyAll();
        }
    }

    /**
     * Creates a new game with the provided opponent and saves it.
     *
     * @param secondPlayer The opponent for the new game.
     */
    private void createAndSaveNewGame(GameUser secondPlayer) {
        // Create a new online chess game
        newGame = new OnlineChessGame(firstPlayer.getToken(), secondPlayer.getToken(),
                firstPlayer.getUserName(), secondPlayer.getUserName(),
                firstPlayer.getRating(), secondPlayer.getRating());

        newGameID = newGame.getGameID();
        // Notify monitor about the new game
        synchronized (matchmakingMonitor) {
            matchmakingMonitor.notifyAll();
        }
    }

    /**
     * Checks if the matchmaking with the provided opponent is valid within the given elo range.
     *
     * @param secondPlayer The opponent to check matchmaking with.
     * @param eloRange     The elo range within which the matchmaking should be valid.
     * @return true if the matchmaking is valid, false otherwise.
     */
    private boolean isValidMatchMaking(GameUser secondPlayer, int eloRange) {
        int firstPlayerElo = firstPlayer.getRating();
        int secondPlayerElo = secondPlayer.getRating();

        // Check if the elo difference is within the specified range
        return (secondPlayerElo - eloRange < firstPlayerElo && firstPlayerElo < secondPlayerElo + eloRange);
    }

    /**
     * Gets the ID of the newly created game.
     *
     * @return The ID of the newly created game.
     */
    public int getNewGameID() {
        return newGameID;
    }

    /**
     * Gets the newly created game.
     *
     * @return The newly created game.
     */
    public OnlineChessGame getNewGame() {
        return newGame;
    }
}

