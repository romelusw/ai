package com.romelus_borucki.logicalAgents;

import com.romelus_borucki.common.structures.Stack;
import com.romelus_borucki.common.utils.WumpusBoardHelper;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardPiece;
import com.romelus_borucki.common.utils.WumpusBoardHelper.PieceType;

import java.util.List;
import java.util.Random;

//    var clockwise = d - c;
//    var cclockwise = (4 - clockwise) % 4;
//    console.log("direction:", clockwise < cclockwise ? 1 : -1, " amount:", Math.min(clockwise, cclockwise));
//    S   =   "one of your neighbors is"    ?W
//            B   =   "one of your neighbors is"    ?P
//            ?P  =>  "All of your neighbors are"   B
//    OK  =>  "All of your neighbors are"   OK
//    #?W  =>  "All of your neighbors are"   B'
/**
 * An agent capable of inferring facts from its wumpus world.
 *
 * @author romelus
 */
public class InferenceAgent {
    /**
     * Flag indicating if the agent has fallen into a pit or been eaten by the wumpus.
     */
    private boolean isDead;
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
     * Default constructor.
     *
     * @param rows the number of rows to build up KB
     * @param cols the number of cols to build up KB
     */
    public InferenceAgent(final int rows, final int cols) {
        knowledgeBase = new BoardPieceEnhanced[rows][cols];
    }

    /**
     * Provides a "next-move" for the provided current piece.
     *
     * @param bp the piece
     * @return a piece to move from the specified {@param bp}
     */
    public BoardPiece ask(final BoardPiece bp) {
        final List<BoardPieceEnhanced> neighbors = getNeighbors(bp);
        BoardPiece retVal = null;
        BoardPiece backtrack = null;

        for(int i = 0; i < neighbors.size(); i++) {
            // Allow for random selection from neighbors
            final BoardPieceEnhanced item = neighbors.get(new Random().nextInt(neighbors.size()));
            final boolean isSafePiece = item.isSafe();
            if(isSafePiece && item.getVisitCount() == 0) {
                retVal = item;
                break;
            } else if(isSafePiece && item.getVisitCount() > 0) {
                backtrack = item;
            }
        }
        return retVal == null ? backtrack : retVal;
    }

    /**
     * Updates the knowledge base with the pieces state.
     *
     * @param bp the piece
     */
    public void tell(final BoardPiece bp) {
        final BoardPieceEnhanced bph = knowledgeBase[bp.getX()][bp.getY()];
        if(bph.getVisitCount() == 0) {
            for(final BoardPieceEnhanced neighbor : getNeighbors(bp)) {
                if(neighbor.hasType(PieceType.Empty)) {

                }
            }
        }

    }

    /**
     * Getter for isDead.
     *
     * @return isDead
     */
    public boolean isDead() {
        return isDead;
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
        return null;
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
        public BoardPieceEnhanced(int xloc, int yloc) {
            super(xloc, yloc);
        }

        /**
         * Getter for the visit count.
         *
         * @return the visit count
         */
        public int getVisitCount() {
            return visitCount;
        }
    }
}