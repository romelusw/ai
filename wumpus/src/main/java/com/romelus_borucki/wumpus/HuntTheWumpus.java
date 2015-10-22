/*******************************************************************************
 * Hunt The Wumpus - 10/2015
 *  - Brian Borucki - bborucki@bu.edu
 *  - Woody Romelus
 *
 * Quick and dirty implementation of the game Hunt the Wumpus
 *
 * TODO:
 * - Implement shootBow()
 * - Make sure the gamestate changes appropriately
 * - Finish writing the AI itself
 * --- Take care of case where AI needs to shoot bow
 * --- Use the path finding algorithm to find the way out.
 * -----Set all unknowns and possible pits/wumpus cells -1 then run algorithm
 * -----and take local minima to find the entrance of the cave
 ******************************************************************************/

import java.util.Scanner;
import java.util.Hashtable;
import java.awt.Point;
import java.util.Set;
import java.util.Iterator;

class HuntTheWumpus {

  enum Gamestate{
    Init,
    Ongoing,
    PlayerFell,
    PlayerEaten,
    PlayerWon,
    PlayerQuit
  };

  enum Direction{
    North,
    South,
    East,
    West
  };

  static Scanner in;
  static Direction playerDir;
  static int playerPos;
  static Gamestate gamestate;
  static int boardCols;
  static int boardRows;
  static int score;

  static final char Enter   = 'E';
  static final char Smelly  = 'S';
  static final char Breezy  = 'B';
  static final char Wumpus  = 'W';
  static final char Glitter = 'G';
  static final char Pit     = 'P';
  static final char Unknown = '?';
  static final char Empty   = 'T';

  static String TestBoard[][] =
          {
                  {"E" ,"B" ,"P" ,"B" ,"" },
                  {"" ,""  ,"B" ,""   ,"" },
                  {"B",""  ,"S" ,""   ,"B"},
                  {"P","BS","W" ,"BSG","P"},
                  {"B","P" ,"BS",""   ,"B"}
          };

  static String Known[][];
  static String Possible[][];
  static int    DistanceBoard[][];

  public static void printMatrix(String[][] map){
    String spaces = "   ";

    for(int i = 0; i < map.length; i++){
      for(int j = 0; j < map[i].length; j++){

        int numSpaces = 3 - map[i][j].length();
        String output = map[i][j];
        if(numSpaces > 0){
          output += spaces.substring(0,numSpaces);
        }
        if(j < map[i].length - 1){
          output += "|";
        }
        System.out.print(output);
      }
      if(i < map.length - 1){
        System.out.print("\n-------------------\n");
      } else {
        System.out.println();
      }
    }
  }

  public static void printMatrix(int[][] mat){
    String out = "";
    for(int i = 0; i < mat.length; i++){
      for(int j = 0; j < mat.length; j++){
        out += mat[i][j] + " ";
      }
      out += "\n";
    }
    System.out.println(out);
  }

  public static void printPosition(String[][] mat){
    int row = (playerPos - 1) / 5;
    int col = (playerPos - 1) % 5;

    String out = "";
    for(int i = 0; i < mat.length; i++){
      for(int j = 0; j < mat.length; j++){
        if(i == row && col == j){
          out += "x";
        } else {
          out += " ";
        }
        out += j == mat.length-1 ? "" : "|";
      }
      if(i < mat.length - 1){
        out += "\n---------\n";
      } else {
        out += "\n";
      }
    }
    System.out.println(out);
  }

  public static int findEntrance(String[][] map){
    for(int i = 0; i < map.length; i++){
      for(int j = 0; j < map[i].length; j++){
        if(map[i][j].contains("E")){
          return (j+1) + (i*5);
        }
      }
    }
    return 0;
  }

  public static void aiObserveCell(){
    int row = (playerPos - 1) / 5;
    int col = (playerPos - 1) % 5;

    Known[row][col] = TestBoard[row][col];
  }

  public static void aiUpdatePossible(){
    int row = (playerPos - 1) / 5;
    int col = (playerPos - 1) % 5;

    String attr = Known[row][col];

    // Update all cells in the area
    for(int y = -1; y < 2; y++){
      for(int x = -1; x < 2; x++){

        int currRow = y+row;
        int currCol = x+col;

        // If a valid index        
        if( currRow > -1 && currCol > -1 && currRow < boardRows && currCol < boardCols && Math.abs(x) != Math.abs(y)){

          // If we haven't been to the cell before
          if(Known[currRow][currCol].indexOf(Unknown) > -1){

            // If the cell is smelly, Wumpus near
            if(attr.indexOf(Smelly) > -1){
              // Avoid duplicates
              if(Possible[currRow][currCol].indexOf(Wumpus) < 0){
                Possible[currRow][currCol] += Wumpus;
              } else {
                // Two strikes against a cell for the Wumpus, means it must be the Wumpus
                Known[currRow][currCol] += Wumpus;
              }
            } else {
              // If the cell is not smelly, no Wumpus near
              int index = Possible[currRow][currCol].indexOf(Wumpus);
              if( index >= 0){
                StringBuilder sb = new StringBuilder(Possible[currRow][currCol]);
                sb.deleteCharAt(index);
                Possible[currRow][currCol] = sb.toString();
              }
            }

            // If the cell is breezy, pit near
            if(attr.indexOf(Breezy) > -1l){
              if(Possible[currRow][currCol].indexOf(Pit) < 0){
                Possible[currRow][currCol] += Pit;
              }

            } else {
              // If the cell is not breezy, no pit near
              int index = Possible[currRow][currCol].indexOf(Pit);
              if( index >= 0){
                StringBuilder sb = new StringBuilder(Possible[currRow][currCol]);
                sb.deleteCharAt(index);
                Possible[currRow][currCol] = sb.toString();
              }
            }
          }
        }
      }
    }
  }

  // This will have the logic for what the AI should do given that
  // the known and possible board have been updated by the last move
  public static void aiMakeMove(){
    Hashtable<Point, Integer> ht = new Hashtable<Point, Integer>();

    int row = (playerPos - 1) / 5;
    int col = (playerPos - 1) % 5;

    // For every possible cell in the area
    for(int y = -1; y < 2; y++){
      for(int x = -1; x < 2; x++){

        int currRow = y+row;
        int currCol = x+col;

        // If a valid index        
        if( currRow > -1 && currCol > -1 && currRow < boardRows && currCol < boardCols && Math.abs(x) != Math.abs(y)){
          String attr = Possible[currRow][currCol];
          int score = 0;

          if(attr.indexOf(Pit) > -1){
            score -= 1000;
          }
          if(attr.indexOf(Wumpus) > -1){
            score -= 1000;
          }
          // Haven't been there before
          if(Known[currRow][currCol].indexOf(Unknown) > -1){
            score += 50;
          }
          ht.put(new Point(x,y), score);
        }
      }
    }

    // Find the highest scoring cell of those scored
    // TODO: Problably want to move more randomly.  This tends to move
    //       South more than any other direction when possible
    Set<Point> keys = ht.keySet();
    Iterator<Point> itr = keys.iterator();
    int highestScore = Integer.MIN_VALUE;
    Point highestPoint = new Point(42,42);
    while(itr.hasNext()){
      Point p = itr.next();
      System.out.println("(" + p.getX() + ", " + p.getY() + ") score: " + ht.get(p));
      if(ht.get(p) > highestScore){
        highestScore = ht.get(p);
        highestPoint = p;
      }
    }

    // Output for debug
    String out = "AI recommends you go: ";
    // Note: North/South are reversed
    if(highestPoint.getX() == -1 && highestPoint.getY() == 0){ out += "West"; }
    else if(highestPoint.getX() == 1 && highestPoint.getY() == 0){ out += "East"; }
    else if(highestPoint.getX() == 0 && highestPoint.getY() == 1){ out += "South"; }
    else if(highestPoint.getX() == 0 && highestPoint.getY() == -1){ out += "North"; }
    System.out.println(out);

    // TODO: deal with the case where we need to shoot the bow
  }

  public static void observeCell(){
    String output = "";

    int row = (playerPos - 1) / 5;
    int col = (playerPos - 1) % 5;

    String state = TestBoard[row][col];

    if(state.length() > 0 && (state.length() != 1 || state.charAt(0) == Enter)){
      output += "You notice ";

      for(int i = 0; i < state.length(); i++){
        switch(state.charAt(i)){
          case Smelly:
            output += "a faint stench";
            break;
          case Breezy:
            output += "a breeze";
            break;
          case Glitter:
            output += "a pot of gold";
            break;
          case Wumpus:
            // Want to move these out so processInput so gamestate changes at the right time
            System.out.println("You have been eaten by the Wumpus!");
            return;
          case Pit:
            // Want to move these out so processInput so gamestate changes at the right time        
            System.out.println("You have fallen to your death!");
            return;
          case Enter:
          default:
            break;
        }
        if(i + 1 < state.length()){
          output += " and ";
        }
      }
    } else {
      output += "There is nothing remarkable about this area.";
    }

    System.out.println(output);
    System.out.println("You are facing " + directionToString());
  }

  public static String directionToString(){
    switch(playerDir){
      case North:
        return "North";
      case South:
        return "South";
      case East:
        return "East";
      case West:
        return "West";
      default:
        return "Oops!";
    }
  }

  public static char getInput(){
    boolean done = false;
    char output = 0;

    while(!done){
      System.out.println("What would you like to do? ('h'for help)");
      String input = in.nextLine();
      if(input.length() == 1){
        input = input.toLowerCase();
        output = input.charAt(0);
        switch(output){
          case 'r': // right
          case 'l': // left
          case 'f': // foward
          case 's': // shoot
          case 'h': // help
          case 'q': // quit            
            done = true;
            break;
        }
      }
    }
    return output;
  }

  // Changes the player's current direction
  public static void turnPlayer(char c){
    if(c != 'r' && c != 'l'){
      return;
    }

    switch(playerDir){
      case North:
        playerDir = (c == 'r') ? Direction.East : Direction.West;
        break;
      case South:
        playerDir = (c == 'r') ? Direction.West : Direction.East;
        break;
      case East:
        playerDir = (c == 'r') ? Direction.South : Direction.North;
        break;
      case West:
        playerDir = (c == 'r') ? Direction.North : Direction.South;
        break;
    }
  }

  // Move the player in the direction that they are facing
  public static void movePlayer(){
    int currRow = (playerPos - 1) / 5;
    int currCol = (playerPos - 1) % 5;

    int nextRow = currRow;
    int nextCol = currCol;

    switch(playerDir){
      case North:
        nextRow--;
        break;
      case South:
        nextRow++;
        break;
      case East:
        nextCol++;
        break;
      case West:
        nextCol--;
        break;
    }

    if(nextRow < 0 || nextCol < 0 || nextRow > 4 || nextCol > 4){
      System.out.println("Could not move forward, wall in the way.");
    } else {
      switch(playerDir){
        case North:
          playerPos-=5;
          break;
        case South:
          playerPos+=5;
          break;
        case East:
          playerPos++;
          break;
        case West:
          playerPos--;
          break;
      }
    }
  }

  public static void shootBow(){
    System.out.println("shootBow Not Yet Implemented");
  }

  // Apparently Java doesn't like multi-line string literals
  public static void printHelp(){
    System.out.println("Please enter input as single characters:");
    System.out.println("\th - Help\n\tr - Turn to the Right");
    System.out.println("\tl - Turn to the Left");
    System.out.println("\tf - Walk Forward");
    System.out.println("\ts - Shoot your bow");
    System.out.println("\tq - Quit");
    System.out.println("\tNote: Gold is picked up automatically");
  }

  public static void processInput(char input){
    switch(input){
      case 'r': // right
      case 'l': // left
        score--;
        turnPlayer(input);
        System.out.println("You have turned to face " + directionToString());
        break;
      case 'f': // foward
        score--;
        movePlayer();
        break;
      case 's': // shoot
        score -= 10;
        shootBow();
        break;
      case 'h': // help
        printHelp();
        break;
      case 'q':
        gamestate = Gamestate.PlayerQuit;
        break;
    }
  }

// Will eventually be helpful for finding the way back
//  public static int findMinNeighbor(int[][] map, int row, int col){
//    int min = Integer.MAX_VALUE;
//    
//    // For every cell in the vicinity  
//    for(int y = -1; y < 2; y++){
//      for(int x = -1; x < 2; x++){
//        
//        int currRow = y+i;
//        int currCol = x+j;
//        // If a valid index
//        if( currRow > -1 && currCol > -1 && currRow < numRows && currCol < numCols){
//          if(map[currRow][currCol] >= currDist + 1){
//            map[currRow][currCol] = currDist + 1;
//            changesMade = true;
//          }
//        }
//      }
//    }
//  }

  // This will work, just set all obstacles/unknown to -1 on the distance map, run this
  // at take every local minimum to find exit
  public static void initDistBoard(String[][] map, int entrance){
    boolean changesMade = true;
    int currDist = 0;

    int entRow = (entrance - 1) / 5;
    int entCol = (entrance - 1) % 5;

    int numCols = map.length;
    int numRows = map[numCols-1].length;
    DistanceBoard = new int[numCols][numRows];

    for(int i = 0; i < map.length; i++){
      for(int j = 0; j < map.length; j++){
        DistanceBoard[i][j] = Integer.MAX_VALUE;
      }
    }


    DistanceBoard[entRow][entCol] = 0;

    while(changesMade){
      changesMade = false;

      // For every cell
      for(int i = 0 ; i < DistanceBoard.length; i++){
        for(int j = 0 ; j < DistanceBoard[i].length; j++){

          // Check to see if the dist is what we want
          if(DistanceBoard[i][j] == currDist){
            // For every cell in this cells vicinity
            for(int y = -1; y < 2; y++){
              for(int x = -1; x < 2; x++){

                int currRow = y+i;
                int currCol = x+j;
                // If a valid index
                if( currRow > -1 && currCol > -1 && currRow < numRows && currCol < numCols && Math.abs(x) != Math.abs(y)){
                  if(DistanceBoard[currRow][currCol] >= currDist + 1){
                    DistanceBoard[currRow][currCol] = currDist + 1;
                    changesMade = true;
                  }
                }
              }
            }
          }
        }
      }
      currDist++;
    }
  }

  public static void init(){
    boardCols = TestBoard.length;
    boardRows = TestBoard[boardCols-1].length;

    Known = new String[boardCols][boardRows];
    Possible = new String[boardCols][boardRows];

    for(int i = 0; i < boardCols; i++){
      for(int j = 0; j < boardRows; j++){
        Known[i][j] = "" + Unknown;
        Possible[i][j] = "";
      }
    }

    gamestate = Gamestate.Init;
    in = new Scanner(System.in);

    // The entrance is at cell index 1
    playerPos = 1;
    playerDir = Direction.South;
    gamestate = Gamestate.Ongoing;
    score = 0;
  }

  public static void main(String args[]){
    init();

    System.out.println("Welcome to Hunt the Wumpus");
    System.out.println();
    System.out.println("You have wandered into the Wumpus's cave with nothing more than a single arrow for your bow.");
    System.out.println("You are in search of the Wumpus's pot of gold. However, beware the Wumpus and the bottomless");
    System.out.println("pits. Good luck!");
    System.out.println();

//    This will be done once we've found the gold
//    int entrance = findEntrance(TestBoard);
//    initDistBoard(TestBoard, entrance);
//    printMatrix(DistanceBoard);

    while(gamestate == Gamestate.Ongoing){
      observeCell();
      aiObserveCell();
      aiUpdatePossible();
      aiMakeMove();

      // For debug
//      printMatrix(Known);
      printPosition(Possible);
      printMatrix(Possible);
      char input = getInput();
      processInput(input);
    }
  }
}