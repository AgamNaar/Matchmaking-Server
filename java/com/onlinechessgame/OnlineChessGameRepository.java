package com.onlinechessgame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

/**
 * Repository interface for accessing online chess game data.
 * This repository provides methods for interacting with the database to manage online chess games.
 */
@Repository
public interface OnlineChessGameRepository extends JpaRepository<OnlineChessGame, Integer> {

    /**
     * Retrieves the last 100 game of a player.
     *
     * @return A list of the last 100 game
     */
    @Query("SELECT u FROM OnlineChessGame u WHERE u.whiteUserName = :userName OR u.blackUserName = :userName ORDER BY u.gameID DESC")
    LinkedList<OnlineChessGame> findTop100Players(String userName);
}

