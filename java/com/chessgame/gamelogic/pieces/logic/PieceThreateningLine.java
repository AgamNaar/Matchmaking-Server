package com.chessgame.gamelogic.pieces.logic;

import com.chessgame.gamelogic.GameLogicUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The PieceThreateningLine class is responsible for determining the threatening lines
 * of various chess pieces on the board, given their positions and the position of the enemy king.
 * Threatening lines represent the squares on the board that are under attack by a particular piece.
 * Threading lines are also x-ray attack, meaning squares beyond the first enemy piece also marked.
 */
@Component
public class PieceThreateningLine {

    private static final int MAX_NUMBER_OF_PIECE_ON_THREAT_LINE = 1;

    @Autowired
    private PieceLogicUtilities pieceLogicUtilities;

    /**
     * Calculates the threatening line for a queen, which is a combination of the threatening lines
     * of a rook and a bishop.
     *
     * @param piecePosition     the position of the queen
     * @param enemyKingSquare   the position of the enemy king
     * @param allPiecesBitBoard bitboard representing all pieces on the board
     * @return bitboard representing the threatening line of the queen
     */
    public long getQueenThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        // The queen can have only 1 treat line as a piece on the enemy king (either as a bishop or rook)
        // Either both 0 or just one of them is 0
        long rookThreatLine = getRookThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard);
        long bishopThreatLine = getBishopThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard);
        return rookThreatLine + bishopThreatLine;
    }

    /**
     * Calculates the threatening line for a rook.
     *
     * @param piecePosition     the position of the rook
     * @param enemyKingSquare   the position of the enemy king
     * @param allPiecesBitBoard bitboard representing all pieces on the board
     * @return bitboard representing the threatening line of the rook
     */
    public long getRookThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        return calculateThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard,
                PieceLogicUtilities.ROOK_OFFSETS, pieceLogicUtilities.getDistanceTillEdgeOfBoardBishop(piecePosition));
    }

    /**
     * Calculates the threatening line for a bishop.
     *
     * @param piecePosition     the position of the bishop
     * @param enemyKingSquare   the position of the enemy king
     * @param allPiecesBitBoard bitboard representing all pieces on the board
     * @return bitboard representing the threatening line of the bishop
     */
    public long getBishopThreateningLine(byte piecePosition, byte enemyKingSquare, Long allPiecesBitBoard) {
        return calculateThreateningLine(piecePosition, enemyKingSquare, allPiecesBitBoard,
                PieceLogicUtilities.BISHOP_OFFSETS,
                pieceLogicUtilities.getDistanceTillEdgeOfBoardBishop(piecePosition));
    }

    /**
     * Calculates the threatening line for a piece given its position and the position of the enemy king.
     *
     * @param pieceSquare   the position of the piece
     * @param kingPosition  the position of the enemy king
     * @param bitBoard      bitboard representing all pieces on the board
     * @param offsetArray   array of offsets representing possible moves of the piece
     * @param movesTillEdge array representing the number of moves till the edge of the board
     * @return bitboard representing the threatening line of the piece
     */
    private long calculateThreateningLine(byte pieceSquare, byte kingPosition, long bitBoard,
                                          byte[] offsetArray, byte[] movesTillEdge) {
        long positionBit = GameLogicUtilities.squareAsBitBoard(pieceSquare);
        long kingPositionAsBitBoard = GameLogicUtilities.squareAsBitBoard(kingPosition);
        for (byte i = 0; i < offsetArray.length; i++) {
            long currentLine = positionBit;
            int numberOfPiecesOnLine = 0;
            // Run until the edge of the board or till the enemy king
            for (byte j = 1; j <= movesTillEdge[i]; j++) {
                long currentBitPosition = GameLogicUtilities.shiftNumberLeft(positionBit, j * offsetArray[i]);
                // Check if it's on a piece
                if ((currentBitPosition & bitBoard) != 0) {
                    // the piece is a king
                    if ((currentBitPosition & kingPositionAsBitBoard) != 0) {
                        return currentLine;
                    }
                    numberOfPiecesOnLine++;
                    // Too many piece, check next offset
                    if (numberOfPiecesOnLine > MAX_NUMBER_OF_PIECE_ON_THREAT_LINE)
                        break;
                }
                currentLine |= currentBitPosition;
            }
        }
        return 0;
    }
}
