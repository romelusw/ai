package com.romelus_borucki.logicalAgents;

import com.romelus_borucki.GameRunner;
import com.romelus_borucki.common.structures.Implication;
import com.romelus_borucki.common.structures.Stack;
import com.romelus_borucki.common.utils.WumpusBoardHelper;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardPiece;
import com.romelus_borucki.common.utils.WumpusBoardHelper.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * An agent capable of inferring facts from its wumpus world.
 *
 * @author romelus
 */
public class InferenceAgent {
    /**
     * Flag indicating if the agent has been eaten by the wumpus.
     */
    private boolean eatenByWumpus;
    /**
     * Flag indicating if the agent has fell into a pit.
     */
    private boolean fellIntoPit;
    /**
     * Flag indicating that the agent has left the wumpus world safely.
     */
    private boolean hasExited;
    /**
     * Flag indicating that the agent has captured the gold.
     */
    private boolean hasGold;
    /**
     * Internal state of the agents knowledge.
     */
    private BoardPieceEnhanced[][] knowledgeBase;
    /**
     * A stack to help the ai exit.
     */
    private Stack<BoardPiece> breadCrumbs;
    /**
     * The controlling game runner.
     */
    private GameRunner gameRunner;
    /**
     * Flag indicating whether arrow was shot.
     */
    private boolean hasArrow = true;
    /**
     * Flag indicating if the wumpus is still alive.
     */
    private boolean wumpusIsDead = false;
    /**
     * Predefined implication rules.
     */
    private static Map<PieceType, Implication<PieceType>> implicationMap = new HashMap<>();
    /**
     * Add rules.
     */
    static {
        // Direct relation inferences
        implicationMap.put(PieceType.Enter, new Implication<>(Implication.Operator.NONE, PieceType.Ok));
        implicationMap.put(PieceType.Ok, new Implication<>(Implication.Operator.NONE, PieceType.Ok));
        implicationMap.put(PieceType.Safe, new Implication<>(Implication.Operator.NONE, PieceType.Ok));
        implicationMap.put(PieceType.Breezy, new Implication<>(Implication.Operator.NONE, PieceType.QPit));
        implicationMap.put(PieceType.Stench, new Implication<>(Implication.Operator.NONE, PieceType.QWump));

        // Neighbor relation inferences
        implicationMap.put(PieceType.Pit, new Implication<>(Implication.Operator.AND, PieceType.Breezy));
        implicationMap.put(PieceType.Wumpus, new Implication<>(Implication.Operator.AND, PieceType.Stench));
    }

    /**
     * Default constructor.
     *
     * @param rows   the number of rows to build up KB
     * @param cols   the number of cols to build up KB
     * @param runner the game runner in control
     */
    public InferenceAgent(final int rows, final int cols, final GameRunner runner) {
        knowledgeBase = new BoardPieceEnhanced[rows][cols];
        breadCrumbs = new Stack<>(rows * cols);
        gameRunner = runner;
    }

    /**
     * Provides a "next-move" for the provided current piece.
     *
     * @param bp the piece
     * @return a piece to move from the specified {@param bp}
     */
    public BoardPiece ask(final BoardPiece bp) {
        final List<BoardPieceEnhanced> neighbors = getNeighbors(bp);
        final int numNeighbors = neighbors.size();
        BoardPiece retVal = null;
        BoardPieceEnhanced backtrack = null;

        // End game scenarios
        if (bp.hasType(PieceType.Wumpus)) {
            eatenByWumpus = true;
            return bp;
        } else if (bp.hasType(PieceType.Pit)) {
            fellIntoPit = true;
            System.exit(1);
            return bp;
        } else if (bp.hasType(PieceType.Gold)) {
            hasGold = true;
        }

        if (!hasGold) {
            // Find a safe neighbor
            for (int i = 0; i < numNeighbors; i++) {
                // Allow for random selection from neighbors
                final BoardPieceEnhanced neighbor = neighbors.get(new Random().nextInt(neighbors.size()));

                if (neighbor.isSafe()) {
                    if(neighbor.getVisitCount() == 0) { // Safe + Least visited
                        retVal = neighbor;
                        break;
                    } else {
                        backtrack = (backtrack == null || backtrack.getVisitCount() > neighbor.getVisitCount()) ? neighbor : backtrack;
                    }
                } else if(neighbor.hasType(PieceType.Breezy)) { // If all else fails walk into the breeze
                    backtrack = neighbor;
                }
                neighbors.remove(neighbor);
            }

            // Move backwards if frontier is uncertain
            retVal = retVal == null ? backtrack : retVal;
        } else {
            if (bp.hasType(PieceType.Enter)) {
                hasExited = true;
                return bp;
            } else {
                return breadCrumbs.pop();
            }
        }

        // Store move for exiting the cave
        breadCrumbs.push(knowledgeBase[bp.getX()][bp.getY()]);
        return retVal;
    }

    /**
     * Updates the knowledge base with the pieces state.
     *
     * @param bp the piece
     */
    public void tell(final BoardPiece bp) {
        BoardPieceEnhanced bpe = knowledgeBase[bp.getX()][bp.getY()];
        // Only applicable to Enter piece
        if (bpe == null) {
            bpe = knowledgeBase[bp.getX()][bp.getY()] = new BoardPieceEnhanced(bp.getX(), bp.getY());
        }

        // Skip end game pieces
        if (hasGold || bpe.isGameEnding()) {
            return;
        }

        // Tally visit
        bpe.addVisit();

        // Update the KB with the type found from the game board if we do not know about it
        bpe.setType(bp.getTypes().isEmpty() ? Arrays.asList(PieceType.Safe) : bp.getTypes());

        // Clear types leading to wumpus if already dead
        clearWumpusStates(bpe);

        if (!hasGold && !bpe.hasType(PieceType.Gold)) {
            // TODO: Improve inaccurate inferencing (eg, piece w/two neighbors which are breezy are treated as pits)
            // Inferences on neighbors
            for (final BoardPieceEnhanced neighbor : getNeighbors(bp)) {
                if (neighbor.getTypes().isEmpty()) {
                    neighbor.addType((PieceType[]) bpe.getImplication().getImplies().toArray());
                } else if (neighbor.hasType(PieceType.QPit, PieceType.QWump)) {
                    neighbor.addType((PieceType[]) bpe.getImplication().getImplies().toArray());
                    final Set<Map.Entry<PieceType, PieceType>> assumedTypes = assumeTypes(neighbor.getTypes()).entrySet();
                    for (final Map.Entry<PieceType, PieceType> type : assumedTypes) {
                        final List<BoardPieceEnhanced> neighbors = getNeighbors(neighbor);
                        // Do we have enough information about this piece to do inferencing?
                        if(neighbors.stream().filter(n -> !n.getTypes().isEmpty() &&
                                !n.hasType(PieceType.Ok, PieceType.QPit, PieceType.QWump, PieceType.Pit)).count() > 1) {
                            if (runInference(implicationMap.get(type.getValue()), neighbors)) {
                                // Confirm
                                neighbor.addType(type.getValue());

                                // Naively shoot the wumpus as soon as possible
                                if (hasArrow && type.getValue().equals(PieceType.Wumpus)) {
                                    if (gameRunner.shootArrow(bp, optimalDirection(bp, neighbor))) {
                                        neighbor.getTypes().clear();
                                        neighbor.addType(PieceType.Safe);
                                        wumpusIsDead = true;
                                    }
                                    hasArrow = false;
                                }
                            }
                            neighbor.getTypes().remove(type.getKey());
                            if(neighbor.getTypes().isEmpty()) {
                                neighbor.addType(PieceType.Ok);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Assume a questionable pieces to be true.
     *
     * @param types the questionable types
     * @return the assumed opposites
     */
    private Map<PieceType, PieceType> assumeTypes(final Set<PieceType> types) {
        final Map<PieceType, PieceType> retVal = new HashMap<>(types.size());
        for (final PieceType type : types) {
            switch (type) {
                case QPit:
                    retVal.put(type, PieceType.Pit);
                    break;
                case QWump:
                    retVal.put(type, PieceType.Wumpus);
                    break;
            }
        }
        return retVal;
    }

    /**
     * Applies inferencing logic on a set of pieces.
     *
     * @param implies   the implication to test
     * @param neighbors the items to test against
     * @return flag indicating if inference passed
     */
    private boolean runInference(final Implication<PieceType> implies, final List<BoardPieceEnhanced> neighbors) {
        return neighbors.stream().filter(n -> !n.getTypes().isEmpty() && n.hasType((PieceType[]) implies.getImplies().toArray())).count() > 1;
    }

    /**
     * Getter for whether the player has died.
     *
     * @return flag
     */
    public boolean isDead() {
        return eatenByWumpus || fellIntoPit;
    }

    /**
     * Getter for hasExited.
     *
     * @return hasExited
     */
    public boolean hasExited() {
        return hasExited;
    }

    /**
     * Getter for eatenByWumpus.
     *
     * @return eatenByWumpus
     */
    public boolean isEatenByWumpus() {
        return eatenByWumpus;
    }

    /**
     * Getter for hasGold.
     *
     * @return hasGold
     */
    public boolean hasGold() {
        return hasGold;
    }

    /**
     * Retrieve the neighbors for a piece.
     *
     * @param bp the board piece
     * @return it's neighbors
     */
    private List<BoardPieceEnhanced> getNeighbors(final BoardPiece bp) {
        final List<BoardPieceEnhanced> neighbors = new ArrayList<>();

        // North
        int northX = bp.getX(), northY = bp.getY() + 1;
        if (withinWidth(northX) && withinHeight(northY)) {
            if (knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        // South
        northX = bp.getX();
        northY = bp.getY() - 1;
        if (withinWidth(northX) && withinHeight(northY)) {
            if (knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        // East
        northX = bp.getX() + 1;
        northY = bp.getY();
        if (withinWidth(northX) && withinHeight(northY)) {
            if (knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        // West
        northX = bp.getX() - 1;
        northY = bp.getY();
        if (withinWidth(northX) && withinHeight(northY)) {
            if (knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        return neighbors;
    }

    /**
     * Getter for the knowledge base.
     *
     * @return the knowledge base
     */
    public BoardPieceEnhanced[][] getKnowledgeBase() {
        return knowledgeBase;
    }

    /**
     * Determine if a location fits within the horizontal bounds of the board.
     *
     * @param x the x-location
     * @return flag indicating if within width
     */
    private boolean withinWidth(final int x) {
        final int rows = knowledgeBase.length;
        return x >= 0 && x < rows;
    }

    /**
     * Determine if a location fits within the vertical bounds of the board.
     *
     * @param y the y-location
     * @return flag indicating if within height
     */
    private boolean withinHeight(final int y) {
        final int cols = knowledgeBase[0].length;
        return y >= 0 && y < cols;
    }

    /**
     * Removes traces of the wumpus, if it has died.
     *
     * @param bp the piece to scrub
     */
    private void clearWumpusStates(final BoardPiece bp) {
        if(wumpusIsDead) {
            bp.getTypes().remove(PieceType.Stench);
            bp.getTypes().remove(PieceType.QWump);
            bp.getTypes().remove(PieceType.Wumpus);
            if(bp.getTypes().isEmpty()) {
                bp.addType(PieceType.Ok);
            }
        }
    }

    /**
     * Determine the direction necessary to face a given piece.
     *
     * @param start the origin piece
     * @param end   the destination piece
     * @return the direction in order to reach the end piece
     */
    private WumpusBoardHelper.Direction optimalDirection(final BoardPiece start, final BoardPiece end) {
        WumpusBoardHelper.Direction retVal = null;
        if (end.getY() > start.getY() && start.getX() == end.getX()) { // EAST
            retVal = WumpusBoardHelper.Direction.East;
        } else if (end.getY() < start.getY() && start.getX() == end.getX()) { // WEST
            retVal = WumpusBoardHelper.Direction.West;
        } else if (end.getX() > start.getX() && end.getY() == start.getY()) { // NORTH
            retVal = WumpusBoardHelper.Direction.North;
        } else {
            retVal = WumpusBoardHelper.Direction.South;
        }
        return retVal;
    }

    /**
     * Wrapper class to maintain visit count.
     */
    private class BoardPieceEnhanced extends BoardPiece {
        /**
         * The amount of times the piece has been visited.
         */
        private int visitCount = 0;

        /**
         * Default constructor.
         *
         * @param xloc the x-position of the piece on the board
         * @param yloc the y-position of the piece on the board
         */
        public BoardPieceEnhanced(final int xloc, final int yloc) {
            super(xloc, yloc);
        }

        /**
         * Increments the visit count.
         */
        public void addVisit() {
            visitCount++;
        }

        /**
         * Getter for the visit count.
         *
         * @return the visit count
         */
        public int getVisitCount() {
            return visitCount;
        }

        /**
         * Getter for the pieces implications.
         *
         * @return the implication
         */
        public Implication<PieceType> getImplication() {
            return implicationMap.get(getTypes().toArray()[0]);
        }

        /**
         * Sets the type for the piece.
         *
         * @param types the types to set
         */
        public void setType(final Collection<PieceType> types) {
            super.getTypes().clear();
            super.getTypes().addAll(types);
        }
    }
}