package com.romelus_borucki.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
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

    public static void main(final String...args) {
        final ClassLoader classLoader = WumpusBoardHelper.class.getClassLoader();
        final BoardPiece[][] board = readBoard(new File(classLoader.getResource("b1.txt").getFile()));
        printBoard(board);
    }

    /**
     * Enumeration of the board pieces.
     */
    public enum PieceType {
        Wumpus("W"), Breezy("B"), Gold("G"), Stench("S"), Pit("P"), Enter("E"), Empty("");
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
         * Converts a string into its enumerable state, any non recognized string will be converted to {@link PieceType#Empty}.
         * @param s the string to convert
         * @return the converted {@link PieceType}
         */
        public static PieceType fromString(final String s) {
            PieceType retVal = PieceType.Empty;
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
     * A class which represents a board piece for the wumpus world.
     */
    public static class BoardPiece {
        private int x;
        private int y;
        private Set<PieceType> types = new HashSet<PieceType>();

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
         * Adds a type to the piece.
         *
         * @param type the type of piece to add
         * @return flag indicating if add successfully
         */
        public boolean addType(final PieceType type) {
            return types.add(type);
        }

        /**
         * Getter for types.
         *
         * @return the types
         */
        public String getTypes() {
            final StringBuilder sb = new StringBuilder();
            for(PieceType type : types) {
                sb.append(type.literal);
            }
            return sb.toString();
        }
    }

    /**
     * Reads a specially formatted file to representing a wumpus board.
     *
     * @param f the file
     * @return the resolved board
     */
    public static BoardPiece[][] readBoard(final File f) {
        BoardPiece[][] retVal = null;
        try {
            final Scanner s = new Scanner(f);
            int lineNum = 0;
            while (s.hasNextLine()) {
                final String line = s.nextLine();
                if(lineNum == 0) {
                    final Matcher m = BOARD_DIMENSIONS_PATTERN.matcher(line);
                    if (m.find()) {
                        retVal = new BoardPiece[Integer.parseInt(m.group(1))][Integer.parseInt(m.group(2))];
                    } else {
                        break;
                    }
                } else {
                    final String[] tokens = line.split(",");
                    final int x = Integer.parseInt(tokens[0]);
                    final int y = Integer.parseInt(tokens[1]);
                    final BoardPiece bp = new BoardPiece(x, y);
                    // Add all the types
                    for(int i = 2; i < tokens.length; i++) {
                        bp.addType(PieceType.fromString(tokens[i]));
                    }
                    retVal[x][y] = bp;
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
     * @param board the board
     */
    public static void printBoard(final BoardPiece[][] board) {
        final int rows = board.length;
        final int cols = board[0].length;
        final String tableHeader = new String(new char[cols]).replace("\0", "+---------") + "+\n";
        final StringBuilder sb = new StringBuilder(tableHeader);
        for(int i = rows - 1; i >= 0; i--) {
            for(int j = 0; j < cols; j++) {
                final String types = board[j][i].getTypes().toString();
                sb.append("|")
                        .append(StringUtils.center(String.format("  %s  ", types), 9));
            }
            sb.append("|\n").append(tableHeader);
        }
        System.out.println(sb);
    }
}