package myPackage;

import java.awt.*;
import myPackage.Utils.GEM;

public class PVPBot extends AbstractGameBoard {
	
	/** 
	 * Define the order of your cards, use these constants to access the cards Array.
	 * e.g. cards[DRYAD] in the MapFarmGameBoard.
	 */
	private static int CORONET = 0;
	private static int FLAME = 1;
	private static int KHORVASH = 2;
	private static int GOBLIN = 3;
	
	private enum STATE {EARLY, LATE};
	private static int turns = 0;
	private static STATE boardState;
	
	/**
	 * This constructor is used to copy your Board to analyze moves. 
	 */
	public PVPBot(AbstractGameBoard otherBoard) {
		super(otherBoard);
	}
	
	/**
	 * Use this constructor to initialize your Board. 
	 * 
	 * You need to fill your cards Array here with the correct Cards.
	 * 
	 * If you have defined states, set your beginning state here.
	 */
	public PVPBot(long[][] vals) throws AWTException {
		super(vals);
		if (!Utils.hasInitialized) {
			cards = new CARD[] {new CARD("CORONET", CORONET), new CARD("FLAME", FLAME), new CARD("KHORVASH", KHORVASH), new CARD("GOBLIN", GOBLIN)};
			turns = 0;
			boardState = STATE.EARLY;
		}
	}
	
	/**
	 * This method defines the AI logic. 
	 * 
	 * If your board has states, you will want to switch over the states here.
	 * 
	 * This method needs to return a valid Move (BoardMove or Cast).
	 * 
	 * For BoardMoves, use the predefined methods from AbstractGameBoard.
	 * e.g. finalMove = takeThrees();
	 * 
	 * For Casts, use the predefined methods from CARD.
	 * e.g. finalMove = cards[TYRI].castOnAlly(cards[SPIDER]);
	 */
	@Override
	public Move calculateNextMove (int not, BoardMove used) {
		turns++;
		if (isCardActive(cards[GOBLIN])) {
			return cards[GOBLIN].castOnTopEnemy();
		}
		Move finalMove = takeFoursOrFives();
		if (finalMove != null) return finalMove;
		if (isCardActive(cards[FLAME])) {
			Coordinates c = findBestFlameTarget();
			if (c != null) {
				return cards[FLAME].castOnBoard(c.x, c.y);
			}
		}
		if (isCardActive(cards[KHORVASH])) {
			return cards[KHORVASH].castWithoutTarget();
		}
		if (isCardActive(cards[CORONET])) {
			return cards[CORONET].castWithoutTarget();
		}
		
		if (boardState == STATE.EARLY) {
			return tryToTakeColors(GEM.BROWN, GEM.RED, GEM.BLUE, GEM.YELLOW, GEM.GREEN, GEM.SKULL, GEM.PURPLE);
		} else {
			return tryToTakeColors(GEM.SKULL, GEM.BROWN, GEM.RED, GEM.BLUE, GEM.YELLOW, GEM.GREEN, GEM.PURPLE);
		}
	}

	private Coordinates findBestFlameTarget() {
		int best = 0;
		Coordinates bestCoordinates = new Coordinates(3, 3);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int amount = 0;
				for (int m = -1; m < 2; m++) {
					for (int n = -1; n < 2; n++) {
						if (i + m > 0 && i + m < 8 && j + n > 0 && j + n < 8) {
							GEM current = (GEM) board[i + m][j + n];
							if (current == GEM.SKULL) {
								amount++;
							}
							if (current == GEM.RED) {
								amount += 3;
							}
						}
					}
				}
				
				if (amount > best) {
					best = amount;
					bestCoordinates = new Coordinates(i, j);
				}
			}
		}
		return bestCoordinates;
	}

	@Override
	public void updateBoardState() {
		if (turns > 8) {
			boardState = STATE.LATE;
		}
	}
}