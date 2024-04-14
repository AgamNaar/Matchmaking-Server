package com.chessgame.gamelogic.pieces;

/**
 * Represents a queen chess piece.
 */
public class Queen extends Piece {

    /**
     * Constructor for a Queen object.
     *
     * @param square the square on which the queen is located
     * @param color  the color of the queen (true for white, false for black)
     */
    public Queen(byte square, boolean color) {
        super(square, color);
    }

    /**
     * Calculates the threatening lines for the queen.
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   the bitboard representing the board state
     * @return the threatening lines as a bitboard
     */
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return threateningLine.getQueenThreateningLine(getSquare(), enemyKingSquare, boardBitBoard);
    }

    /**
     * Calculates the legal moves for the queen.
     *
     * @param allPiecesBitBoard       the bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard the bitboard representing pieces of the same color
     * @return the legal moves as a bitboard
     */
    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getQueenMovement(getSquare(), allPiecesBitBoard, sameColorPiecesBitBoard);
    }
}
