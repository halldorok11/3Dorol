package mazepack;

public class Maze {

	//member variables
	private WeightedGraph graph;
	private PrimMST mst;
	public Maze(int v) {

		graph = new WeightedGraph(v);
		graph.linkMatrix();
		mst = new PrimMST(graph);

	}

	public Iterable<Edge> getEdges() {
		return mst.edges();
	}
}
