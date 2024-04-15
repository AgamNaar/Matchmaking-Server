package com.chessgame.gamelogic.specialmoves;

import com.chessgame.gamelogic.ChessGame;
import com.chessgame.gamelogic.GameLogicUtilities;
import com.chessgame.gamelogic.pieces.*;

import java.util.LinkedList;

import static com.chessgame.gamelogic.GameLogicUtilities.BLACK_PAWN_MOVE_OFFSET;
import static com.chessgame.gamelogic.GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET;

/**
 * Class responsible for updating, executing, and determining the special moves a pawn can perform:
 * en passant and promotion.
 */
public class PawnSpecialMoves {

    private byte enPassantTargetSquare;

    // Constants representing special conditions and positions
    private static final int NO_EN_PASSANT_TARGET_SQUARE = -1;
    private static final int PAWN_DOUBLE_MOVE_OFFSET = 16;
    private static final byte LAST_ROW_WHITE = 55;
    private static final byte LAST_ROW_BLACK = 8;

    /**
     * Constructs a PawnSpecialMoves object with the given en passant target square.
     *
     * @param enPassantTargetSquare The en passant target square.
     */
    public PawnSpecialMoves(byte enPassantTargetSquare) {
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    /**
     * Determines if the piece can perform en passant if the target square of en passant
     * is one of its possible attack squares.
     * Checks that en-passant won't expose king to check.
     *
     * @param piece              The piece.
     * @param pieceList          List of chess pieces.
     * @param allPieceBitboard   Bitboard representing all pieces on the board.
     * @param colorOfPlayersTurn Boolean indicating the color of the player's turn.
     * @param king               The king piece.
     * @return Bitboard representing possible en passant moves.
     */
    public long getMoves(Piece piece, LinkedList<Piece> pieceList, long allPieceBitboard,
                         boolean colorOfPlayersTurn, Piece king) {
        long enPassantTargetSquareBitBoard = 0;

        // If the move exposes the king to a rook's check, it's not valid, return 0
        if (doesExposeToRookCheck(piece.getSquare(), pieceList, allPieceBitboard, colorOfPlayersTurn, king))
            return 0;

        if (enPassantTargetSquare != NO_EN_PASSANT_TARGET_SQUARE)
            enPassantTargetSquareBitBoard = GameLogicUtilities.squareAsBitBoard(enPassantTargetSquare);

        long pawnAttackSquare = piece.getPieceMovement().getPawnCaptureSquare(piece.getColor(), piece.getSquare());
        return pawnAttackSquare & enPassantTargetSquareBitBoard;
    }


    /**
     * Updates the en passant target square.
     *
     * @param currentSquare Current square of the piece.
     * @param targetSquare  Target square of the piece.
     * @param pieceToMove   Piece that is moved.
     */
    public void updateEnPassantSquare(byte currentSquare, byte targetSquare, Piece pieceToMove) {
        byte movementOffset = pieceToMove.getColor() ? WHITE_PAWN_MOVE_OFFSET : BLACK_PAWN_MOVE_OFFSET;
        // If a pawn has moved, check if it moved 2 squares, meaning an enemy pawn can take it using en passant
        if (pieceToMove instanceof Pawn && Math.abs(targetSquare - currentSquare) == PAWN_DOUBLE_MOVE_OFFSET)
            enPassantTargetSquare = (byte) (currentSquare + movementOffset);
        else
            enPassantTargetSquare = NO_EN_PASSANT_TARGET_SQUARE;
    }


    /**
     * Executes the move and updates the board and list of pieces based
     * on the current square and target square of the piece.
     * Handles en passant and promotion moves.
     *
     * @param currentSquare          Current square of the piece.
     * @param targetSquare           Target square of the piece.
     * @param pieceBoard             Board representing the chess pieces.
     * @param pieceList              List of chess pieces.
     * @param typeOfPieceToPromoteTo Type of piece to promote to (if promotion move).
     */
    public void execute(byte currentSquare, byte targetSquare, Piece[] pieceBoard, LinkedList<Piece> pieceList,
                        char typeOfPieceToPromoteTo) {

        if (targetSquare == enPassantTargetSquare)
            executeEnPassant(currentSquare, targetSquare, pieceBoard, pieceList);
        else
            executePromotion(currentSquare, targetSquare, pieceBoard, pieceList, typeOfPieceToPromoteTo);
    }


    /**
     * Executes the en passant move given the current square of the pawn and the target square.
     * Removes the captured pawn from the board and list of pieces.
     *
     * @param currentSquare Current square of the pawn.
     * @param targetSquare  Target square of the en passant move.
     * @param pieceBoard    Board representing the chess pieces.
     * @param pieceList     List of chess pieces.
     */
    private void executeEnPassant(byte currentSquare, byte targetSquare, Piece[] pieceBoard,
                                  LinkedList<Piece> pieceList) {

        // The target en passant square + 1 square in the direction the pawn went is where the pawn is now
        byte enPassantPawnToCaptureSquare = (byte) (targetSquare + (pieceBoard[currentSquare].getColor()
                ? BLACK_PAWN_MOVE_OFFSET : WHITE_PAWN_MOVE_OFFSET));

        GameLogicUtilities.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);
        // Remove the captured pawn from the list and board
        pieceList.remove(pieceBoard[enPassantPawnToCaptureSquare]);
        pieceBoard[enPassantPawnToCaptureSquare] = null;
    }


    /**
     * Executes a promotion move given the current square, target square, piece board, piece list, and type of piece to promote to.
     * Removes the piece on the target square and replaces the piece on the current square with the promoted piece.
     *
     * @param currentSquare          Current square of the piece.
     * @param targetSquare           Target square of the promotion move.
     * @param pieceBoard             Board representing the chess pieces.
     * @param pieceList              List of chess pieces.
     * @param typeOfPieceToPromoteTo Type of piece to promote to.
     */
    private void executePromotion(byte currentSquare, byte targetSquare, Piece[] pieceBoard,
                                  LinkedList<Piece> pieceList, char typeOfPieceToPromoteTo) {

        boolean colorOfPiece = pieceBoard[currentSquare].getColor();
        // Remove piece on target square and replace piece on current square with the promoted piece
        pieceList.remove(pieceBoard[currentSquare]);
        pieceList.remove(pieceBoard[targetSquare]);
        pieceBoard[currentSquare] = null;
        Piece newPiece = createPieceForPromotion(targetSquare, colorOfPiece, typeOfPieceToPromoteTo);
        pieceBoard[targetSquare] = newPiece;
        pieceList.add(newPiece);
    }


    /**
     * Creates a new piece for promotion based on the type of piece to promote to.
     *
     * @param targetSquare           Target square for the new piece.
     * @param colorOfPiece           Color of the new piece.
     * @param typeOfPieceToPromoteTo Type of piece to promote to.
     * @return The newly created piece.
     */
    private Piece createPieceForPromotion(byte targetSquare, boolean colorOfPiece, char typeOfPieceToPromoteTo) {
        Piece piece;
        // Create a piece according to the type of piece
        if (ChessGame.PROMOTE_TO_QUEEN == typeOfPieceToPromoteTo)
            piece = new Queen(targetSquare, colorOfPiece);
        else if (ChessGame.PROMOTE_TO_ROOK == typeOfPieceToPromoteTo)
            piece = new Rook(targetSquare, colorOfPiece);
        else if (ChessGame.PROMOTE_TO_BISHOP == typeOfPieceToPromoteTo)
            piece = new Bishop(targetSquare, colorOfPiece);
        else if (ChessGame.PROMOTE_TO_KNIGHT == typeOfPieceToPromoteTo)
            piece = new Knight(targetSquare, colorOfPiece);
        else // default to queen
            piece = new Queen(targetSquare, colorOfPiece);

        return piece;
    }


    /**
     * Checks if the target square is either the en-passant square or a promotion square.
     *
     * @param targetSquare Target square to check.
     * @return True if the target square is a special move square, false otherwise.
     */
    public boolean isSpecialMove(byte targetSquare) {
        return targetSquare == enPassantTargetSquare
                || targetSquare < LAST_ROW_BLACK
                || targetSquare > LAST_ROW_WHITE;
    }

    /**
     * Returns the en passant square.
     *
     * @return The en passant square.
     */
    public byte getEnPassantSquare() {
        return enPassantTargetSquare;
    }

    /**
     * Checks if performing en passant would expose the king to a check from a rook.
     * En-passant can cause a special situating where it will expose the king to a check from a rook.
     *
     * @param currentSquare      Current square of the pawn.
     * @param pieceList          List of chess pieces.
     * @param allPieceBitboard   Bitboard representing all pieces on the board.
     * @param colorOfPlayersTurn Color of the player's turn.
     * @param myKing             The king piece.
     * @return True if performing en passant would expose the king to a check from a rook, false otherwise.
     */
    private boolean doesExposeToRookCheck(byte currentSquare, LinkedList<Piece> pieceList,
                                          long allPieceBitboard, boolean colorOfPlayersTurn, Piece myKing) {

        long rowMask = 0xffL << (GameLogicUtilities.getRowOfSquare(currentSquare) * 8);
        long currentPosition;

        // If the king isn't in the same row as the pawn, it can't be exposed to check
        if ((myKing.getSquareAsBitBoard() & rowMask) == 0)
            return false;

        // Check if one of the enemy rooks is on the same row as the king as the pawn
        for (Piece piece : pieceList) {
            if (piece instanceof Rook && piece.getColor() != colorOfPlayersTurn
                    && (piece.getSquareAsBitBoard() & rowMask) != 0) {

                int counter = 0, offset = myKing.getSquare() > piece.getSquare() ? -1 : 1;
                currentPosition = GameLogicUtilities.shiftNumberLeft(myKing.getSquareAsBitBoard(), offset);

                // Check how many pieces there are between the king and the rook
                while (currentPosition != piece.getSquareAsBitBoard()) {
                    if ((currentPosition & allPieceBitboard) != 0)
                        counter++;

                    currentPosition = GameLogicUtilities.shiftNumberLeft(currentPosition, offset);
                }
                // If there are only 2 pieces between the king and the rook, return true
                if (counter == 2)
                    return true;
            }
        }
        return false;
    }

}
