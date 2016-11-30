package myPackage;

import java.util.List;

public class GemsMatch {
	public List<Coordinates> coords;
	public Coordinates replacementCoord;
	
	public GemsMatch(List<Coordinates> coords, Coordinates replacementCoord) {
		this.coords = coords;
		this.replacementCoord = replacementCoord;
	}
	
	public int size() {
		return coords.size();
	}
}