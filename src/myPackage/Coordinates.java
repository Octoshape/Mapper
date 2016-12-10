package myPackage;

public class Coordinates {
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	public int x;
	public int y;
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
}