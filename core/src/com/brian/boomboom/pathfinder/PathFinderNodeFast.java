package com.brian.boomboom.pathfinder;

//[Author("Franco, Gustavo")]
//[StructLayout(LayoutKind.Sequential, Pack=1)] 
public class PathFinderNodeFast
{
	public int  F; // f = gone + heuristic
	public int  G;
	public int  PX; // Parent
	public int  PY;
	public byte Status;
}
