package com.romelus_borucki.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
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
    private static final Pattern BOARD_DIMENSIONS_PATTERN = Pattern.compile("^Size.*(\\d),(\\d+)$");

    public static void main(final String...args) {
        final ClassLoader classLoader = WumpusBoardHelper.class.getClassLoader();
        readBoard(new File(classLoader.getResource("b1.txt").getFile()));
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
     * Reads a specially formatted file to representing a wumpus board.
     *
     * @param f the file
     * @return the resolved board
     */
    public static String[][] readBoard(final File f) {
        String[][] retVal = null;
        try {
            final Scanner s = new Scanner(f);
            int lineNum = 0;
            while (s.hasNextLine()) {
                final String line = s.nextLine();
                if(lineNum == 0) {
                    final Matcher m = BOARD_DIMENSIONS_PATTERN.matcher(line);
                    if (m.find()) {
                        retVal = new String[Integer.parseInt(m.group(0))][Integer.parseInt(m.group(1))];
                    } else {
                        break;
                    }
                } else {
                    final String[] tokens = line.split(",");
                    final int x = Integer.parseInt(tokens[0]);
                    final int y = Integer.parseInt(tokens[1]);
                    final PieceType bp = PieceType.fromString(tokens[2]);
                    retVal[x][y] = bp.literal;
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
    public void printBoard(final String[][] board) {
        final int rows = board.length;
        final int cols = board[0].length;
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                sb.append("");
            }
        }
        System.out.println(sb);
    }
}