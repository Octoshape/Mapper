package myPackage;

public class CARD {
	public enum STATUS {ACTIVE, INACTIVE, DEAD};
	private STATUS status;
	private Integer position;
	private long baseValue;
	private String name;

	CARD(String name, Integer position) {
		this.status = STATUS.INACTIVE;
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	long getBaseValue() {
		return baseValue;
	}

	void setPosition (int value) {
		this.position = value;
	}

	int getPosition () {
		return this.position;
	}

	void setBaseValue (long value) {
		this.baseValue = value;
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
