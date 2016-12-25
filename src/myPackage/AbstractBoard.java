package myPackage;

import java.util.ArrayList;
import java.util.List;

import myPackage.Utils.MAP_GEM;

public abstract class AbstractBoard {
	
	protected AbstractBoard(long[][] vals) {
		board = new IGem[8][8];
		initBoard(vals);
	}

	protected AbstractBoard(AbstractBoard otherBoard) {
		board = new IGem[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = otherBoard.board[i][j];
	}


	protected abstract void initBoard(long[][] values);

	public IGem[][] board;

	@Override
	public String toString() {
		String result = "";
	
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				result += String.format("%1$" + 8 + "s", this.board[i][j]) + " ";
			}
			result += "\n";
		}
		return result;
	}

	protected void collapse() {
		for (int column = 0; column < 8; column++) {
			int row = 7, fetchRow = 7;
			while (row >= 0) {
				if (board[fetchRow][column] != MAP_GEM.EMPTY) {
					board[row--][column] = board[fetchRow][column];
				}
				fetchRow--;
				if (fetchRow == -1) {
					// fill rest with empty.
					while (row >= 0) {
						board[row--][column] = MAP_GEM.EMPTY;
					}
				}
			}
		}
	}

	protected GemsMatch findMatchingStones(int x, int y, BoardMove move) {
		List<Coordinates> removeStones = new ArrayList<>(), leftright = new ArrayList<>(), updown = new ArrayList<>();
		Coordinates replacement = new Coordinates(-1, -1);
	
		IGem val = board[x][y];
		if (val == MAP_GEM.EMPTY || val == MAP_GEM.TREASURE) {
			return new GemsMatch(removeStones, replacement);
		}
	
		// go left
		for (int currentX = x; --currentX >= 0 && this.board[currentX][y] == val; leftright.add(new Coordinates(currentX, y)));
		// go right
		for (int currentX = x; ++currentX < 8 && this.board[currentX][y] == val; leftright.add(new Coordinates(currentX, y)));
		// go up
		for (int currentY = y; --currentY >= 0 && this.board[x][currentY] == val; updown.add(new Coordinates(x, currentY)));
		// go down
		for (int currentY = y; ++currentY < 8 && this.board[x][currentY] == val; updown.add(new Coordinates(x, currentY)));
	
		if (leftright.size() > 1) {
			removeStones.addAll(leftright);
		}
	
		if (updown.size() > 1) {
			removeStones.addAll(updown);
		}
	
		if (!removeStones.isEmpty()) {
			removeStones.add(new Coordinates(x, y));
		} else {
			return new GemsMatch(removeStones, replacement);
		}
	
		// Calculate replacement coordinates.
	
		if (move != null) {
			replacement = new Coordinates(x, y);
			assignExtraTurns(removeStones.size(), move);
		} else {
			// Found air match!
			int matchSize = removeStones.size();
			for (Coordinates c : removeStones) {
				if (c.x == x && c.y == y) {
					continue;
				} else {
					GemsMatch newMatch = searchInsideMatch(c.x, c.y);
					if (newMatch.size() > matchSize) {
						return newMatch;
					}
				}
			}
			
			// Air match resolved, x y is not on a branch, we see the whole match.
			
			if (leftright.size() > 1 && updown.size() > 1) {
				// cross
				if (removeStones.size() % 2 == 0) {
					Utils.SNAPSHOT = true;
				}
	
				replacement = new Coordinates(x, y);
	
			} else {
				// line
				if (removeStones.size() % 2 == 0) {
					Utils.SNAPSHOT = true;
				}
				int xMean = 0, yMean = 0;
				for (Coordinates c : removeStones) {
					xMean += c.x;
					yMean += c.y;
				}
				xMean /= removeStones.size();
				yMean /= removeStones.size();
	
				replacement = new Coordinates(xMean, yMean);
			}
		}
	
		return new GemsMatch(removeStones, replacement);
	}

	private GemsMatch searchInsideMatch(int x, int y) {
		List<Coordinates> removeStones = new ArrayList<>(), leftright = new ArrayList<>(), updown = new ArrayList<>();
		Coordinates replacement = new Coordinates(-1, -1);
	
		IGem val = board[x][y];
	
		// go left
		for (int currentX = x; --currentX >= 0 && this.board[currentX][y] == val; leftright.add(new Coordinates(currentX, y)));
		// go right
		for (int currentX = x; ++currentX < 8 && this.board[currentX][y] == val; leftright.add(new Coordinates(currentX, y)));
		// go up
		for (int currentY = y; --currentY >= 0 && this.board[x][currentY] == val; updown.add(new Coordinates(x, currentY)));
		// go down
		for (int currentY = y; ++currentY < 8 && this.board[x][currentY] == val; updown.add(new Coordinates(x, currentY)));
	
		if (leftright.size() > 1) {
			removeStones.addAll(leftright);
		}
	
		if (updown.size() > 1) {
			removeStones.addAll(updown);
		}
	
		removeStones.add(new Coordinates(x, y));
	
		// Calculate replacement coordinates.
	
		if (leftright.size() > 1 && updown.size() > 1) {
			// cross
			replacement = new Coordinates(x, y);
	
		} else {
			// line
			if (removeStones.size() % 2 == 0) {
				Utils.SNAPSHOT = true;
			}
			int xMean = 0, yMean = 0;
			for (Coordinates c : removeStones) {
				xMean += c.x;
				yMean += c.y;
			}
			xMean /= removeStones.size();
			yMean /= removeStones.size();
	
			replacement = new Coordinates(xMean, yMean);
		}
	
		return new GemsMatch(removeStones, replacement);
	}


	protected void assignExtraTurns(int amount, BoardMove move) {
		switch (amount) {
		case 3:
			move.setExtraTurns(-1);
			break;
		case 4:
			move.setExtraTurns(0);
			break;
		case 5:
		case 6:
		case 7:
			move.setExtraTurns(1);
			break;
		default:
			break;
		}
		
		move.biggestMatch = amount;
	}

	public abstract Move calculateNextMove(int depth, BoardMove bestMove);

	protected void swap(int row, int column, int row2, int column2) {
		IGem tmp = this.board[row][column];
		this.board[row][column] = this.board[row2][column2];
		this.board[row2][column2] = tmp;
	}
}
