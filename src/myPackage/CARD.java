package myPackage;

import myPackage.Utils.GEM;

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
	
	/*** CAST METHODS ***/
	
	Cast castOnAlly(CARD target) {
		return new Cast(this, target);
	}
	
	Cast castOnBoard(int row, int column) {
		return new Cast(this, row, column);
	}
	
	Cast castWithoutTarget() {
		return new Cast(this, false);
	}
	
	Cast castOnTopEnemy() {
		return new Cast(this, true);
	}
}
