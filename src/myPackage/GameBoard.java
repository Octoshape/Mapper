package myPackage;

import myPackage.Utils.DIRECTION;
import myPackage.Utils.MAP_GEM;

public class GameBoard extends AbstractGameBoard {

	public GameBoard() {
		board = new Utils.MAP_GEM[8][8];
	}

	public GameBoard(AbstractGameBoard otherBoard) {
		board = new Utils.MAP_GEM[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = otherBoard.board[i][j];
	}

	public GameBoard(MAP_GEM[][] vals) {
		board = vals;
	}

	public GameBoard(String[][] vals) {
		board = new Utils.MAP_GEM[8][8];
		for (int i = 0; i < 8; i++) 
			for (int j = 0; j < 8; j++) 
				board[i][j] = Utils.MAP_GEM.valueOf(vals[i][j]);
	}

	public Move calculateNextMove (int depth, Move bestMove) {
		GameBoard moveBoard = new GameBoard(this);
		Move nextMove = null;
		if (bestMove == null) {
			bestMove = new Move(0, 0, 0);
		}
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new Move(DIRECTION.RIGHT, row, column);
					bestMove = moveBoard.findBestMove(depth, nextMove, bestMove);
				}
				moveBoard = new GameBoard(this);

				if (row < 7) {
					nextMove = new Move(DIRECTION.DOWN, row, column);
					bestMove = moveBoard.findBestMove(depth, nextMove, bestMove);
				}
				moveBoard = new GameBoard(this);
			}
		}
		
		return bestMove;
	}

	private Move findBestMove(int depth, Move nextMove, Move bestMove) {
		if (!makeMove(nextMove)) {
			return bestMove;
		}

		if (depth > 0) {
			simulate(nextMove, true);
			Move bestNextMove = nextMove.nextMove;
			for (int currentDepth = depth - 1; currentDepth >= 0; currentDepth--){
				bestNextMove = calculateNextMove(currentDepth, bestNextMove);
			}
			nextMove.nextMove = bestNextMove;
			nextMove.totalExtraTurns += nextMove.nextMove.totalExtraTurns;
			nextMove.totalTurnsUsed += nextMove.nextMove.totalTurnsUsed;
			//			System.out.println("First move:");
			//			System.out.println(nextMove);
			//			System.out.println("Second move:");
			//			System.out.println(nextMove.nextMove);
			//			System.out.println();
			//			System.out.println();
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

	private void simulate(Move nextMove, boolean firstTime) {
		boolean foundMatches = false, foundAirMatches = false;
		GemsMatch match;

		if (firstTime) {
			match = findMatchingStones(nextMove.row, nextMove.column, nextMove);
			removeStones(match);
			match = findMatchingStones(nextMove.row2, nextMove.column2, nextMove);
			removeStones(match);
			
			foundMatches = true;
		}

		do {
			foundAirMatches = false;
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					match = findMatchingStones(x, y, null);
					removeStones(match);
					foundAirMatches |= !match.coords.isEmpty();
					if (nextMove.biggestMatch < match.size()) {
						assignExtraTurns(match.size(), nextMove);
					}
				}
			}
			foundMatches |= foundAirMatches;
		} while (foundAirMatches);

		if (foundMatches) {
			collapse();
			simulate(nextMove, false);
		}
	}
}
