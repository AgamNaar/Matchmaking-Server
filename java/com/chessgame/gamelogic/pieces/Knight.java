package com.chessgame.gamelogic.pieces;

import com.chessgame.gamelogic.GameLogicUtilities;

import static com.chessgame.gamelogic.GameLogicUtilities.EMPTY_BOARD;

/**
 * Represents a knight chess piece.
 */
public class Knight extends Piece {

    /**
     * Constructor for a Knight object.
     *
     * @param square the square on which the knight is located
     * @param color  the color of the knight (true for white, false for black)
     */
    public Knight(byte square, boolean color) {
        super(square, color);
    }

    /**
     * Calculates the legal moves for the knight.
     *
     * @param allPiecesBitBoard       the bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard the bitboard representing pieces of the same color
     * @return the legal moves as a bitboard
     */
    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        return pieceMovement.getKnightMovement(getSquare(), sameColorPiecesBitBoard);
    }

    /**
     * Calculates the threatening lines for the knight.
     * The threat line of a knight is its square, if he threatens the king
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   the bitboard representing the board state
     * @return the threatening lines as a bitboard
     */
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        long movement = getMovesAsBitBoard(EMPTY_BOARD, EMPTY_BOARD);
        long enemyKingBitBoardPosition = GameLogicUtilities.squareAsBitBoard(enemyKingSquare);
        if ((movement & enemyKingBitBoardPosition) != 0)
            return getSquareAsBitBoard();

        return 0;
    }
}
