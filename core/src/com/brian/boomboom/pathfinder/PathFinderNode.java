package com.brian.boomboom.pathfinder;

//[Author("Franco, Gustavo")]
public class PathFinderNode
{
    public int     F;
    public int     G;
    public int     H;  // f = gone + heuristic
    public int     X;
    public int     Y;
    public int     PX; // Parent
    public int     PY;
	public PathFinderNode copy()
	{
		PathFinderNode n = new PathFinderNode();
		n.F = F;
		n.G = G;
		n.H = H;
		n.X = X;
		n.Y = Y;
		n.PX = PX;
		n.PY = PY;
		return n;
	}
}
