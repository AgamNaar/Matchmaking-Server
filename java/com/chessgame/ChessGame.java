package com.chessgame;


import com.chessgame.gamelogic.FenTranslator;
import com.chessgame.gamelogic.GameLogicUtilities;
import com.chessgame.gamelogic.GameStatusHandler;
import com.chessgame.gamelogic.LegalMoveHandler;
import com.chessgame.gamelogic.pieces.King;
import com.chessgame.gamelogic.pieces.Pawn;
import com.chessgame.gamelogic.pieces.Piece;
import com.chessgame.gamelogic.specialmoves.SpecialMovesHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

// A class that represent a game of chess
public class ChessGame {
    private boolean colorOfPlayersTurn;
    private LinkedList<Piece> pieceList;
    private Piece[] pieceBoard;
    private long playerTurnPiecesBitBoard;
    private long allPiecesBitBoard;
    private Piece currentPlayerKing;

    private SpecialMovesHandler specialMovesHandler;
    private LegalMoveHandler legalMoveHandler;
    private GameStatusHandler gameStatusHandler;
    private FenTranslator translator;

    public static final int MOVE_NOT_EXECUTED = -1;
    public static final char PROMOTE_TO_QUEEN = 'q';
    public static final char PROMOTE_TO_ROOK = 'r';
    public static final char PROMOTE_TO_KNIGHT = 'n';
    public static final char PROMOTE_TO_BISHOP = 'b';

    /**
     * Initializes the ChessGame object by injecting dependencies and setting up initial configurations.
     *
     * @param translator        The FenTranslator object responsible for translating FEN strings.
     * @param legalMoveHandler  The LegalMoveHandler object responsible for managing legal moves.
     * @param gameStatusHandler The GameStatusHandler object responsible for managing game status.
     */
    @PostConstruct
    @Autowired
    public void initialize(FenTranslator translator,
                           LegalMoveHandler legalMoveHandler,
                           GameStatusHandler gameStatusHandler) {
        this.translator = translator;
        this.legalMoveHandler = legalMoveHandler;
        this.gameStatusHandler = gameStatusHandler;
    }

    /**
     * Initializes a new instance of the ChessGame class.
     *
     * @param fen The FEN (Forsyth-Edwards Notation) string representing the initial state of the chess game.
     *            If empty or null, the game will start from the default setup.
     */
    public ChessGame(String fen) {
        reset(fen);
    }

    /**
     * Initializes a new instance of the ChessGame class with the default setup.
     */
    public ChessGame() {
        reset("");
    }

    /**
     * Resets the game to the default startup or the state specified by the FEN string.
     *
     * @param fen The FEN (Forsyth-Edwards Notation) string representing the desired state of the chess game.
     *            If empty or null, the game will be reset to the default setup.
     */
    public void reset(String fen) {
        pieceBoard = new Piece[GameLogicUtilities.BOARD_SIZE];
        translator.translateFen(this, fen);

        // Insert pieces from the list into the board
        for (Piece piece : pieceList) {
            pieceBoard[piece.getSquare()] = piece;
        }

        // Update game attributes such as bitboards and special moves
        updateAttributes();
    }

    /**
     * Retrieves the legal moves that a piece can perform as a bitboard.
     * Returns a bitboard representing the legal moves that the specified piece can make.
     *
     * @param piece The piece for which to retrieve legal moves.
     * @return A bitboard representing the legal moves that the specified piece can make.
     * Returns 0 if the specified piece is null or does not belong to the current player's turn.
     */
    public long getLegalMovesAsBitBoard(Piece piece) {
        if (piece != null && colorOfPlayersTurn == piece.getColor()) {
            long pieceMoves = piece.getMovesAsBitBoard(allPiecesBitBoard, playerTurnPiecesBitBoard);
            long specialMoves = specialMovesHandler.getSpecialMoves(piece, getBitBoardOfSquaresThreatenByEnemy(),
                    allPiecesBitBoard, pieceList, colorOfPlayersTurn, currentPlayerKing);

            long allPieceMoves = pieceMoves | specialMoves;
            return legalMoveHandler.removeIllegalMoves(allPieceMoves, piece, pieceList, colorOfPlayersTurn,
                    allPiecesBitBoard, playerTurnPiecesBitBoard, gameStatusHandler.isPlayerChecked(this),
                    specialMovesHandler.getEnPassantSquare(), currentPlayerKing);
        }
        return 0;
    }

    /**
     * Executes a move of a piece from its initial square to the target square.
     * Checks if it's a valid move, if not, the move is not executed.
     * Returns the status of the game after the move.
     *
     * @param currentSquare          The index of the initial square where the piece is located.
     * @param targetSquare           The index of the target square where the piece will move.
     * @param typeOfPieceToPromoteTo The type of piece to promote to (for pawn promotion), if applicable.
     * @return The status of the game after the move:
     * - MOVE_NOT_EXECUTED if the move is not valid and not executed.
     * - The status of the game after the move otherwise.
     */
    public int executeMove(byte currentSquare, byte targetSquare, char typeOfPieceToPromoteTo) {
        Piece pieceToMove = pieceBoard[currentSquare];

        if (!legalMoveHandler.isValidMove(currentSquare, targetSquare, pieceBoard, colorOfPlayersTurn,
                getLegalMovesAsBitBoard(pieceToMove)))
            return MOVE_NOT_EXECUTED;

        if (specialMovesHandler.isSpecialMove(targetSquare, pieceToMove))
            specialMovesHandler.executeSpecialMove(currentSquare, targetSquare, pieceList, pieceBoard,
                    typeOfPieceToPromoteTo);
        else
            GameLogicUtilities.updatePiecePosition(targetSquare, currentSquare, pieceBoard, pieceList);

        // Change the turn of the player, and update all other game attributes
        colorOfPlayersTurn = !colorOfPlayersTurn;
        specialMovesHandler.updateSpecialMoves(currentSquare, targetSquare, pieceToMove);
        updateAttributes();

        return gameStatusHandler.afterTurnHandler(new ChessMove(currentSquare, targetSquare,
                typeOfPieceToPromoteTo), this);
    }

    /**
     * Retrieves the bitboard representing squares threatened by enemy pieces.
     * This method calculates and returns the bitboard representing squares threatened by enemy pieces,
     * considering both pawn attacks and other piece movements.
     *
     * @return The bitboard representing squares threatened by enemy pieces.
     */
    public long getBitBoardOfSquaresThreatenByEnemy() {
        long movementBitBoard = 0, enemyBitBoard = getEnemyBitBoard();
        for (Piece piece : pieceList)
            if (piece.getColor() == !colorOfPlayersTurn)
                if (piece instanceof Pawn)
                    movementBitBoard |= ((Pawn) piece).getPawnAttackSquare();
                else
                    movementBitBoard |= piece.getMovesAsBitBoard(allPiecesBitBoard, enemyBitBoard);

        return movementBitBoard;
    }

    /**
     * Updates various attributes of the chess game based on the current state, including the current player's king,
     * the bitboards representing all pieces and the current player's pieces, and the legal move handler.
     */
    private void updateAttributes() {
        updateCurrentKing();
        updateBitBoards();
        legalMoveHandler.updateTreatingLines(pieceList, allPiecesBitBoard, getEnemyBitBoard(),
                colorOfPlayersTurn, currentPlayerKing);
    }

    /**
     * Updates the reference to the current player's king based on the current piece list.
     */
    private void updateCurrentKing() {
        for (Piece piece : pieceList)
            if (piece instanceof King && piece.getColor() == colorOfPlayersTurn) {
                currentPlayerKing = piece;
                return;
            }
    }

    /**
     * Updates the bitboards representing all pieces and the current player's pieces based on the piece list.
     */
    private void updateBitBoards() {
        allPiecesBitBoard = 0;
        playerTurnPiecesBitBoard = 0;
        // For each piece, add its bitboard position to it's matching bitboard
        for (Piece piece : pieceList) {
            long pieceBitBoardPosition = piece.getSquareAsBitBoard();
            if (piece.getColor() == colorOfPlayersTurn)
                playerTurnPiecesBitBoard |= pieceBitBoardPosition;

            allPiecesBitBoard |= pieceBitBoardPosition;
        }
    }

    /**
     * Sets the color of the player whose turn it is.
     *
     * @param colorOfPlayersTurn The color of the player whose turn it is. True for white, false for black.
     */
    public void setColorOfPlayersTurn(boolean colorOfPlayersTurn) {
        this.colorOfPlayersTurn = colorOfPlayersTurn;
    }

    /**
     * Sets the list of chess pieces currently on the board.
     *
     * @param pieceList The list of chess pieces currently on the board.
     */
    public void setPieceList(LinkedList<Piece> pieceList) {
        this.pieceList = pieceList;
    }

    /**
     * Retrieves the king piece of the current player.
     *
     * @return The king piece of the current player.
     */
    public Piece getCurrentPlayerKing() {
        return currentPlayerKing;
    }

    /**
     * Sets the handler for special moves in the chess game.
     *
     * @param specialMovesHandler The handler for special moves in the chess game.
     */
    public void setSpecialMovesHandler(SpecialMovesHandler specialMovesHandler) {
        this.specialMovesHandler = specialMovesHandler;
    }

    /**
     * Retrieves the list of chess pieces currently on the board.
     *
     * @return The list of chess pieces currently on the board.
     */
    public LinkedList<Piece> getPieceList() {
        return pieceList;
    }

    /**
     * Retrieves the bitboard representing the pieces of the enemy.
     * This method calculates the bitwise AND operation between the bitboard representing all pieces on the board
     * and the complement of the bitboard representing the current player's pieces.
     *
     * @return The bitboard representing the pieces of the enemy.
     */
    private long getEnemyBitBoard() {
        return allPiecesBitBoard & ~playerTurnPiecesBitBoard;
    }

    /**
     * Determines whether it is white's turn to play.
     *
     * @return True if it is white's turn to play, false if it is black's turn.
     */
    public boolean getPlayerToPlay() {
        return colorOfPlayersTurn;
    }


}
