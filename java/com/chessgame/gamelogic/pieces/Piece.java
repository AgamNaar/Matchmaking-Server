package com.chessgame.gamelogic.pieces;

import com.chessgame.gamelogic.GameLogicUtilities;
import com.chessgame.gamelogic.pieces.logic.PieceMovement;
import com.chessgame.gamelogic.pieces.logic.PieceThreateningLine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class representing a chess piece.
 * Has two attributes:
 * Color: color of the piece (true for white, false for black)
 * Square: square of the piece on the board
 */
public abstract class Piece {
    private byte square;
    private final boolean color;

    protected PieceMovement pieceMovement;
    protected PieceThreateningLine threateningLine;

    /**
     * Initializes the Piece instance with PieceMovement and PieceThreateningLine instances.
     *
     * @param pieceMovement   the PieceMovement instance
     * @param threateningLine the PieceThreateningLine instance
     */
    @PostConstruct
    @Autowired
    public void initialize(PieceMovement pieceMovement, PieceThreateningLine threateningLine) {
        this.pieceMovement = pieceMovement;
        this.threateningLine = threateningLine;
    }

    /**
     * Constructor for a Piece object.
     *
     * @param square the square on which the piece is located
     * @param color  the color of the piece (true for white, false for black)
     */
    public Piece(byte square, boolean color) {
        this.square = square;
        this.color = color;
    }

    /**
     * Abstract method to calculate the legal moves for the piece.
     * Given a bitboard representing all pieces on the board and a bitboard representing pieces of the same color,
     * this method returns a bitboard representing all the possible moves the piece can make.
     * Note that these moves are returned without checking their legality.
     *
     * @param allPiecesBitBoard       bitboard representing all pieces on the board
     * @param sameColorPiecesBitBoard bitboard representing pieces of the same color
     * @return bitboard representing legal moves for the piece
     */
    public abstract long getMovesAsBitBoard(long allPiecesBitBoard, long sameColorPiecesBitBoard);


    /**
     * Abstract method to calculate the threatening lines for the piece.
     * The threatening line represents the squares on the chessboard that
     * are under attack by the piece, and x-rayed attack squares.
     * considering the piece current position,
     * the position of the enemy king, and the overall board state.
     *
     * @param enemyKingSquare the square of the enemy king
     * @param boardBitBoard   bitboard representing the board state
     * @return bitboard representing the threatening lines for the piece
     */
    public abstract long getThreatLines(byte enemyKingSquare, Long boardBitBoard);

    /**
     * Returns the position of the piece as a bitboard.
     *
     * @return bitboard representing the position of the piece
     */
    public long getSquareAsBitBoard() {
        return GameLogicUtilities.squareAsBitBoard(square);
    }

    /**
     * Retrieves the square where the piece is located.
     *
     * @return The square where the piece is located as a byte value.
     */
    public byte getSquare() {
        return square;
    }

    /**
     * Sets the square where the piece is located.
     *
     * @param square The square to set where the piece is located.
     */
    public void setSquare(byte square) {
        this.square = square;
    }

    /**
     * Retrieves the color of the piece.
     *
     * @return The color of the piece (true for white, false for black).
     */
    public boolean getColor() {
        return color;
    }

    /**
     * Retrieves the piece movement details.
     *
     * @return The piece movement details.
     */
    public PieceMovement getPieceMovement() {
        return pieceMovement;
    }

}
