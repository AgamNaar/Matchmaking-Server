package com.chessgame.gamelogic.pieces;

/**
 * Represents a king chess piece.
 */
public class King extends Piece {

    /**
     * Constructor for a King object.
     *
     * @param square the square on which the king is located
     * @param color  the color of the king (true for white, false for black)
     */
    public King(byte square, boolean color) {
        super(square, color);
    }

    /**
     * Calculates the legal moves for the king.
     *
     * @param allPiecesBitBoard       the bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard the bitboard representing pieces of the same color
     * @return the legal moves as a bitboard
     */
    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getKingMovement(getSquare(), sameColorPiecesBitBoard);
    }

    /**
     * King does not calculate threatening lines, always returns 0.
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   the bitboard representing the board state
     * @return always returns 0
     */
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return 0;
    }
}
