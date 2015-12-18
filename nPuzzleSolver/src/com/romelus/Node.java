package com.romelus;

import java.util.ArrayList;
import java.util.List;

/**
 * Node class.
 *
 * @author romelus
 */
public class Node {
    private Node parent;
    private Board board;
    private int costFromStart;
    private int costToGoal;
    private int totalCost;

    public Node getParent() {
        return parent;
    }

    public void setParent(final Node p) {
        parent = p;
    }

    public Board getBoard() {
        return board;
    }

    public int getCostFromStart() {
        return costFromStart;
    }

    public void setCostFromStart(int costFromStart) {
        this.costFromStart = costFromStart;
    }

    public int getCostToGoal() {
        return costToGoal;
    }

    public void setCostToGoal(int cost2Goal) {
        costToGoal = cost2Goal;
        totalCost = costFromStart + costToGoal;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public Node(final Board brd) {
        board = brd;
    }

    /**
     * Retrieve all the neighbors surrounding the board.
     *
     * @return the neighbors
     */
    public List<Node> getNeighbors() {
        final List<Node> retVal = new ArrayList<>();
        for (final Board.Direction d : Board.Direction.values()) {
            final Board brd = Board.slideHole(board, d);
            if (brd != null && !brd.equals(board)) {
                retVal.add(new Node(brd));
            }
        }
        return retVal;
    }

    /**
     * Heuristic function which calculates the estimated distance between two nodes.
     *
     * @param start the starting node
     * @param end   the ending node
     * @return the approximate cost between the two nodes
     */
    public static int manhattanDistance(final Node start, final Node end) {
        final int[] startBoard = start.getBoard().getGameboard();
        final int[] endBoard = end.getBoard().getGameboard();
        final int rowSize = (int) Math.sqrt(endBoard.length);
        int sum = 0;

        for (int x = 0; x < endBoard.length; x++) {
            for (int y = 0; y < endBoard.length; y++) {
                if (startBoard[x] != 0 && startBoard[x] == endBoard[y]) {
                    sum += Math.abs((x / rowSize) - (y / rowSize)) + Math.abs((x % rowSize) - (y % rowSize));
                }
            }
        }
        return (int) Math.floor(sum);
    }

    @Override
    public boolean equals(final Object n) {
        if (!(n instanceof Node)) return false;
        if (((Node) n).getBoard().getGameboard().length != board.getGameboard().length) return false;

        final int[] nBoard = ((Node) n).getBoard().getGameboard();
        for (int i = 0; i < nBoard.length; i++) {
            if (nBoard[i] != board.getGameboard()[i]) {
                return false;
            }
        }
        return true;
    }
}