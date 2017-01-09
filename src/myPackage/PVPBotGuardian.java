package myPackage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import myPackage.Utils.DIRECTION;
import myPackage.Utils.GEM;

public class PVPBotGuardian extends AbstractGameBoard {

	/** 
	 * Define the order of your cards, use these constants to access the cards Array.
	 * e.g. cards[DRYAD] in the MapFarmGameBoard.
	 */
	private static int MERCY = 0;
	private static int ALCH = 1;
	private static int CAT = 2;
	private static int GARD = 3;

	private enum STATE { G, M, A, C, MA, MC, AC, MAC, NONE };
	private static STATE boardState;

	/**
	 * This constructor is used to copy your Board to analyze moves. 
	 */
	public PVPBotGuardian(AbstractGameBoard otherBoard) {
		super(otherBoard);
	}
	
	public PVPBotGuardian(GEM[][] gems) {
		super(gems);
	}
	
	public PVPBotGuardian(String gemsString) {
		super(Utils.gemsFromString(gemsString));
	}
	
	/**
	 * Use this constructor to initialize your Board. 
	 * 
	 * You need to fill your cards Array here with the correct Cards.
	 * 
	 * If you have defined states, set your beginning state here.
	 */
	public PVPBotGuardian(long[][] vals) throws AWTException {
		super(vals);
		if (cards == null) {
			cards = new CARD[] {new CARD("MERCY", MERCY), new CARD("ALCHI", ALCH), new CARD("CAT", CAT), new CARD("GARD", GARD)};
		}
		if (boardState == null) {
			boardState = STATE.NONE;
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
		Move finalMove = null;
		switch (boardState) {
		case NONE:
			finalMove = takeFoursOrFives();
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.BROWN, GEM.PURPLE, GEM.RED, GEM.YELLOW, GEM.GREEN, GEM.BLUE);
			break;
		case G:
			finalMove = takeFoursOrFives();
			if (finalMove != null) break;
			finalMove = cards[GARD].castWithoutTarget();
			break;
		case A:
			finalMove = takeFoursOrFivesUnless(GEM.YELLOW);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(ALCH);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.PURPLE, GEM.YELLOW, GEM.GREEN, GEM.BLUE, GEM.BROWN, GEM.RED);
			break;
		case C:
			finalMove = takeFoursOrFivesUnless(GEM.RED);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(CAT);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.BROWN, GEM.RED, GEM.GREEN, GEM.BLUE, GEM.YELLOW, GEM.PURPLE);
			break;
		case M:
			finalMove = takeFoursOrFivesUnless(GEM.YELLOW, GEM.PURPLE);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(MERCY);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.BROWN, GEM.PURPLE, GEM.RED, GEM.YELLOW, GEM.GREEN, GEM.BLUE);
			break;
		case AC:
			finalMove = takeFoursOrFivesUnless(GEM.YELLOW, GEM.RED);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(CAT);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(ALCH);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.GREEN, GEM.BLUE, GEM.BROWN, GEM.YELLOW, GEM.RED, GEM.PURPLE);
			break;
		case MA:
			finalMove = takeFoursOrFivesUnless(GEM.YELLOW, GEM.PURPLE);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(MERCY);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(ALCH);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.YELLOW, GEM.PURPLE, GEM.BROWN, GEM.RED, GEM.GREEN, GEM.BLUE);
			break;
		case MC:
			finalMove = takeFoursOrFivesUnless(GEM.YELLOW, GEM.PURPLE, GEM.RED);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(MERCY);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(CAT);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.BROWN, GEM.RED, GEM.YELLOW, GEM.BLUE, GEM.PURPLE, GEM.GREEN);
			break;
		case MAC:
			finalMove = takeFoursOrFivesUnless(GEM.YELLOW, GEM.PURPLE, GEM.RED);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(MERCY);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(CAT);
			if (finalMove != null) break;
			finalMove = castIfExtraTurn(ALCH);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.SKULL, GEM.BROWN, GEM.YELLOW, GEM.RED, GEM.PURPLE, GEM.BLUE, GEM.GREEN);
			break;
		default:
			break;
		}

		return finalMove;
	}
	
	private Cast castIfExtraTurn (int card) {
		Coordinates castCoords = null;
		if (card == ALCH) {
			castCoords = replaceBestColorWithExtraTurn(GEM.YELLOW);
		} else if (card == CAT) {
			castCoords = replaceBestColorWithExtraTurn(GEM.RED);
		} else if (card == MERCY) {
			AbstractGameBoard checkBoard = new AbstractGameBoard(this);
			replaceGemsOfWith(checkBoard, GEM.PURPLE, GEM.YELLOW);
			if (hasExtraTurnFromColor(checkBoard, GEM.YELLOW)) {
				// We don't need it as a target for mercy
				// but we need to return something not null.
				castCoords = getGemOfColor(GEM.PURPLE);
			}
		} else {
			throw new IllegalArgumentException("Unknown card with value: " + card);
		}
		
		if (castCoords != null) {
			return cards[card].castOnBoard(castCoords.x, castCoords.y);
		}
		return null;
	}

	private Coordinates replaceBestColorWithExtraTurn(GEM color) {
		AbstractGameBoard checkBoard;
		int replaced, bestReplaced = 0;
		Coordinates bestResult = null;
		for (int i = 1; i < GEM.values().length; i++) {
			if (i == color.ordinal() || i == GEM.SKULL.ordinal()) {
				continue;
			}
			GEM currentGem = GEM.values()[i];
			checkBoard = new AbstractGameBoard(this);
			replaced = replaceGemsOfWith(checkBoard, currentGem, color);
			if (hasExtraTurnFromColor(checkBoard, color) && replaced > bestReplaced) {
				bestResult = getGemOfColor(currentGem);
			}
		}
		return bestResult;
	}

	private boolean hasExtraTurnFromColor(AbstractGameBoard checkBoard, GEM color) {
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (checkBoard.board[row][column] != color) {
					continue;
				}
				if (checkBoard.findMatchingStones(row, column, new BoardMove(DIRECTION.DOWN, row, column)).size() > 3) {
					return true;
				}
			}
		}
		return false;
	}

	private int replaceGemsOfWith(AbstractGameBoard replace, GEM from, GEM to) {
		int replaced = 0;
		for (int row = 0; row < 8; row++)
			for (int column = 0; column < 8; column++)
				if (replace.board[row][column] == from) {
					replace.board[row][column] = to;
					replaced++;
				}
		return replaced;
	}

	private Move takeFoursOrFivesUnless(GEM... forbidden) {
		ArrayList<GEM> forbiddenGems = new ArrayList<>(Arrays.asList(forbidden));
		BoardMove bestMove = new BoardMove(DIRECTION.DOWN, 0, 0), nextMove;
		AbstractGameBoard moveBoard = new AbstractGameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.makeMove(nextMove, forbiddenGems)) {
						if (nextMove.biggestMatch > 3 && nextMove.biggestMatch >= bestMove.biggestMatch) {
							bestMove = nextMove;
						}
					}
				}
				moveBoard = new AbstractGameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.makeMove(nextMove, forbiddenGems)) {
						if (nextMove.biggestMatch > 3 && nextMove.biggestMatch >= bestMove.biggestMatch) {
							bestMove = nextMove;
						}
					}
				}
				moveBoard = new AbstractGameBoard(this);
			}
		}
		if (bestMove.biggestMatch > 0)
			return bestMove;
		else
			return null;
	}

	@Override
	public void updateBoardState() {
		if (isCardActive(cards[GARD])) {
			boardState = STATE.G;
			return;
		}
		
		boolean mercy = isCardActive(cards[MERCY]);
		boolean alch = isCardActive(cards[ALCH]);
		boolean cat = isCardActive(cards[CAT]);

		if (mercy) {
			if (alch) {
				if (cat) {
					boardState = STATE.MAC;
				} else {
					boardState = STATE.MA;
				}
			} else {
				if (cat) {
					boardState = STATE.MC;
				} else {
					boardState = STATE.M;
				}
			}
		} else {
			if (alch) {
				if (cat) {
					boardState = STATE.AC;
				} else {
					boardState = STATE.A;
				}
			} else {
				if (cat) {
					boardState = STATE.C;
				} else {
					boardState = STATE.NONE;
				}
			}
		}
	}
}