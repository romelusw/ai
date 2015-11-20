package com.romelus_borucki;

import com.romelus_borucki.common.utils.WumpusBoardHelper;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardPiece;
import com.romelus_borucki.common.utils.WumpusBoardHelper.BoardState;
import com.romelus_borucki.logicalAgents.InferenceAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Object which starts/ends the Wumpus game.
 *
 * @author romelus
 */
public class GameRunner {
    public BoardState boardState;
    private static int score, actions;

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String... args) {
        boolean isPlaying = true;
        final Scanner scanner = new Scanner(System.in);
        while (isPlaying) {
            System.out.println(GameRunner.usage());

            final String line = scanner.nextLine();
            if (line.equalsIgnoreCase("Q")) {
                isPlaying = false;
                System.exit(0);
            } else {
                final File boardFile = new File(line);
                if (boardFile.exists()) {
                    final BoardState gameBoard = WumpusBoardHelper.readBoard(boardFile);
                    final GameRunner runner = new GameRunner(gameBoard);
                    final InferenceAgent ai = new InferenceAgent(gameBoard.getBoard().length, gameBoard.getBoard()[0].length, runner);
                    runner.playGame(ai);
                    System.out.println(String.format("Score: %d, # of actions: %d", score - actions, actions));
                }
            }
            score = 0;
            actions = 0; // Reset score + action count
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

            System.out.println(String.format("Ai position: (x:%d, y:%d)", currLoc.getY() + 1, currLoc.getX() + 1));
            WumpusBoardHelper.printBoard(new BoardState(agent.getKnowledgeBase(), currLoc.getY(), currLoc.getX()));

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

        System.out.println(results);
        return winLoss;
    }

    /**
     * Shoots an arrow throughout the cave.
     *
     * @param firingPosition the location of the shooter
     * @param dir            the direction to shoot the arrow
     * @return flag indicating if the wumpus was killed
     */
    public boolean shootArrow(final BoardPiece firingPosition, final WumpusBoardHelper.Direction dir) {
        BoardPiece currLoc = firingPosition;

        while (currLoc != null) {
            if (currLoc.hasType(WumpusBoardHelper.PieceType.Wumpus)) {
                currLoc.getTypes().clear();
                currLoc.addType(WumpusBoardHelper.PieceType.Safe);
                break;
            } else {
                try {
                    currLoc = boardState.getBoard()[currLoc.getX() + dir.yIncrement][currLoc.getY() + dir.xIncrement];
                } catch (final ArrayIndexOutOfBoundsException aioe) {
                    currLoc = null;
                }
            }
        }
        actions += 10;

        System.out.println((currLoc != null ? "Wumpus is dead!\n" : "Arrow ricocheted off the cave walls\n"));
        return currLoc != null;
    }

    private static String usage() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Welcome to the wumpus game. Please enter the path to the board file:");
        return sb.toString();
    }
}