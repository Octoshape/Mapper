package myPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myPackage.Utils.DIRECTION;
import myPackage.Utils.VALUE;

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

	public VALUE[][] board;

	public VALUE[][] getBoard() {
		return board;
	}

	public GameBoard() {
		board = new Utils.VALUE[8][8];
	}

	public GameBoard(GameBoard otherBoard) {
		board = new Utils.VALUE[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = otherBoard.board[i][j];
	}

	public GameBoard(VALUE[][] vals) {
		board = vals;
	}

	public GameBoard(String[][] vals) {
		board = new Utils.VALUE[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = Utils.VALUE.valueOf(vals[i][j]);
	}

//	private List<Move> threes = new ArrayList<>();
	private Move three = null;
	private Move four = null;
	private Move five = null;

	public Move calculateNextMove () {
		GameBoard moveBoard = new GameBoard(this);
		Move nextMove = null;
		found_five:
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (j < 7) {
						nextMove = new Move(DIRECTION.R, i, j);
						moveBoard.makeMove(nextMove);
						moveBoard.findMatches(nextMove);
//						moveBoard.assignMove(moveBoard.findBestMatch(), nextMove);
						overwriteMoves(moveBoard);
						if (five != null) {
							break found_five;
						}
					}
					moveBoard = new GameBoard(this);
					
					if (i < 7) {
						nextMove = new Move(DIRECTION.D, i, j);
						moveBoard.makeMove(nextMove);
						moveBoard.findMatches(nextMove);
//						moveBoard.assignMove(moveBoard.findBestMatch(), nextMove);
						overwriteMoves(moveBoard);
						if (five != null) {
							break found_five;
						}
					}
					moveBoard = new GameBoard(this);
				}
			}
		
		if (five != null) {
			return five;
		}
		
//		Move bestThree = simulateThrees();
//		
//		if (bestThree != null) {
//			return bestThree;
//		}
		
		if (four != null) {
			return four;
		}
		
		if (three != null) {
			return three;
		}
		
		throw new IllegalStateException("Board found no valid moves!");
	}

//	private Move simulateThrees() {
//		for (Move nextMove : threes) {
//			GameBoard moveBoard = new GameBoard(this);
//			if (moveBoard.simulate(nextMove)) {
//				return nextMove;
//			}
//			
//			Move nextnextMove = moveBoard.calculateNextMove();
//			moveBoard.makeMove(nextnextMove);
//			if (moveBoard.findBestMatch() >= 5) {
//				return nextMove;
//			}
//		}
//		return null;
//	}

//	private int findBestMatch() {
//		int result = 1;
//		for (int i = 0; i < 8; i++) {
//			for (int j = 0; j < 8; j++) {
//				int tmp = findCommonStones(i, j);
//
//				if (tmp > result) {
//					result = tmp;
//				}
//			}
//		}
//		return result;
//	}

	private void overwriteMoves(GameBoard moveBoard) {
//		this.threes.addAll(moveBoard.threes);

		if (moveBoard.three != null) {
			this.four = moveBoard.three;
		}
		
		if (moveBoard.four != null && this.four == null) {
			this.four = moveBoard.four;
		}

		if (moveBoard.five != null) {
			this.five = moveBoard.five;
		}
	}

	private void findMatches(Move lastMove) {
		int activeX = lastMove.row, activeY = lastMove.column;
		int amount = findCommonStones(activeX, activeY);
		assignMove(amount, lastMove);
		
		int passiveX = lastMove.row2, passiveY = lastMove.column2;
		amount = findCommonStones(passiveX, passiveY);
		assignMove(amount, lastMove);
	}
	
	private void assignMove(int amount, Move lastMove) {
		switch (amount) {
		case 3:
//			threes.add(lastMove);
			three = lastMove;
			break;
		case 4:
			four = lastMove;
			break;
		case 5:
		case 6:
		case 7:
			five = lastMove;
			break;
		default:
			break;
		}
	}

	private int findCommonStones(int activeX, int activeY) {
		VALUE val = this.board[activeX][activeY];

		int result = 1;
		int leftright = 0;
		int updown = 0;

		// go left
		for (int currentX = activeX; --currentX >= 0 && this.board[currentX][activeY] == val; leftright++);
		// go right
		for (int currentX = activeX; ++currentX < 8 && this.board[currentX][activeY] == val; leftright++);
		// go up
		for (int currentY = activeY; --currentY >= 0 && this.board[activeX][currentY] == val; updown++);
		// go down
		for (int currentY = activeY; ++currentY < 8 && this.board[activeX][currentY] == val; updown++);
		
		if (leftright > 1) {
			result += leftright;
		}

		if (updown > 1) {
			result += updown;
		}

		return result;
	}

	public void makeMove(Move m) {
		if (validMove(m)) {
			swap (m.row, m.column, m.row2, m.column2);
		}
//		simulate();
		balanceBoard();
	}

//	private boolean simulate(Move m) {
//		return false;
//	}

	private int balanceBoard() {
		return 1;
	}
	
	private void swap(int row, int column, int row2, int column2) {
		VALUE tmp = this.board[row][column];
		this.board[row][column] = this.board[row2][column2];
		this.board[row2][column2] = tmp;
	}

	private boolean validMove(Move m) {
		return (this.board[m.row][m.column] != VALUE.TREASURE && this.board[m.row2][m.column2] != VALUE.TREASURE);
	}

	public static class Move {

		@Override
		public String toString() {
			return "Move: (" + row + ", " + column + ") " + dir;
		}

		public Move(DIRECTION d, int r, int c) {
			dir = d;
			row = r;
			column = c;
			
			switch(d) {
			case D:
				row2 = row + 1;
				column2 = column;
				break;
			case L:
				row2 = row;
				column2 = column - 1;
				break;
			case R:
				row2 = row;
				column2 = column + 1;
				break;
			case U:
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
		
	}
}
