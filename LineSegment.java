public class LineSegment {
	private static int nextLineSegment = 1;
	private int segmentNumber;
	private Line onLine;
	
	public LineSegment(Line aLine) {
		onLine = aLine;
		segmentNumber = nextLineSegment++;
	}
	
	public Line getLine() { return onLine; }
	public String toString() { return String.format("LS:%04d(on %s line)", segmentNumber, onLine.getName()); }
}