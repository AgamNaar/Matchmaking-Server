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
public abstract class Piece implements Cloneable {
    private byte square;
    private final boolean color;

    // Static fields to hold references to PieceMovement and PieceThreateningLine instances
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

    public byte getSquare() {
        return square;
    }

    public void setSquare(byte square) {
        this.square = square;
    }

    public boolean getColor() {
        return color;
    }

    @Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public PieceMovement getPieceMovement() {
        return pieceMovement;
    }
}
