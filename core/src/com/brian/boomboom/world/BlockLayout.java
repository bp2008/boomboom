package com.brian.boomboom.world;

import com.brian.boomboom.Globals;
import com.brian.boomboom.util.Point;

public class BlockLayout
{
	WorldTypes worldType;
	int gameWidth, gameHeight;
	Point[] spawnPositions;

	public BlockLayout(int gameWidth, int gameHeight, WorldTypes worldType)
	{
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.worldType = worldType;
	}

	public Point[] Initialize(Square[][] worldMap, int numPlayers) throws Exception
	{
		spawnPositions = GetSpawnPositions(numPlayers);
		SetBarriers(worldMap);
		BlockFillRandom(worldMap, spawnPositions);
		return spawnPositions;
	}

	private Point[] GetStandardBarriers() throws Exception
	{
		if (gameWidth % 2 == 0 || gameHeight % 2 == 0 || gameWidth < 3 || gameHeight < 3)
			throw new Exception(
					"Game width and height must both be odd and >= 3 when creating Standard Barriers.  Requested width: "
							+ gameWidth + ".  Requested Height: " + gameHeight);
		Point[] pts = new Point[(gameWidth / 2) * (gameHeight / 2)];
		int k = 0;
		for (int i = 0; i < gameWidth; i++)
		{
			if (i % 2 == 1)
				for (int j = 0; j < gameHeight; j++)
				{
					if (j % 2 == 1)
						pts[k++] = new Point(i, j);
				}
		}
		return pts;
	}

	private void SetBarriers(Square[][] gameWorld) throws Exception
	{
		Point[] pts;
		switch (worldType)
		{
			case Standard:
			default:
				pts = GetStandardBarriers();
				break;
		}
		for (int i = 0; i < pts.length; i++)
		{
			Point p = pts[i];
			gameWorld[p.x][p.y].squareType = SquareTypes.Impenetrable;
			// InitWorldLighting(p);
		}
	}

	private Point[] GetSpawnPositions(int numPlayers)
	{
		if (spawnPositions != null && spawnPositions.length == numPlayers)
			return spawnPositions;
		switch (worldType)
		{
			case Standard:
			default:
			{
				spawnPositions = new Point[numPlayers];
				if (numPlayers >= 1)
					spawnPositions[0] = new Point(0, gameHeight - 1);
				if (numPlayers >= 2)
					spawnPositions[1] = new Point(gameWidth - 1, 0);
				if (numPlayers >= 3)
					spawnPositions[2] = new Point(0, 0);
				if (numPlayers >= 4)
					spawnPositions[3] = new Point(gameWidth - 1, gameHeight - 1);
				return spawnPositions;
			}
		}
	}

	/**
	 * Fills Empty blocks with WorldStandard blocks, with a random chance to leave empty blocks empty. A small distance
	 * from each spawn position is guaranteed to be clear so the player is not forced to hurt himself.
	 * 
	 * @param gameWorld
	 *            The Square array that is the new game world.
	 * @param spawnPositions
	 *            An array of spawn positions.
	 */
	private void BlockFillRandom(Square[][] gameWorld, Point[] spawnPositions)
	{
		double blockChance = -0.1;
		int clearSpawnDist = 1;
		boolean guaranteeCenterIsBlocked = true;
		if (blockChance > 1)
			blockChance = 1;
		else if (blockChance < 0)
			blockChance = (70 + Globals.rndGenerator.nextInt(21)) / 100.0;
		if (clearSpawnDist < 1)
			clearSpawnDist = 1;
		int xl = gameWorld.length;
		int yl = gameWorld.length == 0 ? 0 : gameWorld[0].length;
		int xlHalf = xl / 2;
		int ylHalf = yl / 2;
		for (int i = 0; i < xl; i++)
		{
			for (int j = 0; j < yl; j++)
			{
				Square sq = gameWorld[i][j];
				if (sq.squareType == SquareTypes.Empty
						&& ((guaranteeCenterIsBlocked && (i == xlHalf || j == ylHalf)) || (Globals.rndGenerator
								.nextDouble() <= blockChance && !SpawnPointProximityViolation(spawnPositions,
								clearSpawnDist, i, j))))
					sq.squareType = SquareTypes.WorldStandard;
			}
		}
	}

	private static boolean SpawnPointProximityViolation(Point[] spawnPositions, int clearSpawnDist, int x, int y)
	{
		for (int i = 0; i < spawnPositions.length; i++)
		{
			if (spawnPositions[i].IntDistanceFrom(x, y) <= clearSpawnDist)
				return true;
		}
		return false;
	}
}
