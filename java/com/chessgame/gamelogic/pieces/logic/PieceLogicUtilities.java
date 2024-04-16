package com.chessgame.gamelogic.pieces.logic;

import com.chessgame.gamelogic.GameLogicUtilities;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import static com.chessgame.gamelogic.GameLogicUtilities.BOARD_EDGE_SIZE;

/**
 * Utility class for generating bitmaps representing the attack paths of chess pieces.
 */
@Component
public class PieceLogicUtilities {

    // Offsets for rook movement: right, left, up, down
    public static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    // Offsets for bishop movement: northwest, northeast, southwest, southeast
    public static final byte[] BISHOP_OFFSETS = {7, -7, 9, -9};
    // Masks to represent the first 8 bits
    public static final long FIRST_8_BITS = 0XFF;

    // Masks to represent attack paths for rook and bishop for each square on the board
    public static final long[] ROOK_MASK = new long[GameLogicUtilities.BOARD_SIZE];
    public static final long[] BISHOP_MASK = new long[GameLogicUtilities.BOARD_SIZE];

    // Constants representing edge distances and directions for bishop movement
    public static final int NUMBER_OF_POSSIBLE_VALUES_PER_EDGE = (int) Math.pow(2, BOARD_EDGE_SIZE);
    public static final int EDGE_DISTANCE_LEFT = 0;
    public static final int EDGE_DISTANCE_RIGHT = 1;
    public static final int EDGE_DISTANCE_UP = 2;
    public static final int EDGE_DISTANCE_DOWN = 3;
    public static final int BISHOP_LEFT_UP = 0;
    public static final int BISHOP_LEFT_DOWN = 1;
    public static final int BISHOP_RIGHT_UP = 2;
    public static final int BISHOP_RIGHT_DOWN = 3;

    /**
     * Initialize the masks for rook and bishop attack paths.
     */
    @PostConstruct
    public void initialize() {
        for (byte square = 0; square < GameLogicUtilities.BOARD_SIZE; square++)
            BISHOP_MASK[square] = toBitMapBishop(square, FIRST_8_BITS, FIRST_8_BITS);

        for (byte square = 0; square < GameLogicUtilities.BOARD_SIZE; square++)
            ROOK_MASK[square] = toBitMapRook(square, FIRST_8_BITS, FIRST_8_BITS);
    }

    /**
     * Get the distance from a position to the edge of the board in each direction.
     *
     * @param position The position on the board
     * @return An array containing the distance to the edge in each direction
     */
    public byte[] getDistanceTillEdgeOfBoard(byte position) {
        byte[] distances = new byte[4];

        distances[EDGE_DISTANCE_LEFT] = (byte) (BOARD_EDGE_SIZE - 1 - (position % BOARD_EDGE_SIZE));
        distances[EDGE_DISTANCE_RIGHT] = (byte) (position % BOARD_EDGE_SIZE);
        distances[EDGE_DISTANCE_UP] = (byte) (BOARD_EDGE_SIZE - 1 - (position / BOARD_EDGE_SIZE));
        distances[EDGE_DISTANCE_DOWN] = (byte) (position / BOARD_EDGE_SIZE);

        return distances;
    }

    /**
     * Get the distance from a position to the edge of the board in each diagonal direction.
     *
     * @param position The position on the board
     * @return An array containing the distance to the edge in each diagonal direction
     */
    public byte[] getDistanceTillEdgeOfBoardBishop(byte position) {
        byte[] distances = getDistanceTillEdgeOfBoard(position);
        int leftUp = Math.min(distances[EDGE_DISTANCE_RIGHT], distances[EDGE_DISTANCE_UP]);
        int leftDown = Math.min(distances[EDGE_DISTANCE_LEFT], distances[EDGE_DISTANCE_DOWN]);
        int rightUp = Math.min(distances[EDGE_DISTANCE_LEFT], distances[EDGE_DISTANCE_UP]);
        int rightDown = Math.min(distances[EDGE_DISTANCE_RIGHT], distances[EDGE_DISTANCE_DOWN]);

        return new byte[]{(byte) leftUp, (byte) leftDown, (byte) rightUp, (byte) rightDown};
    }

    /**
     * Generate a bitmap representing the attack path of a rook from a given square.
     *
     * @param square      The square from which to generate the attack path
     * @param rowValue    The bitmap representing the row of the square
     * @param columnValue The bitmap representing the column of the square
     * @return A bitmap representing the attack path of the rook
     */
    public long toBitMapRook(byte square, long rowValue, long columnValue) {
        int row = GameLogicUtilities.getRowOfSquare(square), column = getColumnOfSquare(square);
        long rowMask = rowValue << (BOARD_EDGE_SIZE * row), columnMask = 0;

        // Construct row mask
        for (int i = 0; i < BOARD_EDGE_SIZE; i++) {
            // Extract i'th bit value from columnValue, move it the 0 bit position
            long bitValue = extractBit(i, columnValue);
            // Add it by moving it to its right row
            columnMask = columnMask | (bitValue << (column + (i * BOARD_EDGE_SIZE)));
        }
        return rowMask | columnMask;
    }

    /**
     * Generate a bitmap representing the attack path of a bishop from a given square.
     *
     * @param square          The square from which to generate the attack path
     * @param nwDiagonalValue The bitmap representing the northwest diagonal
     * @param neDiagonalValue The bitmap representing the northeast diagonal
     * @return A bitmap representing the attack path of the bishop
     */
    public long toBitMapBishop(byte square, long nwDiagonalValue, long neDiagonalValue) {
        byte[] edgeDistances = getDistanceTillEdgeOfBoardBishop(square);
        long resultNwDiagonal = insertDiagonalVal(square, nwDiagonalValue, BISHOP_OFFSETS[BISHOP_RIGHT_UP],
                edgeDistances[BISHOP_RIGHT_UP]);

        long resultNeDiagonal = insertDiagonalVal(square, neDiagonalValue, BISHOP_OFFSETS[BISHOP_LEFT_UP],
                edgeDistances[BISHOP_LEFT_UP]);

        return resultNwDiagonal | resultNeDiagonal;
    }

    /**
     * Helper method to insert diagonal values into the attack path bitmap of a bishop.
     *
     * @param square               The square from which to start inserting values
     * @param diagonalVal          The bitmap representing the diagonal
     * @param offset               The offset for diagonal movement
     * @param offsetSquareTillEdge The distance till the edge of the board in the specified diagonal direction
     * @return A bitmap representing the attack path of the bishop in the specified direction
     */
    private long insertDiagonalVal(byte square, long diagonalVal, int offset, int offsetSquareTillEdge) {
        long result = 0;
        // Find the most up square on the diagonal and set curr square to the value
        int currSquare = square + (offset * offsetSquareTillEdge);
        byte[] currEdgeDistances = getDistanceTillEdgeOfBoardBishop((byte) currSquare);
        int diagonalLength = offset == BISHOP_OFFSETS[BISHOP_RIGHT_UP] ?
                currEdgeDistances[BISHOP_RIGHT_DOWN] : currEdgeDistances[BISHOP_LEFT_DOWN];

        // Run on the entire diagonal from up to down
        for (int i = 0; i <= diagonalLength; i++) {
            long bitVal = extractBit(i, diagonalVal);
            // Move the bit to the curr square, then move curr square by -offset
            result |= bitVal << currSquare;
            currSquare = currSquare - offset;
        }
        return result;
    }

    /**
     * Given a square, returns its column number.
     *
     * @param square The square number (0 to 63).
     * @return The column number of the square.
     */
    public int getColumnOfSquare(byte square) {
        return square % BOARD_EDGE_SIZE;
    }

    /**
     * Extract a bit from a long value at a given position.
     *
     * @param position The position of the bit to extract
     * @param val      The long value from which to extract the bit
     * @return The extracted bit
     */
    private long extractBit(long position, long val) {
        return (val & (1L << position)) >>> position;
    }

}
