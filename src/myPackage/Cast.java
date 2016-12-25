package myPackage;

import myPackage.CARD;

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

	enum TARGET {ALLY, BOARD, ENEMY, NONE}

	public CARD getCard() {
		return card;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Cast (CARD cast, int row, int column) {
		this.card = cast;
		this.x = row;
		this.y = column;
		this.target = TARGET.BOARD;
	}

	public Cast (CARD cast, CARD allyTarget) {
		this.card = cast;
		this.x = allyTarget.getX();
		this.y = allyTarget.getY();
		this.target = TARGET.ALLY;
	}
	
	public Cast (CARD cast, boolean enemy) {
		this.card = cast;
		this.target = enemy ? TARGET.ENEMY : TARGET.NONE;
	}
}
