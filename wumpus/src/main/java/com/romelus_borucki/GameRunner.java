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
        final WumpusBoardHelper.BoardPiece[][] board = WumpusBoardHelper.readBoard(new File(GameRunner.class.getClassLoader().getResource("b2.txt").getFile()));
        final InferenceAgent ai = new InferenceAgent(board.length, board[0].length);
        final StringBuilder results = new StringBuilder();
        int score = 0, actions = 0;

        WumpusBoardHelper.BoardPiece currLoc = findEntrance(board);
        WumpusBoardHelper.printBoard(board);

        // Game-loop
        while(!ai.isDead() && !ai.hasGold()) {
            System.out.println("Ai position: " + (currLoc.getY() + 1) + "," + (currLoc.getX() + 1));
            ai.tell(currLoc);
            WumpusBoardHelper.printBoard(ai.getKnowledgeBase());
            WumpusBoardHelper.BoardPiece aiPiece = ai.ask(currLoc);
            currLoc = board[aiPiece.getX()][aiPiece.getY()];
            // var clockwise = d - c;
            // var cclockwise = (4 - clockwise) % 4;
            // console.log("direction:", clockwise < cclockwise ? 1 : -1, " amount:", Math.min(clockwise, cclockwise));
            actions++;
        }

        if(ai.isDead()) {
            results.append("Unfortunately the Ai has died.");
            if(ai.eatenByWumpus) {
                results.append(" ༼⍨༽ is full.");
            } else {
                results.append(" Fallen and can't get out of the pit.");
            }
        } else {
            results.append("The Ai has exited the Wumpus world.");
            if(ai.hasGold()) {
                score += 1000;
                results.append(" With gold in hand (•‿•)");
            }
        }
        System.out.println(results + String.format(" Total score: %d, Number of actions: %d", score - actions, actions));
    }

    public static WumpusBoardHelper.BoardPiece findEntrance(WumpusBoardHelper.BoardPiece[][] board) {
        WumpusBoardHelper.BoardPiece retVal = null;
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j].hasType(WumpusBoardHelper.PieceType.Enter)) {
                   return board[i][j];
                }
            }
        }
        return retVal;
    }
}