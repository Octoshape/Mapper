package myPackage;

import java.awt.*;
import java.util.Arrays;

import myPackage.CARD.STATUS;
import myPackage.Utils.DIRECTION;
import myPackage.Utils.GEM;

public class AbstractGameBoard extends AbstractBoard {

	public AbstractGameBoard(long[][] vals) throws AWTException {
		super(vals);
		if (!Utils.hasInitialized) {
			cardsValue = Utils.getCardsValue();
		}
	}

	public AbstractGameBoard(AbstractGameBoard otherBoard) {
		super(otherBoard);
	}

	protected int[] counts;
	protected static CARD[] cards;
	protected static long cardsValue;
	protected static boolean isBaseValueSet = false;

	/*** BOARD MOVE METHODS ***/
	
	protected BoardMove takeThrees() {
		BoardMove nextMove = null;
		AbstractGameBoard moveBoard = new AbstractGameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.moveIsThreeAndNotSkulls(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new AbstractGameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.moveIsThreeAndNotSkulls(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new AbstractGameBoard(this);
			}
		}
		return null;
	}
	
	protected BoardMove takeFoursOrFives() {
		BoardMove nextMove = null;
		AbstractGameBoard moveBoard = new AbstractGameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.moveGeneratesExtraTurn(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new AbstractGameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.moveGeneratesExtraTurn(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new AbstractGameBoard(this);
			}
		}
		return null;
	}

	protected BoardMove tryToTakeColors (GEM... colors) {
		BoardMove move = null;
		for (GEM color: colors) {
			move = takeColor(color);
			if (move != null) {
				break;
			}
		}
		return move;
	}

	private BoardMove takeColor(GEM color) {
		BoardMove nextMove = null;
		AbstractGameBoard moveBoard = new AbstractGameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.moveIsValidAndOfColor(moveBoard, nextMove, color)) {
						return nextMove;
					}
				}
				moveBoard = new AbstractGameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.moveIsValidAndOfColor(moveBoard, nextMove, color)) {
						return nextMove;
					}
				}
				moveBoard = new AbstractGameBoard(this);
			}
		}
		return null;
	}
	
	/*** HELPER METHODS ***/
	
	protected void getStoneCount() {
		counts = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int index = ((GEM)board[i][j]).ordinal();
				counts[index]++;
			}
		}
	}
	
	protected boolean moveIsThreeAndNotSkulls(AbstractGameBoard gb, BoardMove m) {
		gb.swap(m.row, m.column, m.row2, m.column2);

		int match1 = gb.findMatchingStones(m.row, m.column, m).size();
		int match2 = gb.findMatchingStones(m.row2, m.column2, m).size();

		return ((match1 == 3 && gb.board[m.row][m.column] != GEM.SKULL) || (match2 == 3 && gb.board[m.row2][m.column2] != GEM.SKULL));
	}

	protected boolean moveGeneratesExtraTurn(AbstractGameBoard gb, BoardMove m) {
		gb.swap (m.row, m.column, m.row2, m.column2);
		int match1 = gb.findMatchingStones(m.row, m.column, m).size();
		int match2 = gb.findMatchingStones(m.row2, m.column2, m).size();

		if (match1 >= 4 || match2 >= 4) {
			return true;
		}

		return false;
	}

	private boolean moveIsValidAndOfColor(AbstractGameBoard gb, BoardMove m, GEM color) {
		if (gb.board[m.row][m.column] != color && gb.board[m.row2][m.column2] != color) {
			return false;
		}

		boolean result = false;
		gb.swap(m.row, m.column, m.row2, m.column2);

		if (gb.board[m.row2][m.column2] == color) {
			int match1 = gb.findMatchingStones(m.row2, m.column2, m).size();

			if (match1 > 0) {
				result = true;
			}
		}

		if (gb.board[m.row][m.column] == color) {
			int match2 = gb.findMatchingStones(m.row, m.column, m).size();

			if (match2 > 0) {
				result = true;
			}
		}

		return result;
	}

	protected Coordinates getGemOfColor(GEM color) {
		for (int row = 0; row < 8; row++)
			for (int column = 0; column < 8; column++)
				if (board[row][column] == color) 
					return new Coordinates(row, column);
		return null;
	}

	@Override
	protected void initBoard(long[][] values) {
			for (int y = 0; y < values.length; y++) {
				for (int x = 0; x < values.length; x++) {
				long value = values[y][x];
				if (value < -6000000000l)
					board[x][y] = GEM.valueOf("BLUE");
				else if (value < -5200000000l)
					board[x][y] = GEM.valueOf("GREEN");
				else if (value < -4300000000l)
					board[x][y] = GEM.valueOf("BROWN");
				else if (value < -4000000000l)
					board[x][y] = GEM.valueOf("PURPLE");
				else if (value < -3000000000l)
					board[x][y] = GEM.valueOf("SKULL");
				else if (value < -1000000000l)
					board[x][y] = GEM.valueOf("RED");
				else if (value < -400000000l)
					board[x][y] = GEM.valueOf("YELLOW");
				else
					Utils.SKIP = true;
			}
		}
	}
	
	protected boolean isCardActive(CARD c) {
		return c.get_status() == STATUS.ACTIVE;
	}
	
	protected boolean isCardInActive(CARD c) {
		return c.get_status() == STATUS.INACTIVE;
	}
	
	protected boolean isCardDead(CARD c) {
		return c.get_status() == STATUS.DEAD;
	}

	public void initCards() throws AWTException, InterruptedException {
		if (Utils.hasInitialized) {
			return;
		}
		for (int i = 0; i < 4; i++) {
//			Thread.sleep(3000);
			Values vals = Utils.getBaseAndCastValueForCard(i);
			cards[i].setBaseValue(vals.getBaseValue());
		}
		Utils.hasInitialized = true;
	}
	
	public void updateCards() throws AWTException, InterruptedException {
		for (int i = 0; i < 4; i++) {
//			Thread.sleep(3000);
			Values vals = Utils.getBaseAndCastValueForCard(i);

			if (cards[i].getBaseValue() != vals.getBaseValue()) {
				// See if any other card is now here or if it's dead.
				boolean alive = false;
				for (int j = 0; j < 4; j++) {
					if (j == i) continue;
					if (cards[j].getBaseValue() == vals.getBaseValue()) {
						// Card j is now at i's position, swap them.
						CARD temp = cards[i];
						int tempPos = cards[i].getPosition();
						cards[i] = cards[j];
						cards[i].setPosition(cards[j].getPosition());
						cards[j] = temp;
						cards[j].setPosition(tempPos);
						alive = true;
						updateConstants();
						break;
					}
				}
				if (!alive) {
					cards[i].set_status(STATUS.DEAD);
				}
			}

			if (!isCardDead(cards[i])) {
				// Check for cast button
				if (vals.getCastValue() == Pixel.CAST_VALUE) {
					cards[i].set_status(STATUS.ACTIVE);
				} else {
					cards[i].set_status(STATUS.INACTIVE);
				}
			}
		}
	}
	
	public void cleanUp() {
		isBaseValueSet = false;
	}

	public void checkForCardUpdates() throws AWTException, InterruptedException {
		if (cardsValue != Utils.getCardsValue()) {
			Thread.sleep(3000);
			updateCards();
		}
	}

	public Move calculateNextMove(int depth, BoardMove bestMove) {
		throw new RuntimeException("Don't calculateNextMove on abstract GameBoard!");
	}
	
	public void simulate(BoardMove move, boolean firstTime) {
		throw new RuntimeException("Don't simulate on abstract GameBoard!");
	}
	
	public void updateConstants () {
		throw new RuntimeException("Don't update constants on abstract GameBoard!");		
	}
}
