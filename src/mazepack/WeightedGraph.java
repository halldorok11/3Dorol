package mazepack;

import java.util.Random;

public class WeightedGraph {

	private final int V;
	private final Bag<Edge>[] adj;

	public WeightedGraph(int V)  //creates a weighted graph with V vertices.
	{
		this.V = V;
		adj = new Bag[V];
		for(int v = 0; v < V; v++)
			adj[v] = new Bag<Edge>();


	}

	public int V() {return V;}

	public void addEdge(Edge e)
	{
		int v = e.either(), w = e.other(v);
		adj[v].add(e);
		adj[w].add(e);
	}

	public Iterable<Edge> adj(int v)
	{
		return adj[v];
	}

	//PRECONDITION: V must have an integer square root!
	public void linkMatrix()
	{
		//we think of our vertices as an NxN matrix
		//for each vertex we have, we add some edges to it

		int n = (int)Math.sqrt(V);

		// start with connecting horizontal vertices
		for(int i = 0; i < V - 1; i++)
		{
			if((i+1) % n != 0)
			{
				addEdge(new Edge(i, i+1, new Random().nextDouble()));
			}
		}

		//connect vertical vertices
		for(int i = 0; i < V - n; i++)
		{
			 addEdge(new Edge(i, n + i, new Random().nextDouble()));
		}
	}
}
