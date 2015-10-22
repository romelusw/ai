package com.romelus_borucki.logicalAgents;

import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardPiece;

/**
 * Created by romelus on 10/22/15.
 */
public class InferenceAgent {
    //    var clockwise = d - c;
//    var cclockwise = (4 - clockwise) % 4;
//    console.log("direction:", clockwise < cclockwise ? 1 : -1, " amount:", Math.min(clockwise, cclockwise));
//    S   =   "one of your neighbors is"    ?W
//            B   =   "one of your neighbors is"    ?P
//            ?P  =>  "All of your neighbors are"   B
//    OK  =>  "All of your neighbors are"   OK
//    #?W  =>  "All of your neighbors are"   B'
    /**
     * Internal state of the agents knowledge.
     */
    private BoardPiece[][] knowledgeBase;

    /**
     * Default constructor.
     *
     * @param rows the number of rows to build up KB
     * @param cols the number of cols to build up KB
     */
    public InferenceAgent(final int rows, final int cols) {
        knowledgeBase = new BoardPiece[rows][cols];
    }

    /**
     * Provides a "next-move" for the provided current piece.
     *
     * @param bp the piece
     * @return a piece to move from the specified {@param bp}
     */
    public BoardPiece ask(final BoardPiece bp) {

        return null;
    }

    /**
     * Updates the knowledge base with the pieces state.
     *
     * @param bp the piece
     */
    public void tell(final BoardPiece bp) {

    }
}