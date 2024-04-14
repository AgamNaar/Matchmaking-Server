package com.chessgame.gamelogic.pieces;

/**
 * Represents a rook chess piece.
 */
public class Rook extends Piece {

    /**
     * Constructor for a Rook object.
     *
     * @param square the square on which the rook is located
     * @param color  the color of the rook (true for white, false for black)
     */
    public Rook(byte square, boolean color) {
        super(square, color);
    }

    /**
     * Calculates the threatening lines for the rook.
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   the bitboard representing the board state
     * @return the threatening lines as a bitboard
     */
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        return threateningLine.getRookThreateningLine(getSquare(), enemyKingSquare, boardBitBoard);
    }

    /**
     * Calculates the legal moves for the rook.
     *
     * @param allPiecesBitBoard       the bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard the bitboard representing pieces of the same color
     * @return the legal moves as a bitboard
     */
    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getRookMovement(getSquare(), allPiecesBitBoard, sameColorPiecesBitBoard);
    }
}
