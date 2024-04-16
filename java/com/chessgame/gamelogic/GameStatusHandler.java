package com.chessgame.gamelogic;

import com.chessgame.ChessGame;
import com.chessgame.ChessMove;
import com.chessgame.gamelogic.pieces.Piece;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Handles game status, normal, check, draw, or checkmate, and checks for repetition of moves.
 * This class is responsible for managing the status of the game, and updating it
 * according to the moves played. It tracks the list of moves, checks for repetitive moves leading to a draw,
 * determines if a player has legal moves to play, and identifies whether a player's king is in check.
 */
@Component
public class GameStatusHandler {

    // Constants defining different game stages
    public static final int NORMAL = 0;
    public static final int CHECK = 1;
    public static final int DRAW = 2;
    public static final int CHECKMATE = 3;

    // Flag indicating whether a repetition of moves has occurred
    private boolean repetition = false;
    // List of moves played in the game
    LinkedList<ChessMove> listOfMoves = new LinkedList<>();

    /**
     * Updates game status and game stage according to the move played and status of the game.
     *
     * @param movePlayed The move played in the current turn.
     * @param game       The instance of the chess game.
     * @return The current game status (NORMAL, CHECK, DRAW, CHECKMATE).
     */
    public int afterTurnHandler(ChessMove movePlayed, ChessGame game) {
        listOfMoves.add(movePlayed);
        boolean doesPlayerHaveMove = doesPlayerHasLegalMovesToPlay(game.getPlayerToPlay(), game);

        // If player is checked and has no legal moves, it's checkmate; if it has moves, it's a check
        if (isPlayerChecked(game)) {
            if (doesPlayerHaveMove)
                return CHECK;
            else
                return CHECKMATE;
            // Player has no moves, but it's not check, so it's a draw
        } else if (!doesPlayerHaveMove)
            return DRAW;

        // Check if it's a draw by repetition
        if (isRepetitionOfMoves(movePlayed))
            return DRAW;

        return NORMAL;
    }

    /**
     * Checks if a repetition of moves has occurred.
     *
     * @param movePlayed The move played in the current turn.
     * @return True if a repetition of moves has occurred, otherwise false.
     */
    private boolean isRepetitionOfMoves(ChessMove movePlayed) {
        int listSize = listOfMoves.size();
        int currSquare = movePlayed.getCurrentPieceSquare();
        int targetSquare = movePlayed.getTargetSquare();

        // need to be at least 9 moves in the list for repetition to occur
        if (listSize < 9)
            return false;

        // Get the second last turn and the third last turn of the player who just played
        ChessMove prevMove = listOfMoves.get(listSize - 5);
        ChessMove prevPrevMove = listOfMoves.get(listSize - 9);

        // Check if the player has repeated the same move twice
        if (currSquare == prevMove.getCurrentPieceSquare() && currSquare == prevPrevMove.getCurrentPieceSquare()
                && targetSquare == prevMove.getTargetSquare() && targetSquare == prevPrevMove.getTargetSquare()) {
            // If the repetition flag is set, it's a draw
            if (repetition)
                return true;
            else {
                // Not a draw, but now opponent can make a draw if they repeat moves themselves
                repetition = true;
                return false;
            }
        }

        // Reset the repetition flag
        return (repetition = false);
    }

    /**
     * Checks if a player has legal moves to play.
     *
     * @param playerColor The color of the player (true for white, false for black).
     * @param game        The instance of the chess game.
     * @return True if the player has at least one legal move, otherwise false.
     */
    private boolean doesPlayerHasLegalMovesToPlay(boolean playerColor, ChessGame game) {
        // Check if at least one of the player's pieces has a legal move
        for (Piece piece : game.getPieceList()) {
            if (piece.getColor() == playerColor) {
                long pieceMovement = game.getLegalMovesAsBitBoard(piece);
                if (pieceMovement != 0)
                    return true; // Found a legal move
            }
        }
        // No legal move found
        return false;
    }

    /**
     * Checks if a player's king is in check.
     *
     * @param game The instance of the chess game.
     * @return True if the player's king is in check, otherwise false.
     */
    public boolean isPlayerChecked(ChessGame game) {
        // Check if king is on one of the squares threatened by enemy pieces
        return (game.getCurrentPlayerKing().getSquareAsBitBoard() & game.getBitBoardOfSquaresThreatenByEnemy()) != 0;
    }
}
