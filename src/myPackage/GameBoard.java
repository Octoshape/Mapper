package myPackage;

import java.util.ArrayList;
import java.util.List;

import myPackage.Utils.DIRECTION;
import myPackage.Utils.GEM;

public class GameBoard {

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

	public GEM[][] board;

	public GEM[][] getBoard() {
		return board;
	}

	public GameBoard() {
		board = new Utils.GEM[8][8];
	}

	public GameBoard(GameBoard otherBoard) {
		board = new Utils.GEM[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = otherBoard.board[i][j];
	}

	public GameBoard(GEM[][] vals) {
		board = vals;
	}

	public GameBoard(String[][] vals) {
		board = new Utils.GEM[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = Utils.GEM.valueOf(vals[i][j]);
	}

	public Move calculateNextMove (int depth) {
		GameBoard moveBoard = new GameBoard(this);
		Move nextMove = null;
		Move bestMove = new Move(0, 0, 0);
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new Move(DIRECTION.RIGHT, row, column);
					bestMove = findBestMove(depth, moveBoard, nextMove, bestMove);
				}
				moveBoard = new GameBoard(this);

				if (row < 7) {
					nextMove = new Move(DIRECTION.DOWN, row, column);
					bestMove = findBestMove(depth, moveBoard, nextMove, bestMove);
				}
				moveBoard = new GameBoard(this);
			}
		}
		return bestMove;
	}

	private Move findBestMove(int depth, GameBoard moveBoard, Move nextMove, Move bestMove) {
		moveBoard.makeMove(nextMove);
		if (depth > 0) {
			moveBoard.simulate(nextMove);
			Move nextnextMove = moveBoard.calculateNextMove(depth - 1);
			nextMove.extraTurns += nextnextMove.extraTurns;
			nextMove.turnsUsed += nextnextMove.turnsUsed;
			System.out.println("First move:");
			System.out.println(nextMove);
			System.out.println("Second move:");
			System.out.println(nextnextMove);
			System.out.println();
			System.out.println();
			if (nextMove.compareTo(bestMove) == 1) {
				bestMove = nextMove;
			}
		} else {
			if (bestMove.extraTurns == -1) {
				if (nextMove.extraTurns >= bestMove.extraTurns) {
					bestMove = nextMove;
				}
			} else {
				if (nextMove.extraTurns > bestMove.extraTurns) {
					bestMove = nextMove;
				}
			}
		}
		
		return bestMove;
	}

	private void simulate(Move nextMove) {
		boolean foundMatches = false;
		GemsMatch match;

		if (nextMove != null) {
			match = findMatchingStones(nextMove.row, nextMove.column, nextMove);
			foundMatches |= removeStones(match);
			match = findMatchingStones(nextMove.row2, nextMove.column2, nextMove);
			foundMatches |= removeStones(match);
		}

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				do  {
					match = findMatchingStones(x, y, null);
					foundMatches |= removeStones(match);
				} while (!match.coords.isEmpty());
			}
		}

		if (foundMatches) {
			collapse();
		}
	}
	
	private void collapse() {
		for (int column = 0; column < 8; column++) {
			int row = 7, fetchRow = 7;
			while (row >= 0) {
				if (board[fetchRow][column] != GEM.EMPTY) {
					board[row--][column] = board[fetchRow][column];
				}
				fetchRow--;
				if (fetchRow == -1) {
					// fill rest with empty.
					while (row >= 0) {
						board[row--][column] = GEM.EMPTY;
					}
				}
			}
		}
		simulate(null);
	}

	private boolean removeStones(GemsMatch match) {
		if (match.coords.isEmpty()) {
			return false;
		}

		// save the replacement's value.
		GEM currVal = board[match.replacementCoord.x][match.replacementCoord.y];

		// remove all stones.
		for (Coordinates c : match.coords) {
			board[c.x][c.y] = GEM.EMPTY;
		}

		// add better replacement.
		board[match.replacementCoord.x][match.replacementCoord.y] = GEM.values()[currVal.ordinal() + 1];

		return true;
	}

	/**
	 * @param x
	 * @param y
	 * @param move 
	 * @return if something has matched
	 */
	private GemsMatch findMatchingStones(int x, int y, Move move) {
		List<Coordinates> removeStones = new ArrayList<>(), leftright = new ArrayList<>(), updown = new ArrayList<>();
		Coordinates replacement = new Coordinates(-1, -1);

		GEM val = board[x][y];
		if (val == GEM.EMPTY) {
			return new GemsMatch(removeStones, null);
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
			return new GemsMatch(removeStones, null);
		}

		// Calculate replacement coordinates.

		if (move != null) {
			replacement = new Coordinates(x, y);
		} else if (leftright.size() > 1 && updown.size() > 1) {
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

		if (move != null) {
			assignExtraTurns(removeStones.size(), move);
		}
		return new GemsMatch(removeStones, replacement);
	}

	private void assignExtraTurns (int amount, Move move) {
		switch (amount) {
		case 3:
			move.extraTurns = -1;
			break;
		case 4:
			move.extraTurns = 0;
			break;
		case 5:
		case 6:
		case 7:
			move.extraTurns = 1;
			break;
		default:
			break;
		}
	}

	public void makeMove(Move m) {
		if (m == null) {
			return;
		}

		if (validMove(m)) {
			swap (m.row, m.column, m.row2, m.column2);
		}

		int match1 = findMatchingStones(m.row, m.column, m).size();
		int match2 = findMatchingStones(m.row2, m.column2, m).size();
		assignExtraTurns(Math.max(match1, match2), m);
	}

	private void swap(int row, int column, int row2, int column2) {
		GEM tmp = this.board[row][column];
		this.board[row][column] = this.board[row2][column2];
		this.board[row2][column2] = tmp;
	}

	private boolean validMove(Move m) {
		return (this.board[m.row][m.column] != GEM.TREASURE && this.board[m.row2][m.column2] != GEM.TREASURE);
	}

	public static class Move implements Comparable<Move> {

		@Override
		public String toString() {
			return "Move: (" + row + ", " + column + ") " + dir + " giving " + extraTurns + " extra turns and using " + turnsUsed + " turns.";
		}

		public Move(DIRECTION d, int r, int c) {
			dir = d;
			row = r;
			column = c;
			extraTurns = -500;
			turnsUsed = 1;

			switch(d) {
			case DOWN:
				row2 = row + 1;
				column2 = column;
				break;
			case LEFT:
				row2 = row;
				column2 = column - 1;
				break;
			case RIGHT:
				row2 = row;
				column2 = column + 1;
				break;
			case UP:
				row2 = row - 1;
				column2 = column;
				break;
			default:
				break;

			}
		}

		public Move(int d, int r, int c) {
			this(DIRECTION.values()[d], r, c);
		}

		public DIRECTION dir;
		public int row;
		public int column;
		public int row2;
		public int column2;
		public int extraTurns;
		public int turnsUsed;
		
		
		@Override
		public int compareTo(Move otherMove) {
			if (this.extraTurns - this.turnsUsed > otherMove.extraTurns - otherMove.turnsUsed)
				return 1;
			else if (this.extraTurns - this.turnsUsed < otherMove.extraTurns - otherMove.turnsUsed)
				return -1;
			else if (this.turnsUsed > otherMove.turnsUsed) {
				return 1;
			} else if (this.turnsUsed < otherMove.turnsUsed) {
				return -1;
			} else
				return 0;
		}
	}
}
