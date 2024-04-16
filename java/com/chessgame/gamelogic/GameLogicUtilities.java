package com.chessgame.gamelogic;

import com.chessgame.gamelogic.pieces.Piece;

import java.util.LinkedList;

/**
 * Utility class for various operations related to chess game logic.
 */
public class GameLogicUtilities {
    // Constants representing piece colors
    public static final boolean WHITE = true;
    public static final boolean BLACK = false;

    // Board size and edge size
    public static final int BOARD_SIZE = 64;
    public static final int BOARD_EDGE_SIZE = 8;

    // Offset values for pawn moves for white and black pieces
    public static final byte WHITE_PAWN_MOVE_OFFSET = 8;
    public static final byte BLACK_PAWN_MOVE_OFFSET = -8;

    // Representation of an empty board
    public static final long EMPTY_BOARD = 0;

    /**
     * Given a square, returns its row number.
     *
     * @param square The square number (0 to 63).
     * @return The row number of the square.
     */
    public static int getRowOfSquare(byte square) {
        return square / BOARD_EDGE_SIZE;
    }

    /**
     * Converts a square represented as a position (i.e. number from 0 to 63)
     * into a bitboard position, i.e. only the bit on the position is set to 1.
     *
     * @param square The square number (0 to 63).
     * @return The bit board representation of the square.
     */
    public static long squareAsBitBoard(long square) {
        return 1L << square;
    }

    /**
     * Updates the position of a piece from its current square to the target square.
     *
     * @param targetSquare  The target square where the piece will be moved.
     * @param currentSquare The current square where the piece is located.
     * @param board         The chessboard array.
     * @param pieceList     The list of all pieces on the board.
     */
    public static void updatePiecePosition(byte targetSquare, byte currentSquare,
                                           Piece[] board, LinkedList<Piece> pieceList) {

        Piece pieceToMove = board[currentSquare];
        Piece pieceToRemove = board[targetSquare];

        // Update the position of the piece on the board and its own position
        pieceToMove.setSquare(targetSquare);
        board[targetSquare] = pieceToMove;
        board[currentSquare] = null;

        // Remove the piece from the target square and remove the piece from its previous position on the board
        pieceList.remove(pieceToRemove);
    }

    /**
     * Shifts a number left if the offset is positive, or right if the offset is negative.
     *
     * @param num    The number to be shifted.
     * @param offset The number of positions to shift (positive for left, negative for right).
     * @return The result of shifting the number.
     */
    public static long shiftNumberLeft(long num, int offset) {
        if (offset > 0)
            return num << offset;
        else
            return num >>> -offset;
    }
}
