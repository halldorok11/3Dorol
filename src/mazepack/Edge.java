package mazepack;

public class Edge implements Comparable<Edge> {

	private final int v, w;
	private final double weight;

	public Edge(int v, int w, double weight)
	{
		this.v = v;
		this.w = w;
		this.weight = weight;
	}

	public int either()
	{
		return v;
	}

	public int other(int vertex)
	{
		if(vertex == v)
			return w;
		return v;
	}

	public double weight()
	{
		return weight;
	}
	@Override
	public int compareTo(Edge o)
	{
		if(this.weight() < o.weight())
			return -1;
		else if(this.weight() > o.weight())
			return 1;

		return 0;
	}
}
