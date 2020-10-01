package com.brian.boomboom.pathfinder;

import java.util.List;

import com.brian.boomboom.util.Point;

// [Author("Franco, Gustavo")]
public interface IPathFinder
{
	boolean Stopped = false;
	HeuristicFormula Formula = HeuristicFormula.Manhattan;
	boolean Diagonals = false;
	boolean HeavyDiagonals = false;
	int HeuristicEstimate = 0;
	boolean PunishChangeDirection = false;
	boolean TieBreaker = false;
	int SearchLimit = 0;
	double CompletedTime = 0;
	boolean DebugProgress = false;
	boolean DebugFoundPath = false;

	void FindPathStop();

	List<PathFinderNode> FindPath(Point start, Point end);

}
