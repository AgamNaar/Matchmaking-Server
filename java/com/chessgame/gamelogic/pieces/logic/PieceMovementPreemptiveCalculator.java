package com.chessgame.gamelogic.pieces.logic;

import com.chessgame.gamelogic.GameLogicUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

import static com.chessgame.gamelogic.pieces.logic.PieceLogicUtilities.BISHOP_OFFSETS;
import static com.chessgame.gamelogic.pieces.logic.PieceLogicUtilities.NUMBER_OF_POSSIBLE_VALUES_PER_EDGE;

/**
 * This class is responsible to generate for each piece, for each square on the board (0-63),
 * bitboards that represent the moves it can do
 * For line pieces - their moves depend on the pieces on their moving line,
 * so it calculates for each square all the possible combination on the moving lines
 * Save it on a 64 array of hashmap, each cell of the array is a square,
 * and for each hashmap the key for the movement is the mask value of the pieces on the movement line
 * For none line pieces - simply calculate for each square what are the possible movements and save it on an array.
 * The movement is represented as a bitboard.
 */
@Component
public class PieceMovementPreemptiveCalculator {

    @Autowired
    private PieceLogicUtilities pieceLogicUtilities;

    // Offsets for different piece movements
    private static final byte[] KING_OFFSETS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final byte[] ROOK_OFFSETS = {1, -1, 8, -8};
    private static final byte[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final byte[] WHITE_PAWN_OFFSETS_ATK = {7, 9};
    private static final byte[] BLACK_PAWN_OFFSETS_ATK = {-7, -9};

    private static final byte MAX_DX_DY = 4;

    private static final byte LAST_SQUARE_ON_2ND_ROW = 15;
    private static final byte LAST_SQUARE_ON_6TH_ROW = 48;

    /**
     * Generates possible moves for the king piece.
     *
     * @param movesArray An array to store the generated moves.
     */
    public void generateKingMoves(long[] movesArray) {
        generateMoves(movesArray, KING_OFFSETS);
    }

    /**
     * Generates possible moves for line-moving pieces (rook and bishop).
     *
     * @param rookMoves   A list to store rook moves.
     * @param bishopMoves A list to store bishop moves.
     */
    public void generateLinePieceMoves(ArrayList<HashMap<Long, Long>> rookMoves,
                                       ArrayList<HashMap<Long, Long>> bishopMoves) {
        generateAllMovesLinePiece(rookMoves, bishopMoves);
    }

    /**
     * Generates possible moves for the knight piece.
     *
     * @param movesArray An array to store the generated moves.
     */
    public void generateKnightMoves(long[] movesArray) {
        generateMoves(movesArray, KNIGHT_OFFSETS);
    }

    /**
     * Generates possible moves for the pawn piece.
     *
     * @param movesArray An array to store the generated moves for regular moves.
     * @param atkArray   An array to store the generated moves for attacking.
     * @param color      The color of the pawn.
     */
    public void generatePawnMoves(long[] movesArray, long[] atkArray, boolean color) {
        if (color == GameLogicUtilities.WHITE) {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, WHITE_PAWN_OFFSETS_ATK);
        } else {
            generatePawnMoves(movesArray, color);
            generateMoves(atkArray, BLACK_PAWN_OFFSETS_ATK);
        }
    }

    /**
     * Generates possible moves for pawns on the chessboard.
     *
     * @param moveArray An array to store the generated moves.
     * @param color     Boolean indicating the color of pawns (true for white, false for black).
     */
    private void generatePawnMoves(long[] moveArray, boolean color) {
        // Determine the offset for pawn movement based on color
        int offset = color ? GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET : GameLogicUtilities.BLACK_PAWN_MOVE_OFFSET;

        // Define the boundaries for pawn movement on the board
        byte firstSquareOn2NdtRow = LAST_SQUARE_ON_2ND_ROW - GameLogicUtilities.BOARD_EDGE_SIZE + 1;
        byte lastSquareOn6ThRow = LAST_SQUARE_ON_6TH_ROW + GameLogicUtilities.BOARD_EDGE_SIZE + 1;

        // Iterate over the squares where pawns can move
        for (byte pieceSquare = firstSquareOn2NdtRow; pieceSquare < lastSquareOn6ThRow; pieceSquare++) {
            // Initialize the bitboard representing possible moves for the current square
            long currSquareMoves = 0L;

            // Set the bit corresponding to the current square
            currSquareMoves |= GameLogicUtilities.squareAsBitBoard(offset + pieceSquare);

            // If the pawn is on its starting square and can move two squares forward, set the corresponding bit
            if (pieceSquare <= LAST_SQUARE_ON_2ND_ROW && offset == GameLogicUtilities.WHITE_PAWN_MOVE_OFFSET)
                currSquareMoves |= GameLogicUtilities.squareAsBitBoard((offset * 2) + pieceSquare);

            // If the pawn is on its starting square and can move two squares forward, set the corresponding bit
            if (pieceSquare >= LAST_SQUARE_ON_6TH_ROW && offset == GameLogicUtilities.BLACK_PAWN_MOVE_OFFSET)
                currSquareMoves |= GameLogicUtilities.squareAsBitBoard((offset * 2) + pieceSquare);

            // Store the generated moves for the current square in the move array
            moveArray[pieceSquare] = currSquareMoves;
        }
    }


    /**
     * Generates possible moves for pieces with predefined offsets on the chessboard.
     *
     * @param moveArray   An array to store the generated moves.
     * @param offsetArray An array containing predefined offsets for the piece's movement.
     */
    private void generateMoves(long[] moveArray, byte[] offsetArray) {
        // Iterate over all squares on the board
        for (byte square = 0; square < GameLogicUtilities.BOARD_SIZE; square++) {
            // Initialize the bitboard representing possible moves for the current square
            long currSquareMoves = 0L;

            // Iterate over each predefined offset
            for (byte offset : offsetArray) {
                // Check if the move is valid and within the board boundaries
                if (dxDyCheck(square, offset))
                    // Set the bit corresponding to the destination square after applying the offset
                    currSquareMoves |= GameLogicUtilities.squareAsBitBoard(offset + square);
            }

            // Store the generated moves for the current square in the move array
            moveArray[square] = currSquareMoves;
        }
    }


    /**
     * Generates possible moves for rook and bishop pieces on the chessboard.
     *
     * @param moveListRook   An ArrayList to store generated moves for rooks.
     * @param moveListBishop An ArrayList to store generated moves for bishops.
     */
    private void generateAllMovesLinePiece(ArrayList<HashMap<Long, Long>> moveListRook,
                                           ArrayList<HashMap<Long, Long>> moveListBishop) {
        // Iterate over all squares on the board
        for (byte pieceSquare = 0; pieceSquare < GameLogicUtilities.BOARD_SIZE; pieceSquare++) {
            // Iterate over each possible combination of row and column values
            for (int rowValue = 0; rowValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; rowValue++) {
                for (int columnValue = 0; columnValue < NUMBER_OF_POSSIBLE_VALUES_PER_EDGE; columnValue++) {
                    // Generate bitmaps representing possible moves for rooks and bishops
                    long rookMap = pieceLogicUtilities.toBitMapRook(pieceSquare, rowValue, columnValue);
                    long bishopMap = pieceLogicUtilities.toBitMapBishop(pieceSquare, rowValue, columnValue);

                    // Generate moves for rooks and bishops
                    Long movesRook = generateMovesLinePiece(pieceSquare, ROOK_OFFSETS, rookMap,
                            pieceLogicUtilities.getDistanceTillEdgeOfBoard(pieceSquare));
                    Long movesBishop = generateMovesLinePiece(pieceSquare, BISHOP_OFFSETS, bishopMap,
                            pieceLogicUtilities.getDistanceTillEdgeOfBoardBishop(pieceSquare));

                    // Store the generated moves for rooks and bishops in the respective lists
                    moveListRook.get(pieceSquare).put(rookMap, movesRook);
                    moveListBishop.get(pieceSquare).put(bishopMap, movesBishop);
                }
            }
        }
    }


    /**
     * Generates possible moves for rook and bishop pieces in a specific direction on the chessboard.
     *
     * @param pieceSquare   The square index of the piece on the board.
     * @param offsetArray   An array containing predefined offsets for the piece's movement in different directions.
     * @param bitBoard      A bitmap representing the occupancy of the board.
     * @param movesTillEdge An array representing the number of possible moves until the edge of the board in each direction.
     * @return A MovementData object containing the generated moves and the count of moves.
     */
    private Long generateMovesLinePiece(byte pieceSquare, byte[] offsetArray, long bitBoard,
                                        byte[] movesTillEdge) {
        // Calculate the bit representing the piece's current position
        long positionBit = GameLogicUtilities.squareAsBitBoard(pieceSquare), result = 0;

        // Iterate over each predefined offset
        for (byte i = 0; i < offsetArray.length; i++) {
            // Iterate over the possible moves in the current direction until reaching the edge of the board
            for (byte j = 1; j <= movesTillEdge[i]; j++) {
                // Calculate the bit representing the current destination square
                long currentBit = GameLogicUtilities.shiftNumberLeft(positionBit, j * offsetArray[i]);

                // Check if the current destination square is occupied by a piece
                if ((currentBit & bitBoard) != 0) {
                    // If occupied, add the current bit to the result and increment the counter
                    result |= currentBit;
                    // Break the loop as the piece cannot move beyond this square
                    break;
                } else {
                    // If empty, add the current bit to the result
                    result |= currentBit;
                }
            }
        }
        return result;
    }


    /**
     * Checks if the movement specified by the given offset is within the board boundaries.
     *
     * @param pieceSquare The current square index of the piece on the board.
     * @param offset      The offset indicating the movement direction.
     * @return True if the movement is within the board boundaries, otherwise false.
     */
    private boolean dxDyCheck(byte pieceSquare, byte offset) {
        // Calculate the index of the target square after applying the offset
        byte targetSquare = (byte) (pieceSquare + offset);

        // Get the column and row indices of the current square and the target square
        int currX = GameLogicUtilities.getColOfSquare(pieceSquare),
                currY = GameLogicUtilities.getRowOfSquare(pieceSquare),
                targetX = GameLogicUtilities.getColOfSquare(targetSquare),
                targetY = GameLogicUtilities.getRowOfSquare(targetSquare);

        // Calculate the absolute differences in column and row indices (dx and dy)
        int dx = Math.abs(currX - targetX), dy = Math.abs(currY - targetY);

        // Check if the sum of dx and dy is less than the maximum dx and dy values,
        // and if the target square index is within the board boundaries
        return (dx + dy < MAX_DX_DY) && targetSquare < GameLogicUtilities.BOARD_SIZE && targetSquare > -1;
    }
}
