package com.romelus_borucki;

import com.romelus_borucki.common.utils.WumpusBoardHelper;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardPiece;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardState;
import com.romelus_borucki.logicalAgents.InferenceAgent;

import java.io.File;

/**
 * Object which starts/ends the Wumpus game.
 *
 * @author romelus
 */
public class GameRunner {
    public BoardState boardState;
    private static boolean inDevMode = true;
    private static int score, actions;

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String... args) {
        final File test_file = new File("/Users/romelus/Desktop/BU-Compter Science/cs664/ai/wumpus/src/main/resources/b8.txt");
        final int NUM_TEST_RUNS = 1000;
        int won = 0, loss = 0, avgScore = 0, avgActionCount = 0;

        // Play several games to see general outcomes
        for (int i = 0; i < (inDevMode ? NUM_TEST_RUNS : 1); i++) {
            final BoardState gameBoard = WumpusBoardHelper.readBoard(test_file);
            final GameRunner runner = new GameRunner(gameBoard);
            final InferenceAgent ai = new InferenceAgent(gameBoard.getBoard().length, gameBoard.getBoard()[0].length, runner);
            if (runner.playGame(ai) == 0) {
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
        } else {
            System.out.println(String.format("Score: %d, # of actions: %d", avgScore, avgActionCount));
        }
    }

    /**
     * Default constructor.
     *
     * @param bState the initial board state
     */
    public GameRunner(final BoardState bState) {
        boardState = bState;
    }

    /**
     * Plays the autonomous wumpus game.
     *
     * @return flag indicating if the ai has retrieved the gold successfully
     */
    public int playGame(final InferenceAgent agent) {
        final StringBuilder results = new StringBuilder();
        int winLoss = 0; // 0: Win 1: Loss

        BoardPiece currLoc = boardState.getBoard()[boardState.getAgentYPosition()][boardState.getAgentXPosition()];
        System.out.println("Gameboard:");
        WumpusBoardHelper.printBoard(boardState);

        // Game-loop
        while (!agent.isDead() && !agent.hasExited()) {
            agent.tell(boardState.getBoard()[currLoc.getX()][currLoc.getY()]);

            if(inDevMode) {
                System.out.println(String.format("Ai position: (x:%d, y:%d)", currLoc.getY() + 1, currLoc.getX() + 1));
                WumpusBoardHelper.printBoard(new BoardState(agent.getKnowledgeBase(), currLoc.getY(), currLoc.getX()));
            }

            final BoardPiece aiPiece = agent.ask(currLoc);
            currLoc = boardState.getBoard()[aiPiece.getX()][aiPiece.getY()];
            actions++;
        }

        // Results
        if (agent.isDead()) {
            results.append("Unfortunately the Ai has died.");
            if (agent.isEatenByWumpus()) {
                results.append(" ༼⍨༽ is full.");
            } else {
                results.append(" Fallen and can't get out of the pit.");
            }
            winLoss = 1;
        } else {
            results.append("The Ai has exited the Wumpus world.");
            if (agent.hasGold()) {
                score += 1000;
                results.append(" With gold in hand (•‿•)");
            }
        }

        if (!inDevMode) {
//            WumpusBoardHelper.printBoard(new BoardState(agent.getKnowledgeBase(), currLoc.getY(), currLoc.getX()));
//            System.out.println(results);
        }

        return winLoss;
    }

    /**
     * Shoots an arrow throughout the cave.
     *
     * @param firingPosition the location of the shooter
     * @param dir the direction to shoot the arrow
     * @return flag indicating if the wumpus was killed
     */
    public boolean shootArrow(final BoardPiece firingPosition, final WumpusBoardHelper.Direction dir) {
        BoardPiece currLoc = firingPosition;

        while(currLoc != null) {
            if(currLoc.hasType(WumpusBoardHelper.PieceType.Wumpus)) {
                currLoc.getTypes().clear();
                currLoc.addType(WumpusBoardHelper.PieceType.Safe);
                break;
            } else {
                try {
                    currLoc = boardState.getBoard()[currLoc.getX() + dir.yIncrement][currLoc.getY() + dir.xIncrement];
                } catch(final ArrayIndexOutOfBoundsException aioe) {
                    currLoc = null;
                }
            }
        }
        actions += 10;

        System.out.println((currLoc != null ? "Wumpus is dead!\n" : "Arrow ricocheted off the cave walls\n"));
        return currLoc != null;
    }
}