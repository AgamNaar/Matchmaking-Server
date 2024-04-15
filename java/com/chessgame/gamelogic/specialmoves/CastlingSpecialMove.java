package com.chessgame.gamelogic.specialmoves;

import com.chessgame.gamelogic.GameLogicUtilities;
import com.chessgame.gamelogic.pieces.King;
import com.chessgame.gamelogic.pieces.Piece;
import com.chessgame.gamelogic.pieces.Rook;

import java.util.LinkedList;

import static com.chessgame.gamelogic.GameLogicUtilities.WHITE;

/**
 * CastlingSpecialMove class handles special moves related to castling in chess.
 */
public class CastlingSpecialMove {
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;

    // Initial positions of kings and rooks for castling
    public static final byte INITIAL_WHITE_KING_SQUARE = 3;
    public static final byte INITIAL_BLACK_KING_SQUARE = 59;
    public static final byte INITIAL_WHITE_ROOK_SQUARE_SHORT = 0;
    public static final byte INITIAL_WHITE_ROOK_SQUARE_LONG = 7;
    public static final byte INITIAL_BLACK_ROOK_SQUARE_SHORT = 56;
    public static final byte INITIAL_BLACK_ROOK_SQUARE_LONG = 63;

    // Bitboards and offsets for castling conditions
    private static final long SHORT_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD = 0b1110;
    private static final long SHORT_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD = 0b0110;
    private static final long SHORT_CASTLING_SQUARE_BITBOARD = 0b10;
    private static final long LONG_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD = 0b111000;
    private static final long LONG_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD = 0b1110000;
    private static final long LONG_CASTLING_SQUARE_BITBOARD = 0b100000;
    private static final int BLACK_CASTLING_SQUARE_OFFSET = 56;
    private static final byte SHORT_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE = 1;
    private static final byte LONG_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE = -1;

    // Square indices for castling
    private static final long WHITE_SHORT_CASTLE_SQUARE = 1;
    private static final long WHITE_LONG_CASTLE_SQUARE = 5;
    private static final long BLACK_SHORT_CASTLE_SQUARE = 57;
    private static final long BLACK_LONG_CASTLE_SQUARE = 61;

    /**
     * Constructor for CastlingSpecialMove.
     *
     * @param whiteShortCastle Boolean indicating if white short castle is possible.
     * @param whiteLongCastle  Boolean indicating if white long castle is possible.
     * @param blackShortCastle Boolean indicating if black short castle is possible.
     * @param blackLongCastle  Boolean indicating if black long castle is possible.
     */
    public CastlingSpecialMove(boolean whiteShortCastle, boolean whiteLongCastle, boolean blackShortCastle,
                               boolean blackLongCastle) {
        this.whiteShortCastle = whiteShortCastle;
        this.whiteLongCastle = whiteLongCastle;
        this.blackShortCastle = blackShortCastle;
        this.blackLongCastle = blackLongCastle;
    }

    /**
     * Updates the castling rights based on the piece movement.
     *
     * @param currentSquare Current square of the piece.
     * @param targetSquare  Target square of the piece.
     * @param pieceToMove   Piece that is moved.
     */
    public void updateCastlingRights(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        // If a rook moved from its initial position, disable that rook side castling
        if (pieceToMove instanceof Rook) {
            switch (currentSquare) {
                case INITIAL_WHITE_ROOK_SQUARE_SHORT -> whiteShortCastle = false;
                case INITIAL_WHITE_ROOK_SQUARE_LONG -> whiteLongCastle = false;
                case INITIAL_BLACK_ROOK_SQUARE_SHORT -> blackShortCastle = false;
                case INITIAL_BLACK_ROOK_SQUARE_LONG -> blackLongCastle = false;
            }
        }

        // If the king moved, disable all of its castling rights
        if (pieceToMove instanceof King) {
            if (currentSquare == INITIAL_WHITE_KING_SQUARE) {
                whiteShortCastle = false;
                whiteLongCastle = false;
            }
            if (currentSquare == INITIAL_BLACK_KING_SQUARE) {
                blackShortCastle = false;
                blackLongCastle = false;
            }
        }

        // Check if a piece took an enemy rook to cancel corresponding castling rights
        if (pieceToMove.getColor() == WHITE) {
            if (targetSquare == INITIAL_BLACK_ROOK_SQUARE_LONG)
                blackLongCastle = false;
            if (targetSquare == INITIAL_BLACK_ROOK_SQUARE_SHORT)
                blackShortCastle = false;
        } else {
            if (targetSquare == INITIAL_WHITE_ROOK_SQUARE_LONG)
                whiteLongCastle = false;
            if (targetSquare == INITIAL_WHITE_ROOK_SQUARE_SHORT)
                whiteShortCastle = false;
        }
    }


    /**
     * Executes the castling move.
     *
     * @param currentSquare Current square of the king.
     * @param targetSquare  Target square of the king.
     * @param pieceBoard    Board representing the chess pieces.
     * @param pieceList     List of chess pieces.
     */
    public void execute(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList) {
        Piece king = pieceBoard[currentSquare];
        int rookPosition, rookTargetPosition;
        // If current square (the king position) is larger than its target square, it's short castling
        if (currentSquare > targetSquare) {
            rookPosition = INITIAL_WHITE_ROOK_SQUARE_SHORT;
            rookTargetPosition = targetSquare + SHORT_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE;
        } else {
            rookPosition = INITIAL_WHITE_ROOK_SQUARE_LONG;
            rookTargetPosition = targetSquare + LONG_CASTLE_ROOK_OFFSET_FROM_TARGET_SQUARE;
        }
        // Check if an offset needs to be added; rook position is the white rook position, so if black, an offset is needed
        int offset = king.getColor() ? 0 : BLACK_CASTLING_SQUARE_OFFSET;

        // Update piece positions for king and rook
        GameLogicUtilities.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);
        GameLogicUtilities.updatePiecePosition((byte) rookTargetPosition, (byte) (rookPosition + offset),
                pieceBoard, pieceList);
    }


    /**
     * Generates possible special moves for the king, that is castling.
     *
     * @param piece          The king piece.
     * @param enemyMovement  Bitboard representing enemy movements.
     * @param piecesBitBoard Bitboard representing all pieces on the board.
     * @return Bitboard representing possible castling moves.
     */
    public long getMoves(Piece piece, long enemyMovement, long piecesBitBoard) {
        long specialMoves = 0;
        // Check if the white king can short and long castle
        if (piece.getColor() == WHITE) {
            if (whiteShortCastle && checkShortCastling(piecesBitBoard, enemyMovement, 0))
                specialMoves |= SHORT_CASTLING_SQUARE_BITBOARD;

            if (whiteLongCastle && checkLongCastling(piecesBitBoard, enemyMovement, 0))
                specialMoves |= LONG_CASTLING_SQUARE_BITBOARD;
        } else {
            // Check if the black king can short and long castle
            if (blackShortCastle && checkShortCastling(piecesBitBoard, enemyMovement, BLACK_CASTLING_SQUARE_OFFSET))
                specialMoves |= SHORT_CASTLING_SQUARE_BITBOARD << BLACK_CASTLING_SQUARE_OFFSET;

            if (blackLongCastle && checkLongCastling(piecesBitBoard, enemyMovement, BLACK_CASTLING_SQUARE_OFFSET))
                specialMoves |= LONG_CASTLING_SQUARE_BITBOARD << BLACK_CASTLING_SQUARE_OFFSET;
        }
        return specialMoves;
    }


    /**
     * Checks that the square that should be empty for short castling are empty
     * and the square that need to be threatened
     * are not threatened. Offset the square if it's black to the last rank.
     *
     * @param piecesBitBoard        Bitboard representing all pieces on the board.
     * @param enemyMovementBitBoard Bitboard representing enemy movements.
     * @param offset                Offset for black side castling.
     * @return True if short castling conditions are met, false otherwise.
     */
    private boolean checkShortCastling(long piecesBitBoard, long enemyMovementBitBoard, int offset) {
        return (piecesBitBoard & SHORT_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD << offset) == 0
                && (SHORT_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD << offset & enemyMovementBitBoard) == 0;
    }

    /**
     * Checks that the square that should be empty for long castling
     * are empty and the king's path is not threatened.
     * Offset the square if it's black to the last rank.
     *
     * @param piecesBitBoard        Bitboard representing all pieces on the board.
     * @param enemyMovementBitBoard Bitboard representing enemy movements.
     * @param offset                Offset for black side castling.
     * @return True if long castling conditions are met, false otherwise.
     */
    private boolean checkLongCastling(long piecesBitBoard, long enemyMovementBitBoard, int offset) {
        return (piecesBitBoard & LONG_CASTLE_SHOULD_BE_EMPTY_SQUARE_BITBOARD << offset) == 0
                && (LONG_CASTLE_SHOULD_BE_NOT_ATTACKED_SQUARE_BITBOARD << offset & enemyMovementBitBoard) == 0;

    }

    /**
     * Returns if the target square is a square of castling.
     *
     * @param targetSquare Target square to check.
     * @return True if the target square is a square of castling, false otherwise.
     */
    public boolean isCastlingMove(byte targetSquare) {
        return (targetSquare == WHITE_SHORT_CASTLE_SQUARE && whiteShortCastle) ||
                (targetSquare == WHITE_LONG_CASTLE_SQUARE && whiteLongCastle) ||
                (targetSquare == BLACK_SHORT_CASTLE_SQUARE && blackShortCastle) ||
                (targetSquare == BLACK_LONG_CASTLE_SQUARE && blackLongCastle);
    }

}
