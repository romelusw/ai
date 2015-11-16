package com.romelus_borucki.common.utils;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object able to read/write a Wumpus board.
 *
 * @author romelus
 */
public class WumpusBoardHelper {
    /**
     * Pattern for board dimensions.
     */
    private static final Pattern BOARD_DIMENSIONS_PATTERN = Pattern.compile("^Size.* (\\d+),(\\d+)$");
    private static final List<PieceType> safePieces = Arrays.asList(PieceType.Safe, PieceType.Ok, PieceType.Gold, PieceType.Enter);
    private static final List<PieceType> gameEndingPieces = Arrays.asList(PieceType.Wumpus, PieceType.Gold, PieceType.Pit);
    private static final List<PieceType> confirmedPieces = ListUtils.union(gameEndingPieces, Arrays.asList(PieceType.Breezy, PieceType.Stench, PieceType.Safe));

    /**
     * Enumeration of the board pieces.
     */
    public enum PieceType {
        Wumpus("W"), QWump("?W"), Stench("S"), Pit("P"), QPit("?P"), Breezy("B"), Gold("G"), Enter("E"), Ok("Ok"), Safe("✓");
        /**
         * The string literal representing the piece.
         */
        private String literal;

        /**
         * Default constructor.
         *
         * @param token the string mapping for the piece
         */
        PieceType(final String token) {
            literal = token;
        }

        /**
         * Converts a string into its enumerable state.
         * @param s the string to convert
         * @return the converted {@link PieceType}
         */
        public static PieceType fromString(final String s) {
            PieceType retVal = null;
            for(PieceType b : values()) {
                if(b.literal.equalsIgnoreCase(s)) {
                    retVal = b;
                    break;
                }
            }
            return retVal;
        }
    }

    /**
     * Enumeration of cardinal directions.
     */
    public enum Direction {
        North(0, 1), South(0, -1), East(1, 0), West(-1, 0);
        /**
         * The new x position.
         */
        public int xIncrement;
        /**
         * The new y position.
         */
        public int yIncrement;

        /**
         * Default constructor.
         *
         * @param xMotion the
         * @param yMotion the
         */
        Direction(final int xMotion, final int yMotion) {
            xIncrement = xMotion;
            yIncrement = yMotion;
        }
    }

    /**
     * A class which represents a board piece for the wumpus world.
     */
    public static class BoardPiece {
        private int x;
        private int y;
        private Set<PieceType> types = new HashSet<>();

        /**
         * Default constructor.
         *
         * @param xloc the x-position of the piece on the board
         * @param yloc the y-position of the piece on the board
         */
        public BoardPiece(final int xloc, final int yloc) {
            x = xloc;
            y = yloc;
        }

        /**
         * Getter for the row location.
         *
         * @return the row index
         */
        public int getX() {
            return x;
        }

        /**
         * Getter for the column location.
         *
         * @return the column index
         */
        public int getY() {
            return y;
        }

        /**
         * Adds a type to the piece.
         *
         * @param type the type of piece to add
         * @return flag indicating if add successfully
         */
        public boolean addType(final PieceType ...type) {
            return types.addAll(Arrays.asList(type));
        }

        /**
         * Determine if a type exists on the piece.
         *
         * @param type the type to check
         * @return flag indicating if type exists
         */
        public boolean hasType(final PieceType ...type) {
            final Set<PieceType> clone = new HashSet<>(Arrays.asList(type));
            clone.retainAll(types);
            return !clone.isEmpty();
        }

        /**
         * Getter for the types.
         *
         * @return the types
         */
        public Set<PieceType> getTypes() {
            return types;
        }

        /**
         * Retrieves the types in string format.
         *
         * @return the types
         */
        public String typesToString() {
            final StringBuilder sb = new StringBuilder();
            for(final PieceType type : types) {
                sb.append(type.literal);
            }
            return sb.toString();
        }

        /**
         * Determines if the piece is safe to move into.
         *
         * @return flag indicating if the piece is safe
         */
        public boolean isSafe() {
            final Set<PieceType> clone = new HashSet<>(safePieces);
            clone.retainAll(types);
            return !clone.isEmpty();
        }

        /**
         * Determines if the piece is "confirmed".
         *
         * @return flag indicating if the piece is confirmed
         */
        public boolean isConfirmed() {
            final Set<PieceType> clone = new HashSet<>(confirmedPieces);
            clone.retainAll(types);
            return !clone.isEmpty();
        }

        /**
         * Determines if the piece is "game-ending".
         *
         * @return flag indicating if the piece is game ending
         */
        public boolean isGameEnding() {
            final Set<PieceType> clone = new HashSet<>(gameEndingPieces);
            clone.retainAll(types);
            return !clone.isEmpty();
        }
    }

    /**
     * Reads a specially formatted file to representing a wumpus board.
     *
     * @param f the file
     * @return the resolved board state
     */
    public static BoardState readBoard(final File f) {
        final BoardState retVal = new BoardState(null, -1, -1);
        try {
            final Scanner s = new Scanner(f);
            int lineNum = 0;
            while (s.hasNextLine()) {
                final String line = s.nextLine();
                if(lineNum == 0) {
                    final Matcher m = BOARD_DIMENSIONS_PATTERN.matcher(line);
                    if (m.find()) {
                        retVal.setBoard(new BoardPiece[Integer.parseInt(m.group(1))][Integer.parseInt(m.group(2))]);
                    } else {
                        break;
                    }
                } else {
                    final String[] tokens = line.split(",");
                    final int x = Integer.parseInt(tokens[0]);
                    final int y = Integer.parseInt(tokens[1]);
                    final BoardPiece bp = new BoardPiece(y, x);
                    // Add all the types
                    for(int i = 2; i < tokens.length; i++) {
                        final PieceType type = PieceType.fromString(tokens[i]);
                        // Mark entrance
                        if(type == PieceType.Enter) {
                            retVal.agentXPosition = x;
                            retVal.agentYPosition = y;
                        }
                        if(type != null) {
                            bp.addType(type);
                        }
                    }
                    retVal.getBoard()[y][x] = bp;
                }
                lineNum++;
            }
        } catch (final FileNotFoundException e) {
            // Handle
        }
        return retVal;
    }

    /**
     * Outputs the board to stdout.
     *
     * @param boardState the board
     */
    public static void printBoard(final BoardState boardState) {
        final int rows = boardState.getBoard().length;
        final int cols = boardState.getBoard()[0].length;
        final String tableHeader = new String(new char[cols]).replace("\0", "+-------------") + "+\n";
        final StringBuilder sb = new StringBuilder(tableHeader);
        for(int i = rows - 1; i >= 0; i--) {
            for(int j = 0; j < cols; j++) {
                final BoardPiece piece = boardState.getBoard()[i][j];
                String types = piece == null ? "" : piece.typesToString().toString();
                int padding = 13;
                if(j == boardState.getAgentXPosition() && i == boardState.getAgentYPosition()) {
                    // Color ai position red
                    types = "\033[31m"+ types +"\033[0m";
                    padding += 9;
                }
                sb.append("|").append(StringUtils.center(String.format("%s", types), padding));
            }
            sb.append("|\n").append(tableHeader);
        }
        System.out.println(sb);
    }

    /**
     * Wrapper around the board state.
     */
    public static class BoardState {
        private BoardPiece[][] board;
        private int agentXPosition;
        private int agentYPosition;

        public BoardState(final BoardPiece[][] brd, final int xPos, final int yPos) {
            board = brd;
            agentXPosition = xPos;
            agentYPosition = yPos;
        }

        public BoardPiece[][] getBoard() {
            return board;
        }

        public void setBoard(BoardPiece[][] board) {
            this.board = board;
        }

        public int getAgentXPosition() {
            return agentXPosition;
        }

        public int getAgentYPosition() {
            return agentYPosition;
        }
    }
}