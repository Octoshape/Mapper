package myPackage;

import myPackage.Utils.DIRECTION;
import myPackage.Utils.MAP_GEM;

public class TreasureBoard extends AbstractBoard {

	public TreasureBoard(long[][] vals) {
		super(vals);
	}

	public TreasureBoard(AbstractBoard otherBoard) {
		super(otherBoard);
	}

	public BoardMove calculateNextMove (int depth, BoardMove bestMove) {
		TreasureBoard moveBoard = new TreasureBoard(this);
		BoardMove nextMove = null;
		if (bestMove == null) {
			bestMove = new BoardMove(0, 0, 0);
		}
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if (column < 7) {
					nextMove = new BoardMove(DIRECTION.RIGHT, row, column);
					bestMove = moveBoard.findBestMove(depth, nextMove, bestMove);
				}
				moveBoard = new TreasureBoard(this);

				if (row < 7) {
					nextMove = new BoardMove(DIRECTION.DOWN, row, column);
					bestMove = moveBoard.findBestMove(depth, nextMove, bestMove);
				}
				moveBoard = new TreasureBoard(this);
			}
		}
		
		return bestMove;
	}

	private BoardMove findBestMove(int depth, BoardMove nextMove, BoardMove bestMove) {
		if (!makeMove(nextMove)) {
			return bestMove;
		}

		if (depth > 0) {
			simulate(nextMove, true);
			BoardMove bestNextMove = nextMove.nextMove;
			for (int currentDepth = depth - 1; currentDepth >= 0; currentDepth--){
				bestNextMove = calculateNextMove(currentDepth, bestNextMove);
			}
			nextMove.nextMove = bestNextMove;
			nextMove.totalExtraTurns += nextMove.nextMove.totalExtraTurns;
			nextMove.totalTurnsUsed += nextMove.nextMove.totalTurnsUsed;
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
	
	protected void simulate(BoardMove nextMove, boolean firstTime) {
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
	
	@Override
	protected void initBoard(long[][] values) {
		for (int x = 0; x < values.length; x++) {
			for (int y = 0; y < values.length; y++) {
				long value = values[y][x];
				if (value < -4300000000l)
					board[x][y] = MAP_GEM.valueOf("VAULT");
				else if (value < -4070000000l)
					board[x][y] = MAP_GEM.valueOf("SILVER");
				else if (value < -3800000000l)
					board[x][y] = MAP_GEM.valueOf("RED");
				else if (value < -3400000000l)
					board[x][y] = MAP_GEM.valueOf("IRON");
				else if (value < -3100000000l)
					board[x][y] = MAP_GEM.valueOf("BAG");
				else if (value < -2800000000l)
					board[x][y] = MAP_GEM.valueOf("COPPER");
				else if (value < -2500000000l)
					board[x][y] = MAP_GEM.valueOf("GREEN");
				else if (value < -1300000000l)
					board[x][y] = MAP_GEM.valueOf("GOLD");
				else
					Utils.SKIP = true;
			}
		}
	}

	protected boolean validMove(BoardMove m) {
		return (this.board[m.row][m.column] != MAP_GEM.VAULT && this.board[m.row2][m.column2] != MAP_GEM.VAULT);
	}
	
	@Override
	protected boolean makeMove(BoardMove m) {
		if (!validMove(m)) {
			return false;
		}
		return super.makeMove(m);
	}
	
	@Override
	protected void removeStones(GemsMatch match) {
		if (match.coords.isEmpty()) {
			return;
		}

		// save the replacement's value.
		MAP_GEM currVal = (MAP_GEM)board[match.replacementCoord.x][match.replacementCoord.y];
		
		super.removeStones(match);

		// add better replacement.
		board[match.replacementCoord.x][match.replacementCoord.y] = MAP_GEM.values()[currVal.ordinal() + 1];
	}
}
