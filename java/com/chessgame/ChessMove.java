package com.chessgame;

/**
 * Represents a move of a piece on the chessboard.
 */
public class ChessMove {
    private final byte currentPieceSquare;
    private final byte targetSquare;
    private final char typeOfPieceToPromoteTo;

    /**
     * Constructs a PieceMove object.
     *
     * @param piecePosition          Current position of the piece.
     * @param targetSquare           Target square the piece will move to.
     * @param typeOfPieceToPromoteTo Type of piece to promote to.
     */
    public ChessMove(byte piecePosition, byte targetSquare, char typeOfPieceToPromoteTo) {
        this.currentPieceSquare = piecePosition;
        this.targetSquare = targetSquare;
        this.typeOfPieceToPromoteTo = typeOfPieceToPromoteTo;
    }

    /**
     * Generates the string representation of the move.
     *
     * @return String representation of the move.
     */
    @Override
    public String toString() {
        return positionToNotation(currentPieceSquare) + positionToNotation(targetSquare) + typeOfPieceToPromoteTo;
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

    public char getTypeOfPieceToPromoteTo() {
        return typeOfPieceToPromoteTo;
    }
}
