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
        final Board s = new Board(new int[]{ 3, 7, 8, 0, 1, 2, 5, 4, 6 }, 3);
        final Board e = new Board(new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 0 }, 8);
        final StringBuilder sb = new StringBuilder("Original board:\n");

        long start = System.currentTimeMillis();
        Node curr = aStar.findPath(new Node(s), new Node(e));

        while(curr != null) {
            sb.append(curr.getBoard()).append("\n");
            curr = curr.getParent();
        }
        System.out.println(sb.append(String.format("Took %d ms", System.currentTimeMillis() - start)));
    }
}