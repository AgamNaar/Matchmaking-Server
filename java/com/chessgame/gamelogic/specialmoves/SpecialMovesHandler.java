package com.chessgame.gamelogic.specialmoves;

import com.chessgame.gamelogic.pieces.King;
import com.chessgame.gamelogic.pieces.Pawn;
import com.chessgame.gamelogic.pieces.Piece;

import java.util.LinkedList;

/**
 * Class responsible for handling all the special moves in a chess game, including castling and en passant.
 * It updates the special moves parameters (En-passant square, castling rights).
 * Provides methods to retrieve, and execute special moves.
 * Also check if a move is a special mov.e
 */
public class SpecialMovesHandler {

    private final PawnSpecialMoves pawnSpecialMoves;
    private final CastlingSpecialMove castlingSpecialMove;

    /**
     * Constructs a SpecialMovesHandler object with the initial state of castling and en passant.
     *
     * @param whiteShortCastle      Flag indicating if white can castle king side.
     * @param whiteLongCastle       Flag indicating if white can castle queen side.
     * @param blackShortCastle      Flag indicating if black can castle king side.
     * @param blackLongCastle       Flag indicating if black can castle queen side.
     * @param enPassantTargetSquare En passant target square.
     */
    public SpecialMovesHandler(boolean whiteShortCastle, boolean whiteLongCastle,
                               boolean blackShortCastle, boolean blackLongCastle, byte enPassantTargetSquare) {

        pawnSpecialMoves = new PawnSpecialMoves(enPassantTargetSquare);
        castlingSpecialMove = new CastlingSpecialMove(whiteShortCastle, whiteLongCastle,
                blackShortCastle, blackLongCastle);
    }

    /**
     * Updates the special moves based on the piece that has been moved and the square it moved to.
     *
     * @param currentSquare Current square of the piece.
     * @param targetSquare  Target square of the piece.
     * @param pieceToMove   Piece that has been moved.
     */
    public void updateSpecialMoves(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        castlingSpecialMove.updateCastlingRights(currentSquare, targetSquare, pieceToMove);
        pawnSpecialMoves.updateEnPassantSquare(currentSquare, targetSquare, pieceToMove);
    }

    /**
     * Retrieves the special moves available for a piece.
     *
     * @param piece              Piece to check for special moves.
     * @param enemyMovement      Bitboard representing possible enemy movements.
     * @param piecesBitBoard     Bitboard representing positions of all pieces.
     * @param pieceList          List of pieces on the board.
     * @param colorOfPlayersTurn Color of the player's turn.
     * @param king               King piece.
     * @return Bitboard representing available special moves.
     */
    public long getSpecialMoves(Piece piece, long enemyMovement, long piecesBitBoard, LinkedList<Piece> pieceList,
                                boolean colorOfPlayersTurn, Piece king) {

        if (piece instanceof King)
            return castlingSpecialMove.getMoves(piece, enemyMovement, piecesBitBoard);

        if (piece instanceof Pawn)
            return pawnSpecialMoves.getMoves(piece, pieceList, piecesBitBoard, colorOfPlayersTurn, king);

        return 0;
    }

    /**
     * Executes a special move.
     *
     * @param currentSquare          Current square of the piece.
     * @param targetSquare           Target square of the special move.
     * @param pieceList              List of pieces on the board.
     * @param pieceBoard             Board representing the chess pieces.
     * @param typeOfPieceToPromoteTo Type of piece to promote to (if applicable).
     */
    public void executeSpecialMove(byte currentSquare, byte targetSquare, LinkedList<Piece> pieceList,
                                   Piece[] pieceBoard, char typeOfPieceToPromoteTo) {

        if (pieceBoard[currentSquare] instanceof King)
            castlingSpecialMove.execute(currentSquare, targetSquare, pieceBoard, pieceList);
        else
            pawnSpecialMoves.execute(currentSquare, targetSquare, pieceBoard, pieceList, typeOfPieceToPromoteTo);
    }

    /**
     * Checks if the target square is a special move square.
     *
     * @param targetSquare Target square to check.
     * @param pieceToMove  Piece that is moving.
     * @return True if the target square is a special move square, false otherwise.
     */
    public boolean isSpecialMove(byte targetSquare, Piece pieceToMove) {
        if (pieceToMove instanceof Pawn)
            return pawnSpecialMoves.isSpecialMove(targetSquare);

        if (pieceToMove instanceof King)
            return castlingSpecialMove.isCastlingMove(targetSquare);

        return false;
    }

    /**
     * Returns the en passant target square.
     *
     * @return The en passant target square.
     */
    public byte getEnPassantSquare() {
        return pawnSpecialMoves.getEnPassantSquare();
    }
}
