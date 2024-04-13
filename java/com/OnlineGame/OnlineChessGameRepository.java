package com.OnlineGame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing online chess game data.
 * This repository provides methods for interacting with the database to manage online chess games.
 */
@Repository
public interface OnlineChessGameRepository extends JpaRepository<OnlineChessGame, Integer> {

}

