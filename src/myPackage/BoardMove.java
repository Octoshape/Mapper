package myPackage;

import myPackage.Utils.DIRECTION;

public class BoardMove extends Move implements Comparable<BoardMove> {
	
	@Override
	public String toString() {
		return "Move: (" + row + ", " + column + ") " + dir + " giving " + totalExtraTurns + " extra turns and using " + totalTurnsUsed + " turns.";
	}

	public BoardMove(DIRECTION d, int r, int c) {
		dir = d;
		row = r;
		column = c;
		extraTurns = -500;
		totalExtraTurns = -500;
		totalTurnsUsed = 1;

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

	public BoardMove(int d, int r, int c) {
		this(DIRECTION.values()[d], r, c);
	}

	public int biggestMatch;
	public BoardMove nextMove;
	public DIRECTION dir;
	public int row;
	public int column;
	public int row2;
	public int column2;
	public int extraTurns;
	public int totalTurnsUsed;
	public int totalExtraTurns;

	public void setExtraTurns(int val) {
		totalExtraTurns = val;
		extraTurns = val;
	}

	@Override
	public int compareTo(BoardMove otherMove) {
		if (this.totalExtraTurns + this.totalTurnsUsed > otherMove.totalExtraTurns + otherMove.totalTurnsUsed) {
			return 1;
		} else if (this.totalExtraTurns + this.totalTurnsUsed < otherMove.totalExtraTurns + otherMove.totalTurnsUsed) {
			return -1;
		} else if (this.totalExtraTurns > otherMove.totalExtraTurns) {
			return 1;
		} else if (this.totalExtraTurns < otherMove.totalExtraTurns) {
			return -1;
		} else
			return 0;
	}
}