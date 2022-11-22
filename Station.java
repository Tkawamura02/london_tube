import java.util.ArrayList;

public class Station {
	private String name;
	private ArrayList<Line> lines;

	public Station(String aName) {
		name = aName;
		lines = new ArrayList<Line>();
	}

	public String getName() {
		return name;
	}

	public void addLine(Line aLine) {
		lines.add(aLine);
	}

	public String toString() {
		return name + " Station";
	}
}
