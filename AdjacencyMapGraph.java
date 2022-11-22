public class AdjacencyMapGraph<V,E> implements Graph<V,E> {
  private boolean isDirected;
  private PositionalList<Vertex<V>> vertices = new LinkedPositionalList<>();
  private PositionalList<Edge<E>> edges = new LinkedPositionalList<>();

  public AdjacencyMapGraph(boolean directed) { isDirected = directed; }

  public int numVertices() { return vertices.size(); }

  public Iterable<Vertex<V>> vertices() { return vertices; }

  public int numEdges() { return edges.size(); }

  public Iterable<Edge<E>> edges() { return edges; }

  public int outDegree(Vertex<V> v) throws IllegalArgumentException {
    InnerVertex<V> vert = validate(v);
    return vert.getOutgoing().size();
  }

  public Iterable<Edge<E>> outgoingEdges(Vertex<V> v) throws IllegalArgumentException {
    InnerVertex<V> vert = validate(v);
    return vert.getOutgoing().values();   // edges are the values in the adjacency map
  }

  public int inDegree(Vertex<V> v) throws IllegalArgumentException {
    InnerVertex<V> vert = validate(v);
    return vert.getIncoming().size();
  }

  public Iterable<Edge<E>> incomingEdges(Vertex<V> v) throws IllegalArgumentException {
    InnerVertex<V> vert = validate(v);
    return vert.getIncoming().values();   // edges are the values in the adjacency map
  }

  public Edge<E> getEdge(Vertex<V> u, Vertex<V> v) throws IllegalArgumentException {
    InnerVertex<V> origin = validate(u);
    return origin.getOutgoing().get(v);    // will be null if no edge from u to v
  }

  public Vertex<V>[] endVertices(Edge<E> e) throws IllegalArgumentException {
    InnerEdge<E> edge = validate(e);
    return edge.getEndpoints();
  }

  public Vertex<V> opposite(Vertex<V> v, Edge<E> e) throws IllegalArgumentException {
    InnerEdge<E> edge = validate(e);
    Vertex<V>[] endpoints = edge.getEndpoints();
    if (endpoints[0] == v)
      return endpoints[1];
    else if (endpoints[1] == v)
      return endpoints[0];
    else
      throw new IllegalArgumentException("v is not incident to this edge");
  }

  public Vertex<V> insertVertex(V element) {
    InnerVertex<V> v = new InnerVertex<>(element, isDirected);
    v.setPosition(vertices.addLast(v));
    return v;
  }

  public Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E element) throws IllegalArgumentException {
//    if (getEdge(u,v) == null) {
      InnerEdge<E> e = new InnerEdge<>(u, v, element);
      e.setPosition(edges.addLast(e));
      InnerVertex<V> origin = validate(u);
      InnerVertex<V> dest = validate(v);
      origin.getOutgoing().put(v, e);
      dest.getIncoming().put(u, e);      
      return e;
 //   } else
 //     throw new IllegalArgumentException("Edge from u to v exists");
  }

  public void removeVertex(Vertex<V> v) throws IllegalArgumentException {
    InnerVertex<V> vert = validate(v);
    // remove all incident edges from the graph
    for (Edge<E> e : vert.getOutgoing().values())
      removeEdge(e);
    for (Edge<E> e : vert.getIncoming().values())
      removeEdge(e);
    // remove this vertex from the list of vertices
    vertices.remove(vert.getPosition());
    vert.setPosition(null);             // invalidates the vertex
  }

  @SuppressWarnings({"unchecked"})
  public void removeEdge(Edge<E> e) throws IllegalArgumentException {
    InnerEdge<E> edge = validate(e);
    // remove this edge from vertices' adjacencies
    InnerVertex<V>[] verts = (InnerVertex<V>[]) edge.getEndpoints();
    verts[0].getOutgoing().remove(verts[1]);
    verts[1].getIncoming().remove(verts[0]);
    // remove this edge from the list of edges
    edges.remove(edge.getPosition());
    edge.setPosition(null);             // invalidates the edge
  }

  @SuppressWarnings({"unchecked"})
  private InnerVertex<V> validate(Vertex<V> v) {
    if (!(v instanceof InnerVertex)) throw new IllegalArgumentException("Invalid vertex");
    InnerVertex<V> vert = (InnerVertex<V>) v;     // safe cast
    if (!vert.validate(this)) throw new IllegalArgumentException("Invalid vertex");
    return vert;
  }

  @SuppressWarnings({"unchecked"})
  private InnerEdge<E> validate(Edge<E> e) {
    if (!(e instanceof InnerEdge)) throw new IllegalArgumentException("Invalid edge");
    InnerEdge<E> edge = (InnerEdge<E>) e;     // safe cast
    if (!edge.validate(this)) throw new IllegalArgumentException("Invalid edge");
    return edge;
  }

  private class InnerVertex<V> implements Vertex<V> {
    private V element;
    private Position<Vertex<V>> pos;
    private Map<Vertex<V>, Edge<E>> outgoing, incoming;

    public InnerVertex(V elem, boolean graphIsDirected) {
      element = elem;
      outgoing = new ProbeHashMap<>();
      if (graphIsDirected)
        incoming = new ProbeHashMap<>();
      else
        incoming = outgoing;    // if undirected, alias outgoing map
    }

    public boolean validate(Graph<V,E> graph) {
      return (AdjacencyMapGraph.this == graph && pos != null);
    }

    public V getElement() { return element; }

    public void setPosition(Position<Vertex<V>> p) { pos = p; }

    public Position<Vertex<V>> getPosition() { return pos; }

    public Map<Vertex<V>, Edge<E>> getOutgoing() { return outgoing; }

    public Map<Vertex<V>, Edge<E>> getIncoming() { return incoming; }
    
    public String toString() { return element.toString(); }
  }

  private class InnerEdge<E> implements Edge<E> {
    private E element;
    private Position<Edge<E>> pos;
    private Vertex<V>[] endpoints;

    @SuppressWarnings({"unchecked"})
    public InnerEdge(Vertex<V> u, Vertex<V> v, E elem) {
      element = elem;
      endpoints = (Vertex<V>[]) new Vertex[]{u,v};  // array of length 2
    }

    public E getElement() { return element; }

    public Vertex<V>[] getEndpoints() { return endpoints; }

    public boolean validate(Graph<V,E> graph) {
      return AdjacencyMapGraph.this == graph && pos != null;
    }

    public void setPosition(Position<Edge<E>> p) { pos = p; }

    public Position<Edge<E>> getPosition() { return pos; }

    public String toString() { return element.toString(); }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
//     sb.append("Edges:");
//     for (Edge<E> e : edges) {
//       Vertex<V>[] verts = endVertices(e);
//       sb.append(String.format(" (%s->%s, %s)", verts[0].getElement(), verts[1].getElement(), e.getElement()));
//     }
//     sb.append("\n");
    for (Vertex<V> v : vertices) {
      sb.append("Vertex " + v.getElement() + "\n");
      if (isDirected)
        sb.append(" [outgoing]");
      sb.append(" " + outDegree(v) + " adjacencies:");
      for (Edge<E> e: outgoingEdges(v))
        sb.append(String.format(" (%s, %s)", opposite(v,e).getElement(), e.getElement()));
      sb.append("\n");
      if (isDirected) {
        sb.append(" [incoming]");
        sb.append(" " + inDegree(v) + " adjacencies:");
        for (Edge<E> e: incomingEdges(v))
          sb.append(String.format(" (%s, %s)", opposite(v,e).getElement(), e.getElement()));
        sb.append("\n");
      }
    }
    return sb.toString();
  }
}