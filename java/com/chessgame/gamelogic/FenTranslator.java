package com.chessgame.gamelogic;

import com.chessgame.gamelogic.pieces.*;

import java.util.LinkedList;

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
public class FenTranslator {

    // Standard FEN representing the initial chessboard setup
    private static final String CLASSIC_START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";

    // Constants representing the characters used in FEN notation
    private static final int KING = 'k';
    private static final int QUEEN = 'q';
    private static final int ROOK = 'r';
    private static final int KNIGHT = 'n';
    private static final int BISHOP = 'b';
    private static final int PAWN = 'p';
    private static final int UPPER_CASE_OFFSET = 32;

    private static final char EMPTY = '-';
    private static final char WHITE_TURN = 'w';
    private static final String SHORT_CASTLE_WHITE = "K";
    private static final String LONG_CASTLE_WHITE = "Q";
    private static final String SHORT_CASTLE_BLACK = "q";
    private static final String LONG_CASTLE_BLACK = "k";

    // Fields to store extracted FEN information
    private final String fenStringValue;
    private boolean whiteTurnToPlay;
    private boolean whiteShortCastle;
    private boolean whiteLongCastle;
    private boolean blackShortCastle;
    private boolean blackLongCastle;
    private byte enPassantTargetSquare;
    private final LinkedList<Piece> pieceList = new LinkedList<>();

    /**
     * Constructor that initializes a FenTranslator object using the classical chess starting position as its FEN.
     * It translates the FEN and extracts all the relevant information.
     */
    public FenTranslator() {
        fenStringValue = CLASSIC_START_FEN;
        translateFen();
    }

    /**
     * Constructor that initializes a FenTranslator object using a provided FEN string.
     * It assumes the provided FEN string is valid and translates it, extracting all the relevant information.
     *
     * @param fenStringValue The FEN string representing a particular board position.
     */
    public FenTranslator(String fenStringValue) {
        this.fenStringValue = fenStringValue;
        translateFen();
    }


    /**
     * Translates the FEN string and extracts all the relevant information from it.
     * It sequentially calls methods to extract piece placement, determine player turn,
     * extract castling rights, and extract en passant target square.
     */
    private void translateFen() {
        int indexPosition = 0;
        indexPosition = extractPiecePlacement(indexPosition);
        whiteTurnToPlay = fenStringValue.charAt(indexPosition) == WHITE_TURN;
        indexPosition = indexPosition + 2; // Skip space and move to castling rights section
        indexPosition = extractCastling(indexPosition);
        extractEnPassant(indexPosition);
    }


    /**
     * Extracts the value of the target en passant square from the FEN string.
     * If no en passant target square is specified, the value remains uninitialized (0).
     *
     * @param indexPosition The index position indicating the start of the en passant section in the FEN string.
     */
    private void extractEnPassant(int indexPosition) {
        if (fenStringValue.charAt(indexPosition) != EMPTY) {
            // Convert chess square (e.g., c3, a4, etc.) to numeric square
            int column = fenStringValue.charAt(indexPosition++) - 'h';
            int row = Character.getNumericValue(fenStringValue.charAt(indexPosition));
            // Calculate the numerical representation of the en passant target square
            enPassantTargetSquare = (byte) (column + (row * GameLogicUtilities.BOARD_EDGE_SIZE));
        }
    }


    /**
     * Extracts the castling rights for each player from the FEN string.
     * It checks the substring representing castling rights and sets corresponding boolean variables.
     * The method returns the index position after the castling rights section in the FEN string.
     *
     * @param indexPosition The index position indicating the start of the castling rights section in the FEN string.
     * @return The index position after the castling rights section.
     */
    private int extractCastling(int indexPosition) {
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
        return ++indexPosition;
    }


    /**
     * Extracts pieces from the FEN string along with their type, color, and position, and saves them as a list.
     * It iterates through the piece placement section of the FEN string, starting from the top-left square (square 64)
     * and moving downwards. Each character in the section represents a piece or empty square, and '/' indicates
     * the end of a row. The method returns the index position after the piece placement section in the FEN string.
     *
     * @param indexPosition The index position indicating the start of the piece placement section in the FEN string.
     * @return The index position after the piece placement section.
     */
    private int extractPiecePlacement(int indexPosition) {
        int square = GameLogicUtilities.BOARD_SIZE - 1;

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
                    insertFenCharIntoBitBoards(currChar, (byte) square, GameLogicUtilities.WHITE);
                else
                    insertFenCharIntoBitBoards(currChar, (byte) square, GameLogicUtilities.BLACK);
                square--;
            }
            indexPosition++;
        }
        return ++indexPosition;
    }


    /**
     * Adds the current piece to the list, with the corresponding type, square, and color.
     * It determines the type of the piece based on the provided FEN type and whether it's uppercase (white) or lowercase (black).
     * The method creates a new instance of the corresponding Piece subclass and adds it to the piece list.
     *
     * @param fenType The FEN representation of the piece type (e.g., 'K' for white king, 'k' for black king).
     * @param square  The square on the chessboard where the piece is located.
     * @param color   The color of the piece (true for white, false for black).
     */
    private void insertFenCharIntoBitBoards(int fenType, byte square, boolean color) {
        Piece currentPiece = null;
        // If the piece is white, convert uppercase FEN type to lowercase to match the piece representation
        fenType = color ? fenType + UPPER_CASE_OFFSET : fenType;
        // Determine the type of piece based on the FEN type and create a new instance of the corresponding subclass
        switch (fenType) {
            case KING -> currentPiece = new King(square, color);
            case QUEEN -> currentPiece = new Queen(square, color);
            case ROOK -> currentPiece = new Rook(square, color);
            case BISHOP -> currentPiece = new Bishop(square, color);
            case KNIGHT -> currentPiece = new Knight(square, color);
            case PAWN -> currentPiece = new Pawn(square, color);
        }
        // Add the created piece to the piece list
        pieceList.add(currentPiece);
    }


    /**
     * Checks if it is white's turn to play.
     *
     * @return true if it is white's turn to play, false otherwise.
     */
    public boolean isWhiteTurnToPlay() {
        return whiteTurnToPlay;
    }

    /**
     * Checks if white can perform a short castling.
     *
     * @return true if white can perform a short castling, false otherwise.
     */
    public boolean canWhiteShortCastle() {
        return whiteShortCastle;
    }

    /**
     * Checks if white can perform a long castling.
     *
     * @return true if white can perform a long castling, false otherwise.
     */
    public boolean canWhiteLongCastle() {
        return whiteLongCastle;
    }

    /**
     * Checks if black can perform a short castling.
     *
     * @return true if black can perform a short castling, false otherwise.
     */
    public boolean canBlackShortCastle() {
        return blackShortCastle;
    }

    /**
     * Checks if black can perform a long castling.
     *
     * @return true if black can perform a long castling, false otherwise.
     */
    public boolean canBlackLongCastle() {
        return blackLongCastle;
    }

    /**
     * Retrieves the en passant target square.
     *
     * @return The en passant target square as a byte value.
     */
    public byte getEnPassantSquareToCapture() {
        return enPassantTargetSquare;
    }

    /**
     * Retrieves the list of pieces extracted from the FEN string.
     *
     * @return The list of pieces on the chessboard.
     */
    public LinkedList<Piece> getPieceList() {
        return pieceList;
    }


}

