package com.romelus;

/**
 * Game board class.
 *
 * @author romelus
 */
public class Board {
    /**
     * The game board.
     */
    private int[] gameboard;
    /**
     * Location of space.
     */
    private int holeIndex;
    /**
     * Size of each row.
     */
    private int rowSize;

    /**
     * Default constructor.
     *
     * @param board      initial board configuration
     * @param spaceIndex initial location of the empty space
     */
    public Board(final int[] board, final int spaceIndex) {
        rowSize = (int) Math.sqrt(board.length);
        if (board.length >= 2) {
            gameboard = board;
            holeIndex = spaceIndex;
        } else {
            throw new IllegalArgumentException("Board should at least contain '2' elements");
        }
    }

    /**
     * Getter for game board.
     *
     * @return the game board
     */
    public int[] getGameboard() {
        return gameboard;
    }

    /**
     * Getter for hole index.
     *
     * @return the hole index
     */
    public int getHoleIndex() {
        return holeIndex;
    }

    /**
     * Getter for the row size.
     *
     * @return the row size
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     * Slides the empty space into a valid direction.
     *
     * @param b the {@link Board}
     * @param d the {@link Direction}
     * @return the modified board
     */
    public static Board slideHole(final Board b, final Direction d) {
        final int idx = b.holeIndex;
        final int rowSize = b.rowSize;
        int[] copy = b.getGameboard().clone();
        int newHoleIndex = 0;

        if (d.equals(Direction.UP) && idx - rowSize >= 0) {
            newHoleIndex = idx - rowSize;
            swap(copy, idx, newHoleIndex);
        } else if (d.equals(Direction.DOWN) && idx + rowSize <= copy.length - 1) {
            newHoleIndex = idx + rowSize;
            swap(copy, idx, newHoleIndex);
        } else if (d.equals(Direction.LEFT) && idx - 1 >= 0) {
            newHoleIndex = idx - 1;
            swap(copy, idx, newHoleIndex);
        } else if (d.equals(Direction.RIGHT) && idx + 1 <= copy.length - 1) {
            newHoleIndex = idx + 1;
            swap(copy, idx, newHoleIndex);
        } else {
            copy = null;
        }
        return copy == null ? null : new Board(copy, newHoleIndex);
    }

    /**
     * Swaps the elements at the specified positions.
     *
     * @param b    board to do swapping
     * @param from index to swap from
     * @param to   index to swap to
     */
    private static void swap(final int[] b, final int from, final int to) {
        int copy = b[from];
        b[from] = b[to];
        b[to] = copy;
    }

    @Override
    public String toString() {
        final String header = new String(new char[10 + rowSize]).replace('\0', '-').concat("\n");
        final StringBuilder sb = new StringBuilder().append(header);
        int row = 1;

        for(int i = 0; i < gameboard.length; i++) {
            sb.append("| ").append(gameboard[i] == 0 ? " " : gameboard[i]).append(" ");
            if(row == rowSize) {
                sb.append("| \n").append(header);
                row = 0;
            }
            row++;
        }
        return sb.toString();
    }

    /**
     * Enumeration of directions allowed within {@link Board}.
     */
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}