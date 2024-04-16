package com.chessgame.gamelogic;

import com.chessgame.gamelogic.pieces.Pawn;
import com.chessgame.gamelogic.pieces.Piece;

/**
 * Represents a move of a piece on the chessboard.
 */
public class PieceMove {
    // Constants for defining board positions
    private static final byte FIRST_SQUARE_ON_SECOND_ROW = 8;
    private static final int LAST_SQUARE_ON_7TH_ROW = 55;

    private final byte currentPieceSquare;
    private final byte targetSquare;
    private final char typeOfPieceToPromoteTo;
    private final Piece pieceToMove;

    /**
     * Constructs a PieceMove object.
     *
     * @param piecePosition          Current position of the piece.
     * @param targetSquare           Target square the piece will move to.
     * @param typeOfPieceToPromoteTo Type of piece to promote to.
     * @param piece                  The piece making the move.
     */
    public PieceMove(byte piecePosition, byte targetSquare, char typeOfPieceToPromoteTo, Piece piece) {
        this.currentPieceSquare = piecePosition;
        this.targetSquare = targetSquare;
        this.typeOfPieceToPromoteTo = typeOfPieceToPromoteTo;
        this.pieceToMove = piece;
    }

    // Empty constructor (for default initialization)
    public PieceMove() {
        this.currentPieceSquare = 0;
        this.targetSquare = 0;
        this.typeOfPieceToPromoteTo = 0;
        this.pieceToMove = new Pawn((byte) 1, true);
    }

    /**
     * Checks if the move is a promotion move.
     *
     * @return True if it's a promotion move, otherwise false.
     */
    public boolean isItPromotionMove() {
        return (targetSquare < FIRST_SQUARE_ON_SECOND_ROW || targetSquare > LAST_SQUARE_ON_7TH_ROW)
                && pieceToMove instanceof Pawn;
    }

    /**
     * Generates the string representation of the move.
     *
     * @return String representation of the move.
     */
    @Override
    public String toString() {
        // If it's a promotion move, append the type of piece to promote
        if (isItPromotionMove() && pieceToMove instanceof Pawn)
            return positionToNotation(currentPieceSquare) + positionToNotation(targetSquare) + typeOfPieceToPromoteTo;
        return positionToNotation(currentPieceSquare) + positionToNotation(targetSquare);
    }

    /**
     * Converts a byte position on the board to algebraic notation.
     *
     * @param position Byte position on the board.
     * @return Algebraic notation for the position.
     */
    private String positionToNotation(byte position) {
        String row = String.valueOf((position / 8) + 1);
        char column = (char) ((7 - (position % 8)) + 'a');
        return column + row;
    }

    /**
     * Gets the current piece square.
     *
     * @return Current piece square.
     */
    public byte getCurrentPieceSquare() {
        return currentPieceSquare;
    }

    /**
     * Gets the target square.
     *
     * @return Target square.
     */
    public byte getTargetSquare() {
        return targetSquare;
    }
}
