package com.brian.boomboom.drawing;

import java.util.HashSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brian.boomboom.input.Direction;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Utilities;

public class CharTextureManager
{
	protected CharTextureDefinition[] Textures;
	public int numColorsNeeded;
	protected Color[][] ColorDefinitions;

	public CharTextureManager()
	{
		ColorDefinitions = new Color[4][];
		ColorDefinitions[0] = new Color[] { Utilities.Color(13, 13, 147), Utilities.Color(147, 13, 13),
				Utilities.Color(1, 100, 225), Color.GREEN, Color.RED };
		ColorDefinitions[1] = new Color[] { Color.BLACK, Color.GRAY, Utilities.Color(221, 0, 0), Color.GRAY, Color.GRAY };
		ColorDefinitions[2] = new Color[] { Utilities.Color(13, 103, 13), Utilities.Color(13, 177, 13),
				Utilities.Color(1, 215, 1), Color.GRAY, Color.GRAY };
		ColorDefinitions[3] = new Color[] { Utilities.Color(160, 30, 20), Utilities.Color(120, 20, 20), Color.ORANGE,
				Color.GRAY, Color.GRAY };
	}

	public void Load(TextureAtlas atlas, CharTextureDefinition[] charTextureDefinitions)
	{
		HashSet<Integer> colorIndexHashSet = new HashSet<Integer>();
		for (int texIndex = 0; texIndex < charTextureDefinitions.length; texIndex++)
		{
			CharTextureDefinition ctd = charTextureDefinitions[texIndex];

			if (ctd.colorReq == CharTextureColoringRequirements.NeedsColored)
				colorIndexHashSet.add(ctd.colorIndex);

			ctd.textures = atlas.findRegions(ctd.nameBase).toArray(TextureRegion.class);
		}
		numColorsNeeded = colorIndexHashSet.size();
		Textures = charTextureDefinitions;
	}

	public void Unload()
	{
		Textures = null;
		numColorsNeeded = 0;
	}

	public void Draw(SpriteBatch batch, int playerIndex, int animationFrameIndex, int x, int y, boolean diseased,
			boolean foundAngel, boolean hasLaser, boolean wearHat, boolean drawIcy, Direction fallenDownDrawDirection)
	{
		for (int i = 0; i < Textures.length; i++)
		{
			// Check conditional textures
			switch (Textures[i].type)
			{
				case AngelEyes:
					if (!foundAngel)
						continue;
					break;
				case LaserSpecs:
					if (!hasLaser)
						continue;
					break;
				case WinterHat:
					if (!wearHat)
						continue;
					break;
			}

			boolean drawDiseased = diseased && GameTime.getGameTime() % 400 > 200;

			if (Textures[i].colorReq == CharTextureColoringRequirements.NeedsColored)
			{
				batch.setColor(ColorDefinitions[playerIndex][Textures[i].colorIndex]);
			}
			else if (drawDiseased)
			{
				batch.setColor(Color.RED);
			}
			else if (drawIcy)
				batch.setColor(Color.BLUE);

			if(fallenDownDrawDirection == Direction.Left || fallenDownDrawDirection == Direction.Up)
				batch.draw(Textures[i].textures[10], x, y, 0.5f, 0.5f, 1, 1, 1, 1, -90);
			else if(fallenDownDrawDirection == Direction.Right || fallenDownDrawDirection == Direction.Down)
				batch.draw(Textures[i].textures[4], x, y, 0.5f, 0.5f, 1, 1, 1, 1, 90);
			else 
				batch.draw(Textures[i].textures[animationFrameIndex], x, y, 0, 0, 1, 1, 1, 1, 0);

			if (drawDiseased || drawIcy || Textures[i].colorReq == CharTextureColoringRequirements.NeedsColored)
				batch.setColor(Color.WHITE);
		}
	}
}
