package com.romelus_borucki;

import com.romelus_borucki.common.utils.WumpusBoardHelper;
import com.romelus_borucki.logicalAgents.InferenceAgent;

import java.io.File;

/**
 * Object which starts/ends the Wumpus game.
 *
 * @author romelus
 */
public class GameRunner {
    private static final String testFile = GameRunner.class.getResource("b1.txt").getFile();

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String ...args) {
        final WumpusBoardHelper.BoardPiece[][] board = WumpusBoardHelper.readBoard(new File(testFile));
        final InferenceAgent ai = new InferenceAgent(board.length, board[0].length);
        String results = "";

        // Game-loop
        while(!ai.isDead() && !ai.hasExited()) {

        }

        if(ai.isDead()) {
            results = "Unfortunately the Ai has died.";
        } else {
            results = String.format("The Ai has exited the Wumpus world %s", ai.hasGold() ? "with the gold in hand!":  "empty handed :(");
        }

        System.out.println(results);
    }
}