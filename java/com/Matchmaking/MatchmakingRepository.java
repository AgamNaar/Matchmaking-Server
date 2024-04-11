package com.Matchmaking;

import com.GameUser.GameUser;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

/**
 * Repository class for matchmaking operations.
 * This repository manages the list of users waiting for matchmaking and provides methods
 * for adding, deleting, and accessing users in the list.
 */
@Repository
public class MatchmakingRepository {

    private final LinkedList<GameUser> matchMakingList;

    /**
     * Constructor for MatchmakingRepository.
     * Initializes the matchmaking list.
     */
    public MatchmakingRepository() {
        matchMakingList = new LinkedList<>();
    }

    /**
     * Add a user to the matchmaking list.
     *
     * @param gameUser The user to be added.
     */
    public synchronized void add(GameUser gameUser) {
        matchMakingList.add(gameUser);
    }

    /**
     * Delete a user from the matchmaking list.
     *
     * @param gameUser The user to be deleted.
     */
    public synchronized void delete(GameUser gameUser) {
        matchMakingList.remove(gameUser);
    }

    /**
     * Check if the matchmaking list is empty.
     *
     * @return true if the list is empty, false otherwise.
     */
    public synchronized boolean isEmpty() {
        return matchMakingList.isEmpty();
    }

    /**
     * Remove and return a user from the matchmaking list at the specified index.
     *
     * @param index The index of the user to be removed.
     * @return The user removed from the list.
     */
    public synchronized GameUser pop(int index) {
        GameUser userToPop = matchMakingList.get(index);
        matchMakingList.remove(userToPop);
        return userToPop;
    }

    /**
     * Get the size of the matchmaking list.
     *
     * @return The number of elements in the matchmaking list.
     */
    public synchronized int size() {
        return matchMakingList.size();
    }
}
