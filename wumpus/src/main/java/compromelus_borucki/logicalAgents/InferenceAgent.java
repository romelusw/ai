package com.romelus_borucki.logicalAgents;

import com.romelus_borucki.common.structures.Implication;
import com.romelus_borucki.common.structures.Stack;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardPiece;
import com.romelus_borucki.common.utils.WumpusBoardHelper.PieceType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An agent capable of inferring facts from its wumpus world.
 *
 * @author romelus
 */
public class InferenceAgent {
    /**
     * Flag indicating if the agent has been eaten by the wumpus.
     */
    public boolean eatenByWumpus;
    /**
     * Flag indicating if the agent has fell into a pit.
     */
    public boolean fellIntoPit;
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
     * Predefined implication rules.
     */
    private static Map<PieceType, Implication<PieceType>> implicationMap = new HashMap<>();

    /**
     * Add rules.
     */
    static {
        // Direct relation inferences
        implicationMap.put(PieceType.Enter, new Implication<>(Implication.Operator.NONE, PieceType.Ok));
        implicationMap.put(PieceType.Safe, new Implication<>(Implication.Operator.NONE, PieceType.Ok));
        implicationMap.put(PieceType.Breezy, new Implication<>(Implication.Operator.NONE, PieceType.QPit));
        implicationMap.put(PieceType.Stench, new Implication<>(Implication.Operator.NONE, PieceType.QWump));

        // Neighbor relation inferences
        implicationMap.put(PieceType.Pit, new Implication<>(Implication.Operator.AND, PieceType.Breezy, PieceType.QPit, PieceType.QWump));
        implicationMap.put(PieceType.Wumpus, new Implication<>(Implication.Operator.AND, PieceType.Stench, PieceType.QWump, PieceType.QPit));
    }

    /**
     * Default constructor.
     *
     * @param rows the number of rows to build up KB
     * @param cols the number of cols to build up KB
     */
    public InferenceAgent(final int rows, final int cols) {
        knowledgeBase = new BoardPieceEnhanced[rows][cols];
        breadCrumbs = new Stack<>(rows * cols);
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
        BoardPiece backtrack = null;
        BoardPiece wumpusDirection = null;

        // End game scenarios
        if (bp.hasType(PieceType.Wumpus)) {
            eatenByWumpus = true;
            return bp;
        } else if (bp.hasType(PieceType.Pit)) {
            fellIntoPit = true;
            return bp;
        } else if (bp.hasType(PieceType.Gold)) {
            hasGold = true;
        }

        if (!hasGold) {
            // Find a safe neighbor
            for (int i = 0; i < numNeighbors; i++) {
                // Allow for random selection from neighbors
                final BoardPieceEnhanced neighbor = neighbors.get(new Random().nextInt(neighbors.size()));
                final boolean isSafePiece = neighbor.isSafe();
                if (isSafePiece && neighbor.getVisitCount() == 0) {
                    retVal = neighbor;
                    break;
                } else if (isSafePiece && neighbor.getVisitCount() > 0) {
                    backtrack = neighbor;
                } else if (neighbor.hasType(PieceType.Stench)) {
                    wumpusDirection = neighbor;
                }
                neighbors.remove(neighbor);
            }

            // Move backwards if frontier is uncertain
            if (retVal == null) {
                retVal = backtrack;
            }

            // Arrow scenario
            if (retVal == null && backtrack == null) {
                System.out.println("Arrow shot within row/col: " + wumpusDirection.getX() + "," + wumpusDirection.getY());
                System.exit(1);
            }
        } else {
            if (breadCrumbs.size() != 0) {
                return breadCrumbs.pop();
            } else {
                hasExited = true;
                return bp;
            }
        }

        // Store move for exiting the cave
        breadCrumbs.push(bp);
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

        // Update the KB with the type found from the game board
        bpe.setType(bp.getTypes().isEmpty() ? Arrays.asList(PieceType.Safe) : bp.getTypes());

        // Skip those we have already confirmed
        if (bpe.isGameEnding() || bpe.isConfirmed() && bpe.getVisitCount() > 1) {
            return;
        }

        // TODO: Improve inaccurate inferencing (eg, piece w/two neighbors which are breezy are treated as pits)
        // Inferences on neighbors
        for (final BoardPieceEnhanced neighbor : getNeighbors(bp)) {
            if (neighbor.getTypes().isEmpty()) {
                neighbor.addType((PieceType[]) bpe.getImplication().getImplies().toArray());
            } else if (neighbor.hasType(PieceType.QPit) || neighbor.hasType(PieceType.QWump)) {
                neighbor.addType((PieceType[]) bpe.getImplication().getImplies().toArray());
                final Set<Map.Entry<PieceType, PieceType>> assumedTypes = assumeTypes(neighbor.getTypes()).entrySet();
                for (final Map.Entry<PieceType, PieceType> type : assumedTypes) {
                    final Set<PieceType> neighborTypes = neighbor.getTypes();
                    if (runInference(implicationMap.get(type.getValue()), getNeighbors(neighbor))) {
                        // Confirm
                        neighbor.addType(type.getValue());
                    } else {
                        // Negate
                        if (neighborTypes.size() <= 1) { // A piece that is "OK" should only contain one type
                            neighborTypes.add(PieceType.Ok);
                        }
                    }
                    neighborTypes.remove(type.getKey());
                }
            }
        }
        bpe.addVisit();
    }

    /**
     * Assume a questionable pieces to be true.
     *
     * @param types the questionable types
     * @return the assumed opposites
     */
    public Map<PieceType, PieceType> assumeTypes(final Set<PieceType> types) {
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
    public boolean runInference(final Implication<PieceType> implies, final List<BoardPieceEnhanced> neighbors) {
        boolean retVal;
        final Stream nStream = neighbors.stream();
        final Predicate<BoardPieceEnhanced> predicate = n -> n.getTypes().isEmpty() || n.hasType(PieceType.Ok) || n.hasType((PieceType[]) implies.getImplies().toArray());
        if (implies.getOperator().equals(Implication.Operator.AND)) {
            retVal = nStream.allMatch(predicate);
        } else {
            retVal = nStream.anyMatch(predicate);
        }
        return retVal;
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
        if(withinWidth(northX) && withinHeight(northY)) {
            if(knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        // South
        northX = bp.getX(); northY = bp.getY() - 1;
        if(withinWidth(northX) && withinHeight(northY)) {
            if(knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        // East
        northX = bp.getX() + 1; northY = bp.getY();
        if(withinWidth(northX) && withinHeight(northY)) {
            if(knowledgeBase[northX][northY] == null) {
                knowledgeBase[northX][northY] = new BoardPieceEnhanced(northX, northY);
            }
            neighbors.add(knowledgeBase[northX][northY]);
        }
        // West
        northX = bp.getX() - 1; northY = bp.getY();
        if(withinWidth(northX) && withinHeight(northY)) {
            if(knowledgeBase[northX][northY] == null) {
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