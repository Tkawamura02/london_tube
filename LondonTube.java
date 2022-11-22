import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class LondonTube {

	public static void main(String[] args) throws IllegalArgumentException {
		Scanner kbrd = new Scanner(System.in);
		
		System.out.println("London Tube - <Your Name>\n");

		Graph<Station, LineSegment> g = readNetwork();

		while (true) {
			System.out.print("Where are you starting (done to quit)? ");
			String startStation = kbrd.nextLine().trim();
			if (startStation.equalsIgnoreCase("done")) break;
			Vertex<Station> origin = findStation(g, startStation);
			if (origin == null) {
				System.out.println("Can't find " + startStation);
				continue;
			}
			
			System.out.print("Where are you going (done to quit)? ");
			String endStation = kbrd.nextLine().trim();
			if (endStation.equalsIgnoreCase("done")) break;

			Vertex<Station> destination = findStation(g, endStation);
			if (destination == null) {
				System.out.println("Can't find " + endStation);
				continue;
			}

			PositionalList<Edge<LineSegment>> path = getPath(g, origin, destination);
			System.out.println("\nInstructions:");
			printPath(g, path, origin);
			System.out.println();
		}
	}

	public static PositionalList<Edge<LineSegment>> getPath(Graph<Station, LineSegment> g, Vertex<Station> origin,
			Vertex<Station> destination) {
		Set<Vertex<Station>> known = new HashSet<Vertex<Station>>();
		Map<Vertex<Station>, Edge<LineSegment>> forest = new ProbeHashMap<Vertex<Station>, Edge<LineSegment>>();

		GraphAlgorithms.<Station, LineSegment>BFS(g, origin, known, forest);
		PositionalList<Edge<LineSegment>> path = GraphAlgorithms.<Station, LineSegment>constructPath(g, origin,
				destination, forest);

		return path;
	}

	public static void printPath(Graph<Station, LineSegment> g, PositionalList<Edge<LineSegment>> path,
			Vertex<Station> origin) {
		Vertex<Station> current = origin;
		Vertex<Station> next;

		for (Edge<LineSegment> e : path) {
			Vertex<Station>[] v = g.endVertices(e);
			if (v[0] == current)
				next = v[1];
			else
				next = v[0];

			System.out.printf("Take the %s line from %s to %s\n", e.getElement().getLine().getName(),
					current.getElement().getName(), next.getElement().getName());
			current = next;
		}
	}

	public static Graph<Station, LineSegment> readNetwork() {
		Graph<Station, LineSegment> g = new AdjacencyMapGraph<>(false);
		Scanner in = null;

		try {
			File f = new File("london.txt");
			in = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.out.println("File \"london.txt\" not found.");
			System.exit(1);
		}

		while (in.hasNext()) {
			String key = in.next();
			if (key.equals("Line")) {
				String lineName = in.nextLine().trim();
				Line line = new Line(lineName);

				Vertex<Station> from = null;
				
				while (in.hasNext()) {
					String stationName = in.nextLine().trim();
					if (stationName.equals("End"))
						break;
					
					Vertex<Station> station = findStation(g, stationName);
					if (station == null) 
						station = g.insertVertex(new Station(stationName));
					
					if (from == null) {
						from = station;
					} else {
						try {
						g.insertEdge(from, station, new LineSegment(line));
						}
						catch (IllegalArgumentException e) {
							System.out.printf("Track already exists from %s to %s\n", from.getElement().getName(), station.getElement().getName());
							System.exit(3);
						}
						from = station;
					}
				}
				continue;
			} else {
				System.out.printf("Error found in file \"london.txt\", keyword=\"%s\".\n", key);
				System.exit(2);
			}
		}

		in.close();

		return g;
	}

	static public Vertex<Station> findStation(Graph<Station, LineSegment> g, String name) {
		for (Vertex<Station> v : g.vertices()) {
			if (v.getElement().getName().equals(name))
				return v;
		}
		return null;
	}
}