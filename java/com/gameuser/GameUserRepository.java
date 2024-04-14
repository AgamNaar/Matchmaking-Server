package com.gameuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
