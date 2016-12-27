package myPackage;

import java.awt.*;
import java.awt.image.BufferedImage;

import myPackage.CARD.STATUS;
import myPackage.Utils.GEM;

public class PVPBot extends AbstractGameBoard {
	
	/** 
	 * Define the order of your cards, use these constants to access the cards Array.
	 * e.g. cards[DRYAD] in the MapFarmGameBoard.
	 */
	private static final int DRYAD = 0;
	private static final int TYRI = 1;
	private static final int SHAMAN = 2;
	private static final int SPIDER = 3;
	private static boolean castOnTyri = false;
	
	/**
	 * If you need to, define states for your GameBoard using this enum. 
	 */
	private enum STATE {CHARGE, EXECUTE, FINISH};
	private STATE boardState;
	
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
		boardState = STATE.CHARGE;
		cards = new CARD[] {CARD.DRYAD, CARD.TYRI, CARD.SHAMAN, CARD.SPIDER};
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
		Move finalMove = null;
		finalMove = takeFoursOrFives();
		
		if (finalMove == null) {
			finalMove = tryToTakeColors(GEM.SKULL);
		}
		
		if (finalMove == null) {
			finalMove = takeThrees();
		}
		
		return finalMove;
		
//		switch (boardState) {
//		case CHARGE:
//			finalMove = takeFoursOrFives();
//			if (finalMove != null) break;
//			finalMove = tryToTakeColors(GEM.GREEN, GEM.PURPLE);
//			if (finalMove != null) break;
//			if (isCardActive(cards[SHAMAN])) {
//				finalMove = cards[SHAMAN].castWithoutTarget();
//				break;
//			}
//			if (isCardActive(cards[DRYAD])) {
//				finalMove = castDryad();
//				break;
//			}
//			finalMove = tryToTakeColors(GEM.BROWN, GEM.BLUE, GEM.YELLOW, GEM.RED, GEM.SKULL);
//			break;
//		case EXECUTE:
//			getStoneCount();
//			finalMove = castTyriOnConditions(finalMove, GEM.GREEN, 8);
//			if (finalMove != null) break;
//			finalMove = castTyriOnConditions(finalMove, GEM.PURPLE, 8);
//			if (finalMove != null) break;
//			if (cards[SHAMAN].get_status() == CARD.STATUS.ACTIVE) {
//				finalMove = cards[SHAMAN].castWithoutTarget();
//				break;
//			}
//			if (cards[DRYAD].get_status() == CARD.STATUS.ACTIVE) {
//				finalMove = castDryad();
//				break;
//			}
//			if (cards[SPIDER].get_status() == CARD.STATUS.ACTIVE) {
//				Coordinates c = findBestSpiderTarget();
//				finalMove = cards[SPIDER].castOnBoard(c.x, c.y);
//				break;
//			}
//			finalMove = takeFoursOrFives();
//			if (finalMove != null) break;
//			finalMove = castTyriOnConditions(finalMove, GEM.GREEN, 5);
//			if (finalMove != null) break;
//			finalMove = castTyriOnConditions(finalMove, GEM.PURPLE, 5);
//			if (finalMove != null) break;
//			finalMove = tryToTakeColors(GEM.BROWN, GEM.BLUE, GEM.YELLOW, GEM.RED, GEM.SKULL);
//			break;
//		case FINISH:
//			finalMove = takeThrees();
//			break;
//		default:
//			break;
//		}
//
//		return finalMove;
	}

	private Move castDryad() {
		Move result;
		if (castOnTyri) {
			result = cards[DRYAD].castOnAlly(cards[TYRI]);
		} else {
			result = cards[DRYAD].castOnAlly(cards[DRYAD]);
		}
		castOnTyri = !castOnTyri;
		return result;
	}

	private Cast castTyriOnConditions(Move finalMove, GEM color, int amount) {
		Cast tyriCast = null;
		if (counts[color.ordinal()] >= amount) {
			Coordinates c = getGemOfColor(color);
			tyriCast = cards[TYRI].castOnBoard(c.x, c.y);
		}
		return tyriCast;
	}
	
	private Coordinates findBestSpiderTarget() {
		int max = 0;
		GEM color = null;
		for (int i = 0; i < counts.length; i++) {
			GEM currentColor = GEM.values()[i];
			if (counts[i] > max && currentColor != GEM.GREEN && currentColor != GEM.PURPLE && currentColor != GEM.SKULL) {
				max = counts[i];
				color = GEM.values()[i];
			}
		}
		return getGemOfColor(color);
	}
	
	@Override
	public void updateCards() throws AWTException {
		super.updateCards();
		
		if (cards[TYRI].get_status() == STATUS.ACTIVE) {
			boardState = STATE.EXECUTE;
		} else if (cards[TYRI].get_status() == STATUS.INACTIVE) {
			boardState = STATE.CHARGE;
		} else {
			boardState = STATE.FINISH;
		}
	}
}
