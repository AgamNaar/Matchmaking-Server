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
     * <p>
     * This method is called remotely by the server to inform the client that the opponent has resigned from the game.
     *
     * @throws RemoteException If there is a communication-related issue during the remote method invocation.
     *                         This exception is thrown to indicate that a communication failure has occurred.
     */
    void enemyResigned() throws RemoteException;
}
