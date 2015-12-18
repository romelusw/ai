package com.romelus;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Finds the shortest path between nodes (initial), (goal) using the A* algorithm.
 *
 * @author romelus
 */
public class AStar {
    /**
     * Largest number attempts before giving up.
     */
    private static final int MAX_ATTEMPTS = 20000;

    /**
     * Finds the optimal path from the {@param start} state to the {@param goal} state.
     *
     * @param start the start state
     * @param goal  the goal state
     */
    public Node findPath(final Node start, final Node goal) {
        final PriorityQueue<Node> frontier = new PriorityQueue<>((Comparator) (o1, o2) -> {
            if (((Node) o1).getTotalCost() < ((Node) o2).getTotalCost()) return -1;
            if (((Node) o1).getTotalCost() > ((Node) o2).getTotalCost()) return 1;
            return 0;
        });
        final Set<Node> explored = new HashSet<>();
        Node curr = null;
        int count = 0;

        // Add starting node to the frontier
        frontier.add(start);

        while (!frontier.isEmpty() && count <= MAX_ATTEMPTS) {
            curr = frontier.poll();
            explored.add(curr);

            // Found the goal state
            if (curr.equals(goal)) break;

            // Explore current's neighbors
            for (final Node n : curr.getNeighbors()) {
                final int cost = curr.getCostFromStart() + Node.manhattanDistance(curr, n);

                if (explored.contains(n)) continue; // Skip those that we have already visited

                // The neighbor is smaller than the current node
                if (cost > n.getCostFromStart()) {
                    n.setParent(curr);
                    n.setCostFromStart(cost);
                    n.setCostToGoal(Node.manhattanDistance(n, goal));
                    if (!frontier.contains(n)) {
                        frontier.add(n);
                    }
                }
            }
            count++;
        }

        return count >= MAX_ATTEMPTS ? null : curr;
    }

    public void printPath(Node n, final StringBuilder sb) {
        if (n == null) return;
        printPath(n.getParent(), sb);
        sb.append(n.getBoard()).append("\n");
    }
}