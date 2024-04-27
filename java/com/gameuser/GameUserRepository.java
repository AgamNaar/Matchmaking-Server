package com.gameuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

/**
 * Repository interface for accessing game user data.
 */
@Repository
public interface GameUserRepository extends JpaRepository<GameUser, String> {
    /**
     * Finds a game user by their username.
     *
     * @param userName The username to search for
     * @return The game user with the specified username, or null if not found
     */
    GameUser findByUserName(String userName);

    /**
     * Finds a game user by their email address.
     *
     * @param email The email address to search for
     * @return The game user with the specified email address, or null if not found
     */
    GameUser findByEmail(String email);

    /**
     * Retrieves the top 4 players with the highest elo (rating).
     *
     * @return A list of the top 4 players
     */
    @Query("SELECT u FROM GameUser u ORDER BY u.rating DESC LIMIT 5")
    LinkedList<GameUser> findTop5Players();

}
