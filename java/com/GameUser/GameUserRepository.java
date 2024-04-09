package com.GameUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository interface for accessing game user data.
 */
@Repository
public interface GameUserRepository extends JpaRepository<GameUser, UUID> {

    /**
     * Finds a game user by their user ID.
     *
     * @param userID The user ID to search for
     * @return The game user with the specified user ID, or null if not found
     */
    GameUser findByUserID(String userID);

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
}
