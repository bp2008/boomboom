package com.brian.boomboom.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brian.boomboom.Globals;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.WorldMap;

public class AngelOfDeath
{
	public WorldMap game;
	private long referenceTime = -10000;
	private long timeToFront = 1500;
	private long timeToFire = 1000;
	private long timeToBack = 1000;
	private long timeToDespawn = 1500;
	public boolean deleteMe;
	public Point target;
	private Vector2 position;
	private Vector2 startPosition;
	private Vector2 firePosition;
	private Vector2 endPosition;
	private int myState = 0;
	public AngelOfDeath(WorldMap game, Point tgt)
	{
		this.game = game;
		referenceTime = GameTime.getGameTime();
		target = tgt.copy();
		position = new Vector2(target.x, -1);
		startPosition = new Vector2(target.x, -1);
		firePosition = new Vector2(target.x, target.y);
		endPosition = new Vector2(target.x, game.worldHeight + 1);
	}
	public void Update()
	{
		long timeNow = GameTime.getGameTime();
		float amount;
		switch (myState)
		{
			case 0: // Move forward
				amount = (timeNow - referenceTime) / (float)timeToFront;
				if (amount >= 1)
				{
					myState = 1;
					position.x = firePosition.x;
					position.y = firePosition.y;
					referenceTime += timeToFront + timeToFire;
					return;
				}
				position.x = startPosition.x;
				position.y = startPosition.y;
				position.lerp(firePosition, amount);
				break;
			case 1: // Wait to fire
				if (timeNow >= referenceTime)
				{
					game.AngelFire(this);
					myState = 2;
					referenceTime += timeToBack;
				}
				break;
			case 2: // Wait to move backward
				if (timeNow >= referenceTime)
				{
					myState = 3;
				}
				break;
			case 3: // Move backward
				amount = (timeNow - referenceTime) / (float)timeToDespawn;
				if (amount > 1)
				{
					deleteMe = true;
					position.x = endPosition.x;
					position.y = endPosition.y;
					myState = 4;
					return;
				}
				position.x = firePosition.x;
				position.y = firePosition.y;
				position.lerp(endPosition, amount);
				break;
		}
	}

	public void Draw(SpriteBatch batch)
	{
		batch.draw(Globals.angelTexture, position.x, position.y, 0.5f, 0.5f, 1, 1, 2,
				2, 0);
	}
}
