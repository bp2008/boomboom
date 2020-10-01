package com.brian.boomboom.rocket;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.Globals;
import com.brian.boomboom.entity.Entity;
import com.brian.boomboom.input.Direction;
import com.brian.boomboom.pathfinder.Path;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.WorldMap;

public class Rocket
{
	public Direction direction;
	public Point position;
	public Entity owner;
	public Entity target;
	private double movementDelay;
	private double timeLastMoved = -1000;
	private int frame = 0;
	
	public Rocket(Entity owner, Entity target)
	{
		Recycle(owner, target);
	}

	public void Recycle(Entity owner, Entity target)
	{		
		this.owner = owner;
		this.movementDelay = owner.movementDelay * 2;
		this.timeLastMoved = -1000;
		this.target = target;
		this.position = owner.position.copy();
	}
	public boolean Update(WorldMap world)
	{
		if (GameTime.getGameTime() >= timeLastMoved + movementDelay)
			return PositionMe(world);
		else
			return world.RocketMoved(this);
	}
	public void Draw(SpriteBatch batch)
	{
		batch.draw(Globals.rocketTexture[frame], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
	}
	private boolean PositionMe(WorldMap world)
	{
		timeLastMoved = GameTime.getGameTime();

		ArrayList<Point> points = Path.PathFind(world, position, target.position);
		if (points.size() == 0)
			return true;
		else if (points.size() == 1)
		{
			direction = Direction.None;
			world.ExplodeRocket(this);
			return true;
		}
		else if (this.position.x < points.get(1).x)
		{
			direction = Direction.Right;
			frame = 3;
		}
		else if (this.position.x > points.get(1).x)
		{
			direction = Direction.Left;
			frame = 2;
		}
		else if (this.position.y < points.get(1).y)
		{
			direction = Direction.Up;
			frame = 0;
		}
		else if (this.position.y > points.get(1).y)
		{
			direction = Direction.Down;
			frame = 1;
		}
		else
		{
			direction = Direction.None;
			world.ExplodeRocket(this);
			return true;
		}

		if (direction != Direction.None)
		{
			this.position.x = points.get(1).x;
			this.position.y = points.get(1).y;
			return world.RocketMoved(this);
		}
		return false;
	}
}
