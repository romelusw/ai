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
     * Finds the optimal path from the {@param start} state to the {@param goal} state.
     *
     * @param start the start state
     * @param goal the goal state
     */
    public Node findPath(final Node start, final Node goal) {
        final PriorityQueue<Node> frontier = new PriorityQueue<>((Comparator) (o1, o2) -> ((Node) o1).getTotalCost() - ((Node) o2).getTotalCost());
        final Set<Node> explored = new HashSet<>();

        // Add starting node to the frontier
        frontier.add(start);

        while (frontier.size() > 0) {
            final Node curr = frontier.poll();
            explored.add(curr);

            // Found the goal state
            if (curr.equals(goal)) {
                return curr;
            }

            // Explore current's neighbors
            for (final Node n : curr.getNeighbors()) {
                if(explored.contains(n)) continue; // Ignore nodes already visited
                final int cost = curr.getCostFromStart() + Node.manhattanDistance(curr, n);
                if(!frontier.contains(n)) {
                    n.setParent(curr);
                    n.setCostFromStart(cost);
                    n.setCostToGoal(Node.manhattanDistance(n, goal));
                    frontier.add(n);
                } else {
                    if(n.getTotalCost() > curr.getTotalCost()) {
                        n.setParent(curr);
                        n.setCostFromStart(curr.getCostFromStart());
                    }
                }
            }
        }
        return goal;
    }
}