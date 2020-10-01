package com.brian.boomboom.pathfinder;

import java.util.ArrayList;

import com.brian.boomboom.util.BBLog;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.WorldMap;

public class Path
{
	private static PathFinderFast mPathFinder = null;

	public static ArrayList<Point> PathFind(WorldMap world, Point a, Point b)
	{
		ArrayList<Point> points;
		if (mPathFinder == null)
		{
			try
			{
				mPathFinder = new PathFinderFast(world.worldMap);
			}
			catch (Exception e)
			{
				BBLog.debug(e, Path.class);
				// No path, return just the current point
				points = new ArrayList<Point>(1);
				points.add(a.copy());
				return points;
			}
			mPathFinder.mFormula = HeuristicFormula.MaxDXDY;
			mPathFinder.setDiagonals(false);
			mPathFinder.mHEstimate = 1;
			mPathFinder.mPunishChangeDirection = false;
			mPathFinder.mTieBreaker = true;
			mPathFinder.mSearchLimit = 50000;
		}
		mPathFinder.SetGrid(world.worldMap);
		ArrayList<PathFinderNode> nodes = mPathFinder.FindPath(a.copy(), b.copy());
		if (nodes == null)
		{
			// No path, return just the current point
			points = new ArrayList<Point>(1);
			points.add(a.copy());
			return points;
		}
		points = new ArrayList<Point>(nodes.size());
		for (int i = nodes.size() - 1; i >= 0; i--)
		{
			points.add(new Point(nodes.get(i).X, nodes.get(i).Y));
		}
		// foreach (Point p in points)
		// {
		// System.Windows.Forms.MessageBox.Show(p.ToString());
		// }
		return points;
	}
}
