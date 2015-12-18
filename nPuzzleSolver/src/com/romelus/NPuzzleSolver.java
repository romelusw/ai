package com.romelus;

/**
 * Solves N-Puzzle game using A* algorithm.
 *
 * @author romelus
 */
public class NPuzzleSolver {

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        final AStar aStar = new AStar();
        final Board s = new Board(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 15, 14, 0, 13}, 14);
        final Board e = new Board(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0}, 15);
        final StringBuilder sb = new StringBuilder();

        long start = System.currentTimeMillis();
        final Node goal = aStar.findPath(new Node(s), new Node(e));
        long end = System.currentTimeMillis() - start;

        if (goal != null) {
            aStar.printPath(goal, sb);
            System.out.println(sb.append(String.format("Took %d ms", end)));
        } else {
            System.err.println("Could not find a solution within a reasonable amount of time.");
        }
    }
}