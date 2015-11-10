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
    private static boolean inDevMode = true;
    private static int score, actions;

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String... args) {
        final File test_file = new File("/Users/romelus/Desktop/BU-Compter Science/cs664/ai/wumpus/src/main/resources/b5.txt");
        final WumpusBoardHelper.BoardState gameBoard = WumpusBoardHelper.readBoard(test_file);
        final int NUM_TEST_RUNS = 2000;
        int won = 0, loss = 0, avgScore = 0, avgActionCount = 0;

        // Play several games to see general outcomes
        for (int i = 0; i < (inDevMode ? NUM_TEST_RUNS : 1); i++) {
            if (new GameRunner().playGame(gameBoard) == 0) {
                won++;
            } else {
                loss++;
            }

            // Tally points + Reset scoreboard
            avgScore += (score - actions);
            avgActionCount += actions;
            score = 0;
            actions = 0;
        }

        if (inDevMode) {
            System.out.println(String.format("Avg score: %d, Avg # of actions: %d\nWin/lose ratio %d/%d out of %d games.",
                    avgScore / NUM_TEST_RUNS, avgActionCount / NUM_TEST_RUNS, won, loss, NUM_TEST_RUNS));
        }
    }

    /**
     * Plays the autonomous wumpus game.
     *
     * @return flag indicating if the ai has retrieved the gold successfully
     */
    public int playGame(final WumpusBoardHelper.BoardState gameBoard) {
        final InferenceAgent ai = new InferenceAgent(gameBoard.getBoard().length, gameBoard.getBoard()[0].length);
        final StringBuilder results = new StringBuilder();
        int winLoss = 0; // 0: Win 1: Loss

        WumpusBoardHelper.BoardPiece currLoc = gameBoard.getBoard()[gameBoard.getAgentYPosition()][gameBoard.getAgentXPosition()];
//        System.out.println("Gameboard:");
//        WumpusBoardHelper.printBoard(gameBoard);

        // Game-loop
        while (!ai.isDead() && !ai.hasExited()) {
            ai.tell(gameBoard.getBoard()[currLoc.getX()][currLoc.getY()]);

            if (inDevMode) {
//                System.out.println(String.format("Ai position: (x:%d, y:%d)", currLoc.getY() + 1, currLoc.getX() + 1));
//                WumpusBoardHelper.printBoard(new WumpusBoardHelper.BoardState(ai.getKnowledgeBase(), currLoc.getY(), currLoc.getX()));
            }

            final WumpusBoardHelper.BoardPiece aiPiece = ai.ask(currLoc);
            currLoc = gameBoard.getBoard()[aiPiece.getX()][aiPiece.getY()];
            // var clockwise = d - c;
            // var cclockwise = (4 - clockwise) % 4;
            // console.log("direction:", clockwise < cclockwise ? 1 : -1, " amount:", Math.min(clockwise, cclockwise));
            actions++;
        }

        // Results
        if (ai.isDead()) {
            results.append("Unfortunately the Ai has died.");
            if (ai.eatenByWumpus) {
                results.append(" ༼⍨༽ is full.");
            } else {
                results.append(" Fallen and can't get out of the pit.");
            }
            winLoss = 1;
        } else {
            results.append("The Ai has exited the Wumpus world.");
            if (ai.hasGold()) {
                score += 1000;
                results.append(" With gold in hand (•‿•)");
            }
        }

        if (!inDevMode) {
            WumpusBoardHelper.printBoard(new WumpusBoardHelper.BoardState(ai.getKnowledgeBase(), currLoc.getY(), currLoc.getX()));
            System.out.println(results);
        }

        return winLoss;
    }
}