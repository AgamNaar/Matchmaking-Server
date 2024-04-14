package com.chessgame.gamelogic.pieces;

/**
 * Represents a bishop chess piece.
 */
public class Bishop extends Piece {

    /**
     * Constructor for a Bishop object.
     *
     * @param square the square on which the bishop is located
     * @param color  the color of the bishop (true for white, false for black)
     */
    public Bishop(byte square, boolean color) {
        super(square, color);
    }

    /**
     * Calculates the threatening lines for the bishop.
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   the bitboard representing the board state
     * @return the threatening lines as a bitboard
     */
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return threateningLine.getBishopThreateningLine(getSquare(), enemyKingSquare, boardBitBoard);
    }

    /**
     * Calculates the legal moves for the bishop.
     *
     * @param allPiecesBitBoard       the bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard the bitboard representing pieces of the same color
     * @return the legal moves as a bitboard
     */
    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getBishopMovement(getSquare(), allPiecesBitBoard, sameColorPiecesBitBoard);
    }
}
