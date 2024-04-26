package com.example.chessfrontend.servercommunication;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface defines the methods that can be invoked remotely on the GamePlayService.
 */
public interface GamePlayService extends Remote {

    // Static for port number
    int PORT = 5099;

    /**
     * Sends a chess move to the GamePlayService.
     *
     * @param move The chess move to be sent.
     * @throws RemoteException if there is a communication-related exception during the method invocation.
     */
    void sendMove(String move) throws RemoteException;

    /**
     * Notifies the client that the enemy has resigned from the game.
     * This method is called remotely by the server to inform the client that the opponent has resigned from the game.
     *
     * @throws RemoteException If there is a communication-related issue during the remote method invocation.
     */
    void enemyResigned() throws RemoteException;

    /**
     * Accepts a challenge for the specified game.
     * This method is called remotely by the server to indicate that a challenge for a game has been accepted.
     * It typically updates the game state to reflect the acceptance of the challenge.
     *
     * @param gameID The ID of the game for which the challenge is being accepted.
     * @throws RemoteException If there is a communication-related issue during the remote method invocation.
     */
    void challengeAccept(int gameID) throws RemoteException;
}
