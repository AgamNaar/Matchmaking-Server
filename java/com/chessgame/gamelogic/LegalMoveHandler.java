package com.chessgame.gamelogic;

import com.chessgame.gamelogic.pieces.King;
import com.chessgame.gamelogic.pieces.Pawn;
import com.chessgame.gamelogic.pieces.Piece;

import java.util.LinkedList;

import static com.chessgame.gamelogic.GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET;

/**
 * Handles all aspects of making legal moves in a chess game, ensuring that moves do not result in the king walking
 * into check and preventing pieces from moving in ways that expose their own king to check.
 */
public class LegalMoveHandler {

    private final LinkedList<Long> threatLineList = new LinkedList<>();

    /**
     * Given the current square of the piece and the target square, checks if it's a valid move or not.
     *
     * @param currentSquare      The current square index of the piece.
     * @param targetSquare       The target square index where the piece is to be moved.
     * @param pieceBoard         The array representing the game board with pieces.
     * @param colorOfPlayersTurn The color of the player's turn (true for white, false for black).
     * @param bitBoardLegalMoves The bitboard representing legal moves for the player.
     * @return True if the move is valid, otherwise false.
     */
    public boolean isValidMove(byte currentSquare, byte targetSquare, Piece[] pieceBoard, boolean colorOfPlayersTurn,
                               long bitBoardLegalMoves) {
        // Get the piece to move
        Piece pieceToMove = pieceBoard[currentSquare];

        // Check if the piece is of the same color as the player whose turn it is
        if (pieceToMove.getColor() == !colorOfPlayersTurn)
            return false;

        // Check if the target square is a legal move for the piece
        return (bitBoardLegalMoves & GameLogicUtilities.squareAsBitBoard(targetSquare)) != 0;
    }


    /**
     * Given a piece and the bitboard of moves it can do, removes all moves that are illegal, such as moves that
     * would result in the king walking into check or moves that would cause a check.
     *
     * @param bitBoardMoves            The bitboard representing the possible moves of the piece.
     * @param pieceToMove              The piece for which illegal moves are to be removed.
     * @param pieceList                The list of all pieces on the board.
     * @param colorOfPlayersTurn       The color of the player's turn (true for white, false for black).
     * @param allPiecesBitBoard        The bitboard representing all pieces on the board.
     * @param playerTurnPiecesBitBoard The bitboard representing pieces of the player whose turn it is.
     * @param isKPlayerTurnKingChecked A flag indicating if the player's own king is checked.
     * @param enPassantSquare          The en passant square index (-1 if not applicable).
     * @param king                     The king piece of the player whose turn it is.
     * @return The modified bitboard with illegal moves removed.
     */
    public long removeIllegalMoves(long bitBoardMoves, Piece pieceToMove, LinkedList<Piece> pieceList,
                                   boolean colorOfPlayersTurn, long allPiecesBitBoard, long playerTurnPiecesBitBoard,
                                   boolean isKPlayerTurnKingChecked, byte enPassantSquare, Piece king) {

        // Get the bitboard representing the position of the piece and the king
        long piecePositionAsBitBoard = pieceToMove.getSquareAsBitBoard();
        long kingPositionBitBoard = king.getSquareAsBitBoard();

        // Bitboard representing the position of the en passant square (if applicable)
        long enPassantSquareBitBoardPosition = GameLogicUtilities.squareAsBitBoard(enPassantSquare);

        // If the piece is a king, remove all squares that are threatened by enemy pieces
        if (pieceToMove instanceof King) {
            long threatenedSquare = threatenedSquareForKing(pieceList, allPiecesBitBoard, colorOfPlayersTurn, king);
            return bitBoardMoves & ~threatenedSquare;
        }

        // If the player's own king is checked
        if (isKPlayerTurnKingChecked) {
            // Flag to check if en passant needs to be added to one of the threat lines
            boolean flag = pieceToMove instanceof Pawn && enPassantSquare != -1
                    && (enPassantSquareBitBoardPosition & bitBoardMoves) != 0;

            // While king is checked, only moves that can stop all checks are allowed
            for (Long threatLine : threatLineList) {
                // Find the matching threat line to add the en passant square to it
                if (flag && doesNeedToAddEnPassantToThreatLine(threatLine, enPassantSquareBitBoardPosition,
                        colorOfPlayersTurn))
                    threatLine |= enPassantSquareBitBoardPosition;

                // Check if some piece already blocks this threat line; if not, the piece must block it
                if ((threatLine & (playerTurnPiecesBitBoard & ~kingPositionBitBoard)) == 0)
                    bitBoardMoves &= threatLine;

                // If the piece is on a threat line, it must stay on that threat line
                if ((threatLine & piecePositionAsBitBoard) != 0)
                    bitBoardMoves &= threatLine;
            }
        } else {
            // Check if moving a piece won't expose the king to check
            for (Long threatLine : threatLineList) {
                if ((piecePositionAsBitBoard & threatLine) != 0)
                    return bitBoardMoves & threatLine;
            }
        }
        return bitBoardMoves;
    }


    /**
     * Updates the threatening lines for pieces on the board.
     *
     * @param pieceList           The list of all pieces on the board.
     * @param allPiecesBitBoard   The bitboard representing all pieces on the board.
     * @param enemyPiecesBitBoard The bitboard representing enemy pieces.
     * @param colorOfPlayersTurn  The color of the player's turn (true for white, false for black).
     * @param myKing              The king piece of the player whose turn it is.
     */
    public void updateTreatingLines(LinkedList<Piece> pieceList, long allPiecesBitBoard, long enemyPiecesBitBoard,
                                    boolean colorOfPlayersTurn, Piece myKing) {

        // Clear the list of threatening lines
        threatLineList.clear();

        // Iterate over each piece on the board
        for (Piece piece : pieceList) {
            long treatKingLine = 0;

            // If the piece is an enemy piece and has a line of threat, get its threatening line
            if (piece.getColor() != colorOfPlayersTurn)
                treatKingLine = piece.getThreatLines(myKing.getSquare(), allPiecesBitBoard);

            // Add the threatening line if it's not empty, and remove threat lines that intersect with squares
            // occupied by pieces of the same color
            if (treatKingLine != 0 && (treatKingLine & ~piece.getSquareAsBitBoard() & enemyPiecesBitBoard) == 0)
                threatLineList.add(treatKingLine);
        }
    }


    /**
     * Determines if the en passant square needs to be added to the threat line.
     *
     * @param threatLine                      The threatening line of a piece.
     * @param enPassantSquareBitBoardPosition The bitboard position of the en passant square.
     * @param colorOfPlayersTurn              The color of the player's turn (true for white, false for black).
     * @return True if the en passant square needs to be added to the threat line, otherwise false.
     */
    private boolean doesNeedToAddEnPassantToThreatLine(Long threatLine, Long enPassantSquareBitBoardPosition,
                                                       boolean colorOfPlayersTurn) {

        if (colorOfPlayersTurn)
            return (threatLine & ~(enPassantSquareBitBoardPosition >> WHITE_PAWN_MOVE_OFFSET)) == 0;

        return (threatLine & ~(enPassantSquareBitBoardPosition << WHITE_PAWN_MOVE_OFFSET)) == 0;
    }

    /**
     * Returns a bitboard of all the squares that are threatened by the enemy player.
     *
     * @param pieceList          The list of all pieces on the board.
     * @param allPiecesBitBoard  The bitboard representing all pieces on the board.
     * @param colorOfPlayersTurn The color of the player's turn (true for white, false for black).
     * @param king               The king piece of the player whose turn it is.
     * @return The bitboard representing squares threatened by the enemy player.
     */
    private long threatenedSquareForKing(LinkedList<Piece> pieceList, long allPiecesBitBoard,
                                         boolean colorOfPlayersTurn, Piece king) {

        // By removing the king, squares that are threatened beyond him will also be marked
        long kingBitBoardSquare = king.getSquareAsBitBoard();
        long bitBoardWithoutKing = allPiecesBitBoard & ~kingBitBoardSquare;
        long movementBitBoard = 0;

        // For each piece in piece list, add the movement of pieces with the opposite color as the player
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                if (piece instanceof Pawn)
                    movementBitBoard |= ((Pawn) piece).getPawnAttackSquare();
                else
                    // By setting friendly pieces to 0, pieces that are protected are also marked
                    movementBitBoard |= piece.getMovesAsBitBoard(bitBoardWithoutKing, GameLogicUtilities.EMPTY_BOARD);

        return movementBitBoard;
    }

}
