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
    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String ...args) {
        final WumpusBoardHelper.BoardPiece[][] board =
                WumpusBoardHelper.readBoard(new File(GameRunner.class.getClassLoader().getResource("b1.txt").getFile()));
        WumpusBoardHelper.printBoard(board);
        final InferenceAgent ai = new InferenceAgent(board.length, board[0].length);
        int score = 0;
        WumpusBoardHelper.BoardPiece currLoc = board[0][0];
        final StringBuilder results = new StringBuilder();

        // Game-loop
        while(!ai.isDead() && !ai.hasExited()) {
            System.out.println("Curr location: " + currLoc.getX() + "," + currLoc.getY());
            ai.tell(currLoc);
            WumpusBoardHelper.printBoard(ai.getKnowledgeBase());
            WumpusBoardHelper.BoardPiece aiPiece = ai.ask(currLoc);
            currLoc = board[aiPiece.getX()][aiPiece.getY()];
            score--;
        }

        if(ai.isDead()) {
            results.append("Unfortunately the Ai has died.");
            if(ai.eatenByWumpus) {
                results.append("\t༼⍨༽ is full.");
            } else {
                results.append("\tFallen and can't get out of the pit.");
            }
        } else {
            results.append("The Ai has exited the Wumpus world.");
            if(ai.hasGold()) {
                score += 1000;
                results.append("\tWith gold in hand (•‿•)");
            }
        }
        System.out.println(results + String.format(" Total score:%d", score));
    }
}