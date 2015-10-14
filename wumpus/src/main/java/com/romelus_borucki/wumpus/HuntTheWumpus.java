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
 * - How hard would it be to plug this into a JS front-end?
 * - Think about creating more classes:
 * --- Cell   (actual attr, known attr, possible attr, distance from entrance)
 * --- Map    (Array of cells, get/set, initFromFile, print)
 * --- Player (position, direction, hasArrrow, ...)
 * --- Game   (gamestate, score, wumpusAlive, ... )
 * - Write the AI itself
 ******************************************************************************/
package java.com.romelus_borucki.wumpus;

import java.util.Scanner;

class HuntTheWumpus{

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
    {"" ,"B" ,"P" ,"B"  ,"" },
    {"" ,""  ,"B" ,""   ,"" },
    {"B",""  ,"S" ,""   ,"B"},
    {"P","BS","W" ,"BSG","P"},
    {"B","P" ,"BS",""   ,"B"}
  };
  
  public static void printMap(String[][] map){
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
  
  public static void observeCell(){
    String output = "";
    
    int row = (playerPos - 1) / 5;
    int col = (playerPos - 1) % 5;
    
    String state = TestBoard[row][col];
    
    if(state.length() > 0){
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
        playerDir = (c == 'r')? Direction.East :  Direction.West;
        break;
      case South:
        playerDir = (c == 'r')? Direction.West :  Direction.East;
        break;
      case East:
        playerDir = (c == 'r')? Direction.South :  Direction.North;
        break;
      case West:
        playerDir = (c == 'r')? Direction.North :  Direction.South;
        break;
    }
  }
  
  // Move the player in the direction that they are facing
  public static void movePlayer(){
    boolean debug = false;
    
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
    
    if(debug){    
      System.out.println("Position before: " + playerPos);
      System.out.println("CR: " + currRow + ", CC: " + currCol + ", NR: " + nextRow + ", NC: " + nextCol);
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
    
    if(debug){
      System.out.println("Position after: " + playerPos);
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
            turnPlayer(input);
            System.out.println("You have turned to face " + directionToString());
            break;        
          case 'f': // foward
            movePlayer();
            break;        
          case 's': // shoot
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
  
  public static void main(String args[]){
    gamestate = Gamestate.Init;
    in = new Scanner(System.in);
    
    System.out.println("Welcome to Hunt the Wumpus");
    System.out.println();
    System.out.println("You have wandered into the Wumpus's cave with nothing more than a single arrow for your bow.");
    System.out.println("You are in search of the Wumpus's pot of gold. However, beware the Wumpus and the bottomless");
    System.out.println("pits. Good luck!");
    System.out.println();

    // The entrance is at cell index 1
    playerPos = 1;
    playerDir = Direction.South;
    gamestate = Gamestate.Ongoing;
    
    while(gamestate == Gamestate.Ongoing){
      observeCell();
      char input = getInput();
      processInput(input);
    }
  }
}