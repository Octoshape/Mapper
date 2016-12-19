package myPackage;

import java.awt.image.BufferedImage;

import myPackage.Cast.TARGET;
import myPackage.GameBoard.CARD.STATUS;
import myPackage.Utils.DIRECTION;
import myPackage.Utils.GEM;

public class GameBoard extends AbstractGameBoard {

	private static final int DRYAD = 0;
	private static final int TYRI = 1;
	private static final int SHAMAN = 2;
	private static final int SPIDER = 3;

	public GameBoard(long[][] vals) {
		super(vals);
		counts = new int[] {0, 0, 0, 0, 0, 0, 0};
		boardState = STATE.CHARGE;
		cards = new CARD[] {CARD.DRYAD, CARD.TYRI, CARD.SHAMAN, CARD.SPIDER};
	}

	public GameBoard(GameBoard otherBoard) {
		super(otherBoard);
	}

	private int[] counts;
	private STATE boardState;
	private CARD[] cards;

	private enum STATE {CHARGE, EXECUTE, FINISH};
	public enum CARD {
		DRYAD (-6034306416l, -5230910273l, -6691054740l, Utils.X_CARD_POS, Utils.Y_CARD1_POS, STATUS.INACTIVE),
		TYRI (-127799919l, -3937038574l, -6342012196l, Utils.X_CARD_POS, Utils.Y_CARD2_POS, STATUS.INACTIVE),
		SHAMAN (-3980750978l, -4167849998l, -7076923430l, Utils.X_CARD_POS, Utils.Y_CARD3_POS, STATUS.INACTIVE),
		SPIDER (-3595164031l, -4562135287l, 999999999l, Utils.X_CARD_POS, Utils.Y_CARD4_POS, STATUS.INACTIVE);

		public enum STATUS {ACTIVE, INACTIVE, DEAD};
		private STATUS status;
		private int x, y;
		private long activeValue;
		private long inactiveValue;
		private long deadValue;

		CARD(long active, long inactive, long dead, int x, int y, STATUS stat) {
			this.activeValue = active;
			this.inactiveValue = inactive;
			this.deadValue = dead;
			this.x = x;
			this.y = y;
			this.status = stat;
		}

		long getActiveValue() {
			return activeValue;
		}

		long getInactiveValue() {
			return inactiveValue;
		}

		long getDeadValue() {
			return deadValue;
		}

		int getX () {
			return this.x;
		}

		int getY () {
			return this.y;
		}

		void set_status(STATUS val) {
			this.status = val;
		}

		STATUS get_status() {
			return this.status;
		}
	}

	private void getStoneCount() {
		counts = new int[] {0, 0, 0, 0, 0, 0, 0};
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int index = ((GEM)board[i][j]).ordinal();
				counts[index]++;
			}
		}
	}

	protected boolean moveIsThreeAndNotSkulls(GameBoard gb, BoardMove m) {
		gb.swap(m.row, m.column, m.row2, m.column2);

		int match1 = gb.findMatchingStones(m.row, m.column, m).size();
		int match2 = gb.findMatchingStones(m.row2, m.column2, m).size();

		return ((match1 == 3 && gb.board[m.row][m.column] != GEM.SKULL) || (match2 == 3 && gb.board[m.row2][m.column2] != GEM.SKULL));
	}

	protected boolean moveGeneratesExtraTurn(GameBoard gb, BoardMove m) {
		gb.swap (m.row, m.column, m.row2, m.column2);
		int match1 = gb.findMatchingStones(m.row, m.column, m).size();
		int match2 = gb.findMatchingStones(m.row2, m.column2, m).size();

		if (match1 >= 4 || match2 >= 4) {
			return true;
		}

		return false;
	}

	protected boolean moveIsValidAndOfColor(GameBoard gb, BoardMove m, GEM color) {
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

	private BoardMove takeFoursOrFives() {
		BoardMove nextMove = null;
		GameBoard moveBoard = new GameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.moveGeneratesExtraTurn(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new GameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.moveGeneratesExtraTurn(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new GameBoard(this);
			}
		}
		return null;
	}

	private BoardMove tryToTakeColors (GEM... colors) {
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
		GameBoard moveBoard = new GameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.moveIsValidAndOfColor(moveBoard, nextMove, color)) {
						return nextMove;
					}
				}
				moveBoard = new GameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.moveIsValidAndOfColor(moveBoard, nextMove, color)) {
						return nextMove;
					}
				}
				moveBoard = new GameBoard(this);
			}
		}
		return null;
	}

	private Move takeThrees() {
		BoardMove nextMove = null;
		GameBoard moveBoard = new GameBoard(this);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					if (moveBoard.moveIsThreeAndNotSkulls(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new GameBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					if (moveBoard.moveIsThreeAndNotSkulls(moveBoard, nextMove)) {
						return nextMove;
					}
				}
				moveBoard = new GameBoard(this);
			}
		}
		return null;
	}

	@Override
	public Move calculateNextMove (int not, BoardMove used) {
		Move finalMove = null;
		switch (boardState) {
		case CHARGE:
			finalMove = takeFoursOrFives();
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.GREEN, GEM.PURPLE);
			if (finalMove != null) break;
			if (cards[SHAMAN].get_status() == CARD.STATUS.ACTIVE) {
				finalMove = new Cast(cards[SHAMAN], 0, 0, TARGET.NONE);
				break;
			}
			if (cards[DRYAD].get_status() == CARD.STATUS.ACTIVE) {
				if (Utils.MF_CAST_ON_TYRI) {
					finalMove = new Cast(cards[DRYAD], cards[TYRI].getX(), cards[TYRI].getY(), TARGET.CARD);
				} else {
					finalMove = new Cast(cards[DRYAD], cards[DRYAD].getX(), cards[DRYAD].getY(), TARGET.CARD);
				}
				Utils.MF_CAST_ON_TYRI = !Utils.MF_CAST_ON_TYRI;
				break;
			}
			finalMove = tryToTakeColors(GEM.BROWN, GEM.BLUE, GEM.YELLOW, GEM.RED, GEM.SKULL);
			break;
		case EXECUTE:
			getStoneCount();
			finalMove = castTyriOnConditions(finalMove, GEM.GREEN, 8);
			if (finalMove != null) break;
			finalMove = castTyriOnConditions(finalMove, GEM.PURPLE, 8);
			if (finalMove != null) break;
			if (cards[SHAMAN].get_status() == CARD.STATUS.ACTIVE) {
				finalMove = new Cast(cards[SHAMAN], 0, 0, TARGET.NONE);
				break;
			}
			if (cards[DRYAD].get_status() == CARD.STATUS.ACTIVE) {
				if (Utils.MF_CAST_ON_TYRI) {
					finalMove = new Cast(cards[DRYAD], cards[TYRI].getX(), cards[TYRI].getY(), TARGET.CARD);
				} else {
					finalMove = new Cast(cards[DRYAD], cards[DRYAD].getX(), cards[DRYAD].getY(), TARGET.CARD);
				}
				Utils.MF_CAST_ON_TYRI = !Utils.MF_CAST_ON_TYRI;
				break;
			}
			if (cards[SPIDER].get_status() == CARD.STATUS.ACTIVE) {
				Coordinates c = findBestSpiderTarget();
				finalMove = new Cast(cards[SPIDER], c.x, c.y, TARGET.BOARD);
				break;
			}
			finalMove = takeFoursOrFives();
			if (finalMove != null) break;
			finalMove = castTyriOnConditions(finalMove, GEM.GREEN, 5);
			if (finalMove != null) break;
			finalMove = castTyriOnConditions(finalMove, GEM.PURPLE, 5);
			if (finalMove != null) break;
			finalMove = tryToTakeColors(GEM.BROWN, GEM.BLUE, GEM.YELLOW, GEM.RED, GEM.SKULL);
			break;
		case FINISH:
			finalMove = takeThrees();
			break;
		default:
			break;
		}

		return finalMove;
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

	private Cast castTyriOnConditions(Move finalMove, GEM color, int amount) {
		Cast tyriCast = null;
		if (counts[color.ordinal()] >= amount) {
			Coordinates c = getGemOfColor(color);
			tyriCast = new Cast(cards[TYRI], c.x, c.y, TARGET.BOARD);
		}
		return tyriCast;
	}

	private Coordinates getGemOfColor(GEM color) {
		for (int row = 0; row < 8; row++)
			for (int column = 0; column < 8; column++)
				if (board[row][column] == color) 
					return new Coordinates(row, column);
		return null;
	}

	@Override
	protected void initBoard(long[][] values) {
		for (int x = 0; x < values.length; x++) {
			for (int y = 0; y < values.length; y++) {
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

	public void updateCardsAndStatus(BufferedImage image) {
		for (CARD c : cards) {
			long value = Utils.isCardReady(c, image);

			final long active = Math.abs(value - c.getActiveValue());
			final long inactive = Math.abs(value - c.getInactiveValue());
			final long dead = Math.abs(value - c.getDeadValue());

			long min = Math.min(active, Math.min(inactive, dead));
			
			if (min == active) {
				c.set_status(STATUS.ACTIVE);
			} else if (min == inactive) {
				c.set_status(STATUS.INACTIVE);
			} else {
				c.set_status(STATUS.DEAD);
			}
		}

		if (cards[TYRI].get_status() == STATUS.ACTIVE) {
			boardState = STATE.EXECUTE;
		} else if (cards[TYRI].get_status() == STATUS.INACTIVE) {
			boardState = STATE.CHARGE;
		} else {
			boardState = STATE.FINISH;
		}
	}
}
