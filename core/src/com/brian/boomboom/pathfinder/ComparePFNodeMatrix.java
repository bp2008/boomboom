package com.brian.boomboom.pathfinder;


// [Author("Franco, Gustavo")]
public class ComparePFNodeMatrix implements IComparer<Integer>
{
	PathFinderNodeFast[] mMatrix;

	public ComparePFNodeMatrix(PathFinderNodeFast[] matrix)
	{
		mMatrix = matrix;
	}

	@Override
	public int Compare(Integer a, Integer b)
	{
		if (mMatrix[a].F > mMatrix[b].F)
			return 1;
		else if (mMatrix[a].F < mMatrix[b].F)
			return -1;
		return 0;
	}
}
