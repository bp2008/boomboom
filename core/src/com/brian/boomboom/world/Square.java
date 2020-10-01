package com.brian.boomboom.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.Globals;
import com.brian.boomboom.bomb.Bomb;
import com.brian.boomboom.item.Item;
import com.brian.boomboom.item.ItemType;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;

public class Square
{
	public SquareTypes squareType = SquareTypes.Empty;
	public Point position;
	public int locY;
	public int worldStandardIndex = 0;
	/**
	 * A reference to the bomb on this square (if one is on this square) so we do not have to loop through all bombs to
	 * find one on this square.
	 */
	public Bomb bomb;
	public Item item = new Item(ItemType.None);
	public int fireCount = 0;
	public int myTextureOffset = 0;
	public boolean hadFireLastUpdate = false;
	public boolean hasBananaPeel = false;

	public int PathFindingValue()
	{
		if (squareType == SquareTypes.Empty)
			return 1;
		else if (squareType == SquareTypes.Impenetrable)
			return 0;
		else if (squareType == SquareTypes.Hammered || squareType == SquareTypes.WorldStandard)
			return Settings.blockRocketPathfindingCost;
		return 0;
	}

	public Square(int x, int y)
	{
		position = new Point(x, y);
		worldStandardIndex = Globals.rndGenerator.nextInt(Globals.worldTexture.length - 6);
		myTextureOffset = Globals.rndGenerator.nextInt(3);
	}

	public void Draw(SpriteBatch batch, int worldIndex)
	{
		if (squareType == SquareTypes.Empty)
		{
			// if (worldIndex != 2)
			batch.draw(Globals.worldTexture[1], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
			if(hasBananaPeel)
				batch.draw(Globals.bananaPeelTexture, position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
		}
		else if (squareType == SquareTypes.Impenetrable)
		{
			// Impenetrable blocks have animations
			// Find out which frame we need to be on
			if (worldIndex == Levels.Desert)
				batch.draw(Globals.worldTexture[3 + GameTime.get3wAnimationFrame()], position.x, position.y, 0, 0, 1,
						1, 1, 1, 0);
			else if (worldIndex == Levels.Desert || worldIndex == Levels.Temple)
				batch.draw(Globals.worldTexture[3 + GameTime.get3wSlowAnimationFrame()], position.x, position.y, 0, 0,
						1, 1, 1, 1, 0);
			else if(worldIndex == Levels.Rocks || worldIndex == Levels.MainMenu)
				batch.draw(Globals.worldTexture[3 + myTextureOffset], position.x, position.y, 0, 0,
						1, 1, 1, 1, 0);
			else
				batch.draw(Globals.worldTexture[3 + GameTime.get2wAnimationFrame()], position.x, position.y, 0, 0, 1,
						1, 1, 1, 0);
		}
		else if (squareType == SquareTypes.WorldStandard)
			batch.draw(Globals.worldTexture[6 + worldStandardIndex], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
		else if (squareType == SquareTypes.Hammered)
			batch.draw(Globals.worldTexture[2], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
		item.Draw(position, batch);
	}

	public void Update()
	{
	}

	public boolean IsWalkable()
	{
		return squareType == SquareTypes.Empty && !HasBomb();
	}

	public boolean HasBomb()
	{
		return bomb != null;
	}
}
