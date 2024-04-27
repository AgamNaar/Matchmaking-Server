package com.matchmaking;

import com.example.chessfrontend.servercommunication.GamePlayController;
import com.gameuser.GameUser;
import com.onlinechessgame.LiveGameRepository;
import com.onlinechessgame.OnlineChessGame;
import com.onlinechessgame.OnlineChessGameRepository;
import org.springframework.stereotype.Component;

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
    public static final int DID_NOT_FIND_PLAYER_TO_PLAY_VS = -1;
    public static final int SEARCH_NOT_FINISHED = -2;
    public static final int FAILED_TO_CREATE_GAME = -3;

    private final MatchmakingRepository matchmakingRepository;
    private final LiveGameRepository liveGameRepository;
    private final OnlineChessGameRepository onlineChessGameRepository;
    private final GamePlayController gamePlayController;

    private int currentMatchmaking = 0;

    /**
     * Constructs a MatchmakingMonitor with the specified repositories.
     *
     * @param matchmakingRepository     The repository for matchmaking data.
     * @param liveGameRepository        The repository for live game data.
     * @param onlineChessGameRepository The repository for online games.
     * @param gamePlayController        The game player controller to notify players.
     */
    public MatchmakingMonitor(MatchmakingRepository matchmakingRepository,
                              LiveGameRepository liveGameRepository,
                              OnlineChessGameRepository onlineChessGameRepository,
                              GamePlayController gamePlayController) {
        this.matchmakingRepository = matchmakingRepository;
        this.liveGameRepository = liveGameRepository;
        this.onlineChessGameRepository = onlineChessGameRepository;
        this.gamePlayController = gamePlayController;
    }


    /**
     * Adds a new chess game to matchmaking and notifies the opponent about the challenge acceptance.
     *
     * @param user1 The first user initiating the challenge
     * @param player1Ip The IP address of the first player
     * @param user2 The second user accepting the challenge
     * @param player2IP The IP address of the second player
     * @return The ID of the created game if successful, -1 if failed
     */
    public synchronized int addToMatchmaking(GameUser user1, String player1Ip, GameUser user2, String player2IP) {
        // Create a new OnlineChessGame object with the provided user and player details
        OnlineChessGame chessGame = new OnlineChessGame(user1.getToken(), user2.getToken(),
                user1.getUserName(), user2.getUserName(), user1.getRating(),user2.getRating());
        // Set the IP addresses of the players
        chessGame.setWhitePlayerIP(player1Ip);
        chessGame.setBlackPlayerIP(player2IP);

        // Save the chess game to the repository
        onlineChessGameRepository.save(chessGame);
        // Add the game to the live game repository
        liveGameRepository.addGame(chessGame);

        // Try to notify the other player that the challenge has been accepted
        try {
            gamePlayController.challengeAccepted(player2IP, user2.getToken(), chessGame.getGameID());
        } catch (Exception e) {
            // If an exception occurs, delete the game from both repositories
            liveGameRepository.deleteByID(chessGame.getGameID());
            onlineChessGameRepository.deleteById(chessGame.getGameID());
            return FAILED_TO_CREATE_GAME;
        }
        return chessGame.getGameID();
    }


    /**
     * Finds an online match for the given game user within a specified time limit.
     * This method check if it can start a new matchmaking search or if it should wait in matchmaking list.
     * If a suitable opponent is found within the timeout, it returns the ID of the matched game.
     * If no opponent is found within the timeout, it returns a special code indicating that no match was found.
     *
     * @param gameUser The game user requesting an online match.
     * @param userIP   The user's IP address
     * @return The ID of the matched game or a special code if no match is found.
     * @throws InterruptedException If interrupted while waiting.
     */
    public synchronized int findOnlineMatch(GameUser gameUser, String userIP) throws InterruptedException {
        int gameID;
        long startTime = System.currentTimeMillis();

        // Loop until timeout or match found
        while (System.currentTimeMillis() - startTime < MATCH_MAKING_TIME_OUT) {
            // Check if the repository is not empty and maximum matchmaking attempts not reached
            if (!matchmakingRepository.isEmpty() && currentMatchmaking < MAX_MATCHMAKING) {
                // Start a new search for a game and return its ID
                return startNewSearchAndReturnGameID(gameUser, userIP);
            } else {
                // Wait for a game to be available and return its ID
                gameID = waitForGameAndReturnGameID(gameUser, userIP);
                if (gameID != DID_NOT_FIND_PLAYER_TO_PLAY_VS)
                    return gameID;
            }
        }
        return DID_NOT_FIND_PLAYER_TO_PLAY_VS;
    }

    /**
     * This method adds the provided user to the matchmaking repository and waits until
     * notified by a newly created game. Once notified, it checks if the user has been
     * matched with an opponent by checking if a new live game was made for him.
     * If the user is matched, return the game ID of the new game and remove the game from the list.
     * If no game is found , it returns a special code indicating that no match was found.
     *
     * @param gameUser The user waiting for a game.
     * @param userIP   The user's IP address
     * @return The ID of the game or a special code if no match is found.
     * @throws InterruptedException If interrupted while waiting.
     */
    private synchronized int waitForGameAndReturnGameID(GameUser gameUser, String userIP) throws InterruptedException {
        try {
            // Add the user to the matchmaking repository
            matchmakingRepository.add(gameUser);
            // Wait until notified by a newly created game
            wait();
            // Check if the players name appear in live game repository, meaning new game for him was made
            int gameID = liveGameRepository.findGameIdByPlayerUserName(gameUser.getUserName());
            if (gameID != LiveGameRepository.GAME_NOT_FOUND) {
                liveGameRepository.findGameByID(gameID).setBlackPlayerIP(userIP);
                return gameID;

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
     * game to the list and returns its ID. If no game is found within the timeout,
     * returns a special code indicating that no match was found.
     *
     * @param gameUser The user initiating the matchmaking search.
     * @param userIP   The user's IP address
     * @return The ID of the newly created game or a special code if no match is found.
     * @throws InterruptedException If interrupted while waiting.
     */
    private synchronized int startNewSearchAndReturnGameID(GameUser gameUser, String userIP)
            throws InterruptedException {
        try {
            currentMatchmaking++;
            // Create a new matchmaking instance and start the thread
            Matchmaking matchmaking = new Matchmaking(matchmakingRepository, this, gameUser);
            matchmaking.start();
            // Wait for thread to finish running
            while (matchmaking.getNewGameID() == SEARCH_NOT_FINISHED)
                wait();

            // Check if it found an opponent
            if (matchmaking.getNewGameID() == DID_NOT_FIND_PLAYER_TO_PLAY_VS)
                return DID_NOT_FIND_PLAYER_TO_PLAY_VS;

            // Save the new game on the repository and save it in the new game list
            OnlineChessGame newGame = matchmaking.getNewGame();
            newGame.setWhitePlayerIP(userIP);
            onlineChessGameRepository.save(newGame);
            liveGameRepository.addGame(newGame);
            return newGame.getGameID();
        } finally {
            // Decrement the current matchmaking count and notify all waiting threads
            currentMatchmaking--;
            notifyAll();
        }
    }


}
