package com.chessgame.gamelogic;

import com.chessgame.ChessGame;
import com.chessgame.gamelogic.pieces.*;
import com.chessgame.gamelogic.specialmoves.SpecialMovesHandler;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

import static com.chessgame.gamelogic.specialmoves.PawnSpecialMoves.NO_EN_PASSANT_TARGET_SQUARE;

/**
 * Forsyth–Edwards Notation (FEN) is a standard notation for describing a particular board position of a chess game.
 * The purpose of FEN is to provide all the necessary information to restart a game from a particular position.
 * This class translates a FEN string and extracts all the relevant information from it:
 * 1. Board setup - saved as a list of pieces.
 * 2. Player turn - saved as a boolean (true for white's turn).
 * 3. Castling rights - saved as booleans for each castling type.
 * 4. En passant target square.
 * 5. Half move clock (not extracted because not implemented).
 * 6. Full move clock (not extracted because not implemented).
 */
@Component
public class FenTranslator {

    // Standard FEN representing the initial chessboard setup
    private static final String CLASSIC_FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";

    // Constants representing the characters used in FEN notation
    private static final int KING = 'k';
    private static final int QUEEN = 'q';
    private static final int ROOK = 'r';
    private static final int KNIGHT = 'n';
    private static final int BISHOP = 'b';
    private static final int PAWN = 'p';

    private static final char EMPTY = '-';
    private static final char WHITE_TURN = 'w';
    private static final String SHORT_CASTLE_WHITE = "K";
    private static final String LONG_CASTLE_WHITE = "Q";
    private static final String SHORT_CASTLE_BLACK = "q";
    private static final String LONG_CASTLE_BLACK = "k";

    /**
     * Translates the FEN string and extracts all the relevant information from it.
     * It sequentially calls methods to extract piece placement, determine player turn,
     * extract castling rights, and extract en passant target square.
     */
    public void translateFen(ChessGame game, String fenStringValue) {
        if (fenStringValue == null || fenStringValue.isEmpty())
            fenStringValue = CLASSIC_FEN_START;
        int indexPosition = 0;
        indexPosition = extractPiecePlacement(game, fenStringValue, indexPosition);
        game.setColorOfPlayersTurn(fenStringValue.charAt(indexPosition) == WHITE_TURN);
        indexPosition = indexPosition + 2; // Skip space and move to castling rights section
        extractSpecialMoves(game, fenStringValue, indexPosition);
    }

    /**
     * Extracts pieces from the FEN string along with their type, color,
     * and position, and save them in the chess game.
     * It iterates through the piece placement section of the FEN string,
     * starting from the top-left square (square 64)
     * and moving downwards. Each character in the section represents a piece or empty square,
     * and '/' indicates the end of a row.
     * Each time it adds a new piece to the list accordingly.
     * The method returns the index position after the piece placement section in the FEN string.
     *
     * @param game           The ChessGame object to which the extracted pieces will be added.
     * @param fenStringValue The FEN string representing the board state.
     * @param indexPosition  The index position indicating the start of the piece placement section in the FEN string.
     * @return The index position after the piece placement section.
     */
    private int extractPiecePlacement(ChessGame game, String fenStringValue, int indexPosition) {
        int square = GameLogicUtilities.BOARD_SIZE - 1;
        LinkedList<Piece> pieceList = new LinkedList<>();
        // Iterate through the entire section of the FEN that represents the piece positions
        // It starts with square 64 (top-left square), and each character indicates a piece or an empty square.
        // A number represents an empty square, and '/' indicates the end of a row.
        // After the counter runs from 63 to 0, all the pieces are extracted.
        while (square > -1) {
            char currChar = fenStringValue.charAt(indexPosition);
            // If it's a digit, skip that number of squares
            if (Character.isDigit(currChar))
                square = square - Character.getNumericValue(currChar);
            else if (currChar != '/') {
                // If it's a piece, determine its FEN type, and if it's uppercase, it's white; otherwise, it's black
                if (Character.isUpperCase(currChar))
                    pieceList.add(createPieceFromFenChar(currChar, (byte) square, GameLogicUtilities.WHITE));
                else
                    pieceList.add(createPieceFromFenChar(currChar, (byte) square, GameLogicUtilities.BLACK));
                square--;
            }
            indexPosition++;
        }
        game.setPieceList(pieceList);
        return ++indexPosition;
    }

    /**
     * Creates and return a chess piece object from a FEN (Forsyth-Edwards Notation) character representation.
     * It determines the type of the piece based on the provided FEN type
     * and whether it's uppercase (white) or lowercase (black).
     *
     * @param fenChar The FEN character representing the type of chess piece.
     * @param square  The square index on the chessboard where the piece is located.
     * @param color   The color of the chess piece, true for white, false for black.
     * @return A Piece object corresponding to the given FEN character and color.
     */
    private Piece createPieceFromFenChar(char fenChar, byte square, boolean color) {
        Piece newPiece = null;
        // If the piece is white, convert uppercase FEN char to lowercase to match the piece representation
        int fenType = color ? Character.toLowerCase(fenChar) : fenChar;
        // Determine the type of piece based on the FEN char and create a new instance of the corresponding subclass
        switch (fenType) {
            case KING -> newPiece = new King(square, color);
            case QUEEN -> newPiece = new Queen(square, color);
            case ROOK -> newPiece = new Rook(square, color);
            case BISHOP -> newPiece = new Bishop(square, color);
            case KNIGHT -> newPiece = new Knight(square, color);
            case PAWN -> newPiece = new Pawn(square, color);
        }
        return newPiece;
    }


    /**
     * Extracts special moves information from the FEN (Forsyth-Edwards Notation) string and sets it in the chess game.
     * Extract the castling rights, and the en-passant square.
     *
     * @param game           The ChessGame object to set the extracted special moves information.
     * @param fenStringValue The FEN string representing the current state of the chess game.
     * @param indexPosition  The index position in the FEN string from where to start extracting special moves.
     */
    private void extractSpecialMoves(ChessGame game, String fenStringValue, int indexPosition) {
        boolean whiteShortCastle = false, whiteLongCastle = false, blackShortCastle = false, blackLongCastle = false;
        byte enPassantTargetSquare;
        StringBuilder subFenString = new StringBuilder();
        if (fenStringValue.charAt(indexPosition) != EMPTY) {
            // Get the substring containing the values of castling rights
            while (fenStringValue.charAt(indexPosition) != ' ') {
                subFenString.append(fenStringValue.charAt(indexPosition));
                indexPosition++;
            }
            // Check each letter (K, Q, k, q) representing a castling right and set corresponding booleans
            whiteShortCastle = subFenString.toString().contains(SHORT_CASTLE_WHITE);
            whiteLongCastle = subFenString.toString().contains(LONG_CASTLE_WHITE);
            blackShortCastle = subFenString.toString().contains(SHORT_CASTLE_BLACK);
            blackLongCastle = subFenString.toString().contains(LONG_CASTLE_BLACK);
        }
        indexPosition++;
        // Extract the en-passant square
        if (fenStringValue.charAt(indexPosition) != EMPTY) {
            // Convert chess square (e.g., c3, a4, etc.) to numeric square
            int column = fenStringValue.charAt(indexPosition++) - 'h';
            int row = Character.getNumericValue(fenStringValue.charAt(indexPosition));
            // Calculate the numerical representation of the en passant target square
            enPassantTargetSquare = (byte) (column + (row * GameLogicUtilities.BOARD_EDGE_SIZE));
        } else {
            enPassantTargetSquare = NO_EN_PASSANT_TARGET_SQUARE;
        }
        game.setSpecialMovesHandler(new SpecialMovesHandler(whiteShortCastle,
                whiteLongCastle,
                blackShortCastle,
                blackLongCastle,
                enPassantTargetSquare));
    }


}

