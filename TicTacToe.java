package bu.edu.cs664;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * CS-664 AI Class Lab - 9/24/15
 * 
 * Artificial Intelligence Agent vs Human
 * Tic-Tac-Toe game.
 * 
 * This is a simple board game of Tic-Tac-Toe
 * played with an AI agent vs a human. The game
 * starts and either the Human or the Agent is 
 * selected to go first.
 * 
 * The Human always has a mark of 'O' and the
 * AI Agent the 'X' mark.
 * 
 * All games will end with either the AI Agent
 * winning or a tie.
 * 
 * THE HUMAN SIMPLY CANNOT BEAT THE AI AGENT!
 * 
 * At the end of a game, you will be asked if
 * you would like to play again. Just hit 'Enter'
 * or any key and you will start again.
 *
 * Implemented by Terry O'Neill (ton@bu.edu) with
 * strategy help from Woody Romelus (romelus.w@gmail.com).
 * 
 */
public class TicTacToe {
	Player human = null;
	Player aiAgent = null;
	Board board = null;
	
	/**
	 * Main execution method.
	 * No args accepted.
	 */
	public static void main(String[] args)
	{
		Scanner keyboard = new Scanner(System.in);
		boolean done = false;
		
		/*
		 * Iterate over each game, allowing the user to
		 * play again or quit. 
		 */
		do 
		{
			TicTacToe ttt = new TicTacToe();
			ttt.playGame();
			
			System.out.println("Would you like to play a game? Type 'n' or 'N' to exit.");
			String again = keyboard.nextLine();
			if (again.trim().equalsIgnoreCase("n"))
			{
				done = true;
			}
		}
		while(!done);
	}
	
	/**
	 * Main game playing logic.
	 * 
	 */
	public void playGame()
	{
		// Initialize new objects
		human = new Human();
		aiAgent = new Agent();
		board = new Board();
		
		// Determine who goes first
		Player[] players = new Player[2];
		players[0] = whoGoesFirst(human, aiAgent);
		players[1] = (players[0].equals(human)) ? aiAgent : human;
		
		// Iterate until the games over
		boolean gameOver = false;
		while(!gameOver)
		{
			for (int i = 0; i <= 1; i++)
			{
				// print out the board
				board.print();
				
				// One of the two players take their turn
				players[i].takeTurn();
				
				// Check to see if somebody won!
				if (gameOver = board.hasAnyPlayerWon()) 
				{
					System.out.print("!!!!! " + players[i].getName() + " wins the game!!!!!");
					board.print();
					break;
				}
				// or maybe there is a tie!
				else if (gameOver = board.isThereATie()) 
				{
					System.out.print("Game is tied.");
					board.print();
					break;
				}

			}
		}
	}
	
	/**
	 * Selects a random player to go first.
	 * @param one player one
	 * @param two player two
	 * @return player one or two who goes first
	 */
	Player whoGoesFirst(Player one, Player two)
	{
		Long random = System.currentTimeMillis() % 2;
		Player selected = (random == 1) ? one : two;
		System.out.println(selected.getName() + " with mark '" + selected.getMark() + "' goes first!");
		return selected;
	}
	
	/**
	 * This object is the AI Agent Player.
	 *
	 */
	class Agent implements Player {

		static final String MARK = "X"; 
		
		boolean playingTheCorners = false;
		
		/**
		 * Agent strategy is embodied in this method.
		 * 
		 */
		public void takeTurn() {
			int[] opponentCanWinPosition = null;
			int[] iCanWinPosition = null;
			
			// First, try to win the board
			if ((iCanWinPosition = iCanWin()) != null)
			{
				moveToWin(iCanWinPosition);
			}
			// Next, try to block opponent
			else if ((opponentCanWinPosition = opponentCanWin()) != null)
			{
				moveToBlock(opponentCanWinPosition);
			}
			else {
				// No chance to win or block, so
				// choose a strategic move
				moveStrategically();
			}
		}
		
		/**
		 * Can the opponent win in any winning position on the board?
		 * Iterates through all possible winning positions and returns the first
		 * possible winning position tuple opponent can win at, or null if he
		 * can't win at any position.
		 * 
		 * Winning means two positions of a winning position are occupied by
		 * a single players mark, and the other position in the tuple is empty.
		 *
		 */
		private int[] opponentCanWin() {
			for (int[] winning : board.getWinningPositions())
			{
				// This checks to see if the my mark is 
				// missing from the position
				if (board.canWinPosition(winning, Agent.MARK))
				{
					return winning;
				}
			}
			return null;
		}

		/**
		 * Return the first position on the board in which I have 2 marks,
		 * and my opponent has none, or null, if no such positions exist.
		 * 
		 * Winning means two positions of a winning position are occupied by
		 * a single players mark, and the other position in the tuple is empty.
		 * 
		 */
		private int[] iCanWin() {	
			for (int[] winning : board.getWinningPositions())
			{
				// This checks to see if the opponents mark is 
				// missing from the position
				if (board.canWinPosition(winning, Human.MARK))
				{
					return winning;
				}
			}
			return null;
		}

		/**
		 * Moving strategically is done when the AI Agent is
		 * not moving to win a position, or block a position.
		 * 
		 * Moving strategically means
		 * 1) When we move first, play the corners in two consecutive
		 *    moves.
		 * 2) When the opponent moves first, play the middle position.
		 *
		 */
		private void moveStrategically() 
		{
			if (board.isEmpty())
			{
				// We're first, play the corners strategy
				board.markAvailablePosition(board.getCorners(), Agent.MARK);
				playingTheCorners = true;
			}
			else if (playingTheCorners)
			{
				// We've started playing the corners, so play 2 of them
				board.markAvailablePosition(board.getCorners(), Agent.MARK);
				playingTheCorners = false;
			}
			else if (board.numPositionsMarked() == 1)
			{
				// The opponent has gone first, if they
				// have not taken the middle position, then
				// we will claim it. If they have, we will
				// play the corners
				if (board.getPosition(board.getMiddlePosition()) == null)
				{
					board.markPosition(board.getMiddlePosition(), Agent.MARK);
				}
				else {
					board.markAvailablePosition(board.getCorners(), Agent.MARK);
					playingTheCorners = true;
				}
			}
			else {
				// there are marks on the board, so choose
				// a winningPosition tuple that we already have
				// a position in
				for (int[] position : board.getWinningPositions())
				{
					if (board.positionHasMark(position, Agent.MARK))
					{
						if (board.hasEmptySpot(position)) 
						{
							board.markAvailablePosition(position, Agent.MARK);
							break;
						}
					}
				}
			}
		}
		
		/**
		 * Make a move to block the opponent with our mark.
		 *
		 */
		private void moveToBlock(int[] opponentCanWinPosition) {
			board.markAvailablePosition(opponentCanWinPosition, Agent.MARK);
		}
		
		/**
		 * Make a move to win at this position.
		 *
		 */
		private void moveToWin(int[] iCanWinPosition) {
			board.markAvailablePosition(iCanWinPosition, Agent.MARK);
		}
		
		public String getMark() {
			return MARK;
		}
		
		public String getName() {
			return "Artificial Intelligence Agent";
		}
	}
	
	/**
	 * This Object represents the Human player.
	 *
	 */
	class Human implements Player {

		static final String MARK = "O";
		
		public void takeTurn() {
			Scanner keyboard = new Scanner(System.in);
			boolean done = false;
			
			do {
				System.out.println("Enter an empty position from 1 to 9 to place your mark: ");
				String pos = keyboard.nextLine();
				
				// nothing entered
				if (pos.trim().length() == 0)
				{
					continue;
				}

				Integer numPos = null;
				try {
					numPos = Integer.parseInt(pos);
				} 
				catch (NumberFormatException e) {
					System.out.println("Invalid numeric entry, please try again.");
					continue;
				}
				
				// Is the input between 1 and 9
				if (numPos < 1 || numPos > 9)
				{
					System.out.println("Invalid entry, position must be between 1 and 9.");
					continue;
				}
				
				// Humans enter the board position as a number from 1-9,
				// but we use 0-based array indexing so adjust the position
				// by 1
				numPos--;
				
				// Is something already on the board in that position?
				if (board.getPosition(numPos) != null)
				{
					System.out.println("Invalid entry, that position is marked, try again.");
					continue;
				}
				
				// Mark the position
				board.markPosition(numPos, Human.MARK);
				
				done = true;
			}
			while(!done);
		}

		public String getMark() {
			return MARK;
		}
		
		public String getName() {
			return "Human Being";
		}
	}
		
	interface Player {
		public void takeTurn();
		public String getMark();
		public String getName();	
	}
	
	/**
	 * Board class represents the playing grid, or tic-tac-toe
	 * board.
	 * 
	 * NOTE: The human user inputs board positions as 1-9 
	 *       representing positions on the board. However,
	 *       the actual board positions are 0-based, ie.
	 *       0-8.
	 */
	class Board {
		private String[] board = new String[9];
		
		/*
		 * These tuples represent the winning positions on
		 * the board when filled by one players mark. 
		 *
		 */
		private int[][] winningPositions = { 
			{0, 1, 2}, // across
			{3, 4, 5},
			{6, 7, 8},
			{0, 3, 6}, // down
			{1, 4, 7},
			{2, 5, 8},
			{0, 4, 8}, // diagonal
			{2, 4, 6}
		};
		// These are the corner positions
		int[] corners = {0, 2, 6, 8};
		
		// Middle position of the board
		int middlePosition = 4;
		
		/**
		 * Print out the board contents.
		 * 
		 */
		public void print()
		{
			System.out.println();
			
			for (int x = 0; x <= 2; x++) 
			{
				for (int y = 0; y <= 2 ; y++)
				{
					System.out.print("_");
					if (board[x * 3 + y] != null) 
					{
						System.out.print(board[x * 3 + y]);
					}
					else 
					{
						System.out.print("_");
					}
					System.out.print("_");
					if (y != 2) {
						System.out.print("|");
					}
				}
				System.out.println();
			}
			System.out.println();
		}
		
		/**
		 * Return the array of tuples that are
		 * the winning positions on the board.
		 * @return
		 */
		public int[][] getWinningPositions()
		{
			return winningPositions;
		}
		
		/**
		 * Return an array indication the positions 
		 * of the corners of the board.
		 */
		public int[] getCorners()
		{
			return corners;
		}
		
		/**
		 * Mark a position on the board.
		 */
		public void markPosition(int position, String mark)
		{
			board[position] = mark;
		}
		
		/**
		 * Get the mark at a position, or null if the position
		 * has not been marked.
		 */
		public String getPosition(int position)
		{
			return board[position];
		}
		
		/**
		 * Return true if the board is empty, false otherwise
		 */
		public boolean isEmpty()
		{
			for(int i = 0; i < board.length; i++)
			{
				if (board[i] != null) 
				{
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Return the number of positions marked on the board.
		 */
		public int numPositionsMarked()
		{
			int count = 0;
			for(int i = 0; i < board.length; i++)
			{
				if (board[i] != null) 
				{
					count++;
				}
			}
			return count;
		}
		
		/**
		 * Does a winning position tuple on the board have a specific mark?
		 * 
		 */
		public boolean positionHasMark(int[] tuple, String mark)
		{
			for(int i = 0; i < tuple.length; i++)
			{
				if (board[tuple[i]] != null && board[tuple[i]].equals(mark))
				{
					return true;
				}
			}
			return false;
		}
		
		/**
		 * This method will fill an empty position in the tuple 
		 * with the mark.
		 */
		public void markAvailablePosition(int[] position, String mark)
		{
			// Iterate through the randomized positions in
			// this tuple to make placement appear random
			for (int i : randomize(position))
			{
				if (board[i] == null)
				{
					board[i] = mark;
					return;
				}
			}
		}
		
		/**
		 * Randomize a position tuple of any length.
		 */
		private List<Integer> randomize(int[] position)
		{
		    List<Integer> intList = new ArrayList<Integer>();
		    for (int i = 0; i < position.length; i++)
		    {
		        intList.add(position[i]);
		    }
		   Collections.shuffle(intList);
		   return intList;
		}
		
		/**
		 * Has any player won the game?
		 *
		 */
		public boolean hasAnyPlayerWon()
		{
			for (int[] winning : winningPositions)
			{
				// Someone has won if for a winning position
				// the positions are not null and all of the
				// positions in the tuple is the same
				if (board[winning[0]] != null &&
					board[winning[1]] != null &&
					board[winning[2]] != null &&
					board[winning[0]].equals(board[winning[1]]) &&
					board[winning[0]].equals(board[winning[2]])) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Is there a tie on the board?
		 *
		 */
		public boolean isThereATie()
		{
			/*
			 *  Are all of the winning positions compromised?
			 *  That is, for each of the winning positions,
			 *  Is there no chance of winning because both
			 *  players have played a position in the tuple.
			 */
			int compromised = 0;
			for (int[] winning : winningPositions)
			{
				if (positionHasMark(winning, Agent.MARK) && 
						positionHasMark(winning, Human.MARK))
				{
					compromised++;
				}
			}
			// If all positions are compromised, no one can win
			return (compromised == winningPositions.length) ? true : false;
		}
		
		/**
		 * Are there any positions where that have 2 marks and they
		 * are not the mark passed in.
		 */
		private boolean canWinPosition(int[] winning, String mark)
		{
			// How many positions are marked
			int positionsMarked = 0;
			for (int i = 0; i <= 2; i++)
			{
				positionsMarked += (board[winning[i]] != null) ? 1 : 0;
			}
			
			// if less then 2 positions marked, no chance of winning
			if (positionsMarked < 2)
			{
				return false;
			}	
			
			// Does any position in the tuple have the mark
			if (positionHasMark(winning, mark)) 
			{
				return false;
			}
			
			// Two positions are filled with opponents marks!
			return true;
		}
		
		/**
		 * Does this tuple have an available spot for a player
		 * to place their mark?
		 */
		public boolean hasEmptySpot(int[] tuple)
		{
			for(int i = 0; i < tuple.length; i++)
			{
				if (board[tuple[i]] == null)
				{
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Return the middle position of the board.
		 */
		public int getMiddlePosition()
		{
			return middlePosition;
		}
	}
}
