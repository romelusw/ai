package main.java.com.romelus;

/**
 * A clever battleship player.
 *
 * @author romelus
 */
public class AIPlayer extends Player {
    private static final int[] SHIP_TYPES = {Ships.SHIP_CARRIER, Ships.SHIP_BATTLESHIP, Ships.SHIP_CRUISER, Ships.SHIP_DESTROYER, Ships.SHIP_SUBMARINE};
    private boolean IS_SEEKING = true;

    /**
     * Default constructor.
     *
     * @param playerNum the player id
     */
    public AIPlayer(int playerNum) {
        super(playerNum);
    }

    @Override
    public void makeMove() {
        // Try making a move until successful
        while (!game.makeMove(hisShips, myMoves, randomRow(), randomCol())) ;

        numMoves++;
        System.out.println("Player " + myPlayerNum + " num Moves = " + numMoves);
    }

    @Override
    public boolean addShips() {
        return addShipsRandomly();
    }

    /**
     * Places the ships in random fashion onto the board.
     *
     * @return flag indicating that all ships were placed
     */
    private boolean addShipsRandomly() {
        boolean retVal = false;
        for(final int ship : SHIP_TYPES) {
            int retryCount = 30; // Retry for only a fixed amount
            while(retryCount >= 0) {
                final int row = randomRow();
                final int col = randomCol();
                final int dir = randomDirection();
                if(shipFits(ship, row, col, dir)) {
                    game.putShip(myShips, ship, row, col, dir);
                    retVal = true;
                    break;
                }
                retryCount--;
            }
            if(retryCount < 0) break;
        }
        return retVal;
    }

    /**
     * Generates a random direction.
     *
     * @return the direction
     */
    private int randomDirection() {
        return rnd.nextInt(3) + 1;
    }

    /**
     * Determines if the orientation of the ship fits within the board.
     *
     * @param type
     * @param x
     * @param y
     * @param dir
     * @return
     */
    private boolean shipFits(final int type, final int x, final int y, final int dir) {
        boolean cellsEmpty = false;
        int xAfter = 0, yAfter = 0;
        switch(dir) {
            case Ships.SHIP_NORTH:
                xAfter = x;
                yAfter = y + getShipLength(type);
                break;
            case Ships.SHIP_SOUTH:
                xAfter = x;
                yAfter = y - getShipLength(type);
                break;
            case Ships.SHIP_EAST:
                xAfter = x - getShipLength(type);
                yAfter = y;
                break;
            case Ships.SHIP_WEST:
                xAfter = x + getShipLength(type);
                yAfter = y;
                break;
        }

        if(xAfter >= 0 && xAfter < 10 && yAfter >= 0 && yAfter < 10) {
            final int xSmall = Math.min(x, xAfter);
            final int xLarge = Math.max(x, xAfter);
            final int ySmall = Math.min(y, yAfter);
            final int yLarge = Math.max(y, yAfter);
            outerLoop:
            for(int i = xSmall; i < xLarge; i++) {
                for(int j = ySmall; j < yLarge; j++) {
                    if(myShipsBoard[i][j] != '\0') {
                        cellsEmpty = true;
                        break outerLoop;
                    }
                }
            }
        }
        return cellsEmpty;
    }

    @Override
    public void sankCarrier() {
        System.out.println("You Sank my Carrier(p" + myPlayerNum + ")");
    }

    @Override
    public void sankBattleShip() {
        System.out.println("You Sank my Battleship(p" + myPlayerNum + ")");
    }

    @Override
    public void sankCruiser() {
        System.out.println("You Sank my Cruiser(p" + myPlayerNum + ")");
    }

    @Override
    public void sankDestroyer() {
        System.out.println("You Sank my Destroyer(p" + myPlayerNum + ")");
    }

    @Override
    public void sankSubmarine() {
        System.out.println("You Sank my Submarine(p" + myPlayerNum + ")");
    }
}