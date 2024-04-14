package com.chessgame.gamelogic.pieces;

import com.chessgame.gamelogic.GameLogicUtilities;

/**
 * Represents a pawn chess piece.
 */
public class Pawn extends Piece {

    /**
     * Constructor for a Pawn object.
     *
     * @param square the square on which the pawn is located
     * @param color  the color of the pawn (true for white, false for black)
     */
    public Pawn(byte square, boolean color) {
        super(square, color);
    }

    /**
     * Calculates the legal moves for the pawn.
     *
     * @param allPiecesBitBoard       the bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard the bitboard representing pieces of the same color
     * @return the legal moves as a bitboard
     */
    @Override
    public long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard) {
        long enemyPiecesBitBoards = allPiecesBitBoard & ~sameColorPiecesBitBoard;
        return pieceMovement.getPawnMovement(getSquare(), getColor(), allPiecesBitBoard, enemyPiecesBitBoards);
    }

    /**
     * Calculates the threatening lines for the pawn.
     * The threat line of a pawn is its square, if he threatens the king
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   the bitboard representing the board state
     * @return the threatening lines as a bitboard
     */
    @Override
    public long getThreatLines(byte enemyKingSquare, Long boardBitBoard) {
        long pawnAttackSquares = getPawnAttackSquare();
        long enemyKingBitBoardPosition = GameLogicUtilities.squareAsBitBoard(enemyKingSquare);
        if ((pawnAttackSquares & enemyKingBitBoardPosition) != 0)
            return getSquareAsBitBoard();

        return 0;
    }

    /**
     * Returns the attack square of the pawn as bitboards.
     *
     * @return the attack square of the pawn as bitboards
     */
    public long getPawnAttackSquare() {
        return pieceMovement.getPawnCaptureSquare(getColor(), getSquare());
    }
}
