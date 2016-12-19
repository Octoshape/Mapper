package myPackage;

import myPackage.GameBoard.CARD;

public class Cast extends Move {
	@Override
	public String toString() {
		return "Casting " + card + " with target: " + target;
	}
	
	private CARD card;
	private int x;
	private int y;
	private TARGET target;
	
	public TARGET getTarget() {
		return target;
	}

	enum TARGET {CARD, BOARD, NONE}

	public CARD getCard() {
		return card;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Cast (CARD c, int x, int y, TARGET target) {
		this.card = c;
		this.x = x;
		this.y = y;
		this.target = target;
	}
}
