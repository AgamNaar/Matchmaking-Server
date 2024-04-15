package com.chessgame.gamelogic.pieces.logic;

import com.chessgame.gamelogic.GameLogicUtilities;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

import static com.chessgame.gamelogic.GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET;

/**
 * The PieceMovement class manages and computes movements for chess pieces on the game board.
 * It initializes and stores pre-calculated movements efficiently using static arrays and hash maps.
 * Through its initialize() method, it sets up these movements based on piece positioning.
 * Methods within the class calculate valid moves for each piece type,
 * considering piece positions, board layout, and piece colo
 */
@Component
public class PieceMovement {
    private final PieceMovementPreemptiveCalculator preemptiveCalculator;

    // Static arrays to store pre-calculated movements for pieces
    private static final long[] KING_MOVES = new long[GameLogicUtilities.BOARD_SIZE];
    private static final ArrayList<HashMap<Long, Long>> ROOK_MOVES = new ArrayList<>();
    private static final ArrayList<HashMap<Long, Long>> BISHOP_MOVES = new ArrayList<>();
    private static final long[] KNIGHT_MOVES = new long[GameLogicUtilities.BOARD_SIZE];
    private static final long[] WHITE_PAWN_ONLY_MOVES = new long[GameLogicUtilities.BOARD_SIZE];
    private static final long[] WHITE_PAWN_CAPTURE = new long[GameLogicUtilities.BOARD_SIZE];
    private static final long[] BLACK_PAWN_ONLY_MOVES = new long[GameLogicUtilities.BOARD_SIZE];
    private static final long[] BLACK_PAWN_CAPTURE = new long[GameLogicUtilities.BOARD_SIZE];

    /**
     * Constructor for PieceMovement class.
     *
     * @param preemptiveCalculator Instance of PieceMovementPreemptiveCalculator used for calculating piece movements.
     */
    @Autowired
    public PieceMovement(PieceMovementPreemptiveCalculator preemptiveCalculator) {
        this.preemptiveCalculator = preemptiveCalculator;
    }

    /**
     * Initialize the piece movements after bean construction.
     * This method populates pre-calculated movements for various chess pieces.
     */
    @PostConstruct
    public void initialize() {
        // Initialize arrays to store movement data for rooks and bishops
        for (int i = 0; i < GameLogicUtilities.BOARD_SIZE; i++) {
            ROOK_MOVES.add(new HashMap<>());
            BISHOP_MOVES.add(new HashMap<>());
        }

        // Generate pre-calculated movements for the king, knight, and pawns
        preemptiveCalculator.generateKingMoves(KING_MOVES);
        preemptiveCalculator.generateKnightMoves(KNIGHT_MOVES);
        preemptiveCalculator.generatePawnMoves(WHITE_PAWN_ONLY_MOVES, WHITE_PAWN_CAPTURE, GameLogicUtilities.WHITE);
        preemptiveCalculator.generatePawnMoves(BLACK_PAWN_ONLY_MOVES, BLACK_PAWN_CAPTURE, GameLogicUtilities.BLACK);

        // Generate pre-calculated movements for rooks and bishops based on lines
        preemptiveCalculator.generateLinePieceMoves(ROOK_MOVES, BISHOP_MOVES);
    }

    /**
     * Get possible movements for the king.
     *
     * @param piecePosition          Position of the king on the board.
     * @param sameColorPieceBitBoard Bitboard representing positions of pieces of the same color.
     * @return Bitboard representing possible king movements.
     */
    public long getKingMovement(byte piecePosition, long sameColorPieceBitBoard) {
        long moves = KING_MOVES[piecePosition];
        return moves & ~sameColorPieceBitBoard;
    }

    /**
     * Get possible movements for the queen.
     *
     * @param piecePosition          Position of the queen on the board.
     * @param sameColorPieceBitBoard Bitboard representing positions of pieces of the same color.
     * @param allPiecesBitBoard      Bitboard representing positions of all pieces on the board.
     * @return Bitboard representing possible queen movements.
     */
    public long getQueenMovement(byte piecePosition, long sameColorPieceBitBoard, long allPiecesBitBoard) {
        return getRookMovement(piecePosition, sameColorPieceBitBoard, allPiecesBitBoard)
                | getBishopMovement(piecePosition, sameColorPieceBitBoard, allPiecesBitBoard);
    }

    /**
     * Get possible movements for the rook.
     *
     * @param piecePosition          Position of the rook on the board.
     * @param allPiecesBitBoard      Bitboard representing positions of all pieces on the board.
     * @param sameColorPieceBitBoard Bitboard representing positions of pieces of the same color.
     * @return Bitboard representing possible rook movements.
     */
    public long getRookMovement(byte piecePosition, long allPiecesBitBoard, long sameColorPieceBitBoard) {
        long keyVal = PieceLogicUtilities.ROOK_MASK[piecePosition] & allPiecesBitBoard;
        long moves = ROOK_MOVES.get(piecePosition).get(keyVal);
        return moves & ~sameColorPieceBitBoard;
    }

    /**
     * Get possible movements for the bishop.
     *
     * @param piecePosition          Position of the bishop on the board.
     * @param allPiecesBitBoard      Bitboard representing positions of all pieces on the board.
     * @param sameColorPieceBitBoard Bitboard representing positions of pieces of the same color.
     * @return Bitboard representing possible bishop movements.
     */
    public long getBishopMovement(byte piecePosition, long allPiecesBitBoard, long sameColorPieceBitBoard) {
        long keyVal = PieceLogicUtilities.BISHOP_MASK[piecePosition] & allPiecesBitBoard;
        long moves = BISHOP_MOVES.get(piecePosition).get(keyVal);
        return moves & ~sameColorPieceBitBoard;
    }

    /**
     * Get possible movements for the knight.
     *
     * @param piecePosition          Position of the knight on the board.
     * @param sameColorPieceBitBoard Bitboard representing positions of pieces of the same color.
     * @return Bitboard representing possible knight movements.
     */
    public long getKnightMovement(byte piecePosition, long sameColorPieceBitBoard) {
        long moves = KNIGHT_MOVES[piecePosition];
        return moves & ~sameColorPieceBitBoard;
    }

    /**
     * Get possible movements for the pawn.
     *
     * @param piecePosition      Position of the pawn on the board.
     * @param color              Color of the pawn.
     * @param allPiecesBitBoard  Bitboard representing positions of all pieces on the board.
     * @param enemyPieceBitBoard Bitboard representing positions of enemy pieces.
     * @return Bitboard representing possible pawn movements.
     */
    public long getPawnMovement(byte piecePosition, boolean color, long allPiecesBitBoard, long enemyPieceBitBoard) {
        // Determine the offset for pawn movement based on its color
        long offset = color ? WHITE_PAWN_MOVE_OFFSET : GameLogicUtilities.BLACK_PAWN_MOVE_OFFSET;
        // Calculate the square in front of the pawn
        long squareInFrontOfPawn = GameLogicUtilities.squareAsBitBoard(piecePosition + offset);
        // Calculate capture squares based on pawn's position and color
        long captureSquares = (color ? WHITE_PAWN_CAPTURE[piecePosition] : BLACK_PAWN_CAPTURE[piecePosition])
                & enemyPieceBitBoard;

        // Get movement squares based on pawn's position and color
        long movementSquares = color ? WHITE_PAWN_ONLY_MOVES[piecePosition] : BLACK_PAWN_ONLY_MOVES[piecePosition];

        // Check if the square in front of the pawn is occupied by any piece
        if ((squareInFrontOfPawn & allPiecesBitBoard) != 0)
            return captureSquares;

        // Otherwise, return both capture and movement squares
        return captureSquares | (movementSquares & ~allPiecesBitBoard);
    }

    /**
     * Get possible capture squares for the pawn.
     *
     * @param color  Color of the pawn.
     * @param square Square position of the pawn.
     * @return Bitboard representing possible capture squares for the pawn.
     */
    public long getPawnCaptureSquare(boolean color, byte square) {
        return color ? WHITE_PAWN_CAPTURE[square] : BLACK_PAWN_CAPTURE[square];
    }
}
