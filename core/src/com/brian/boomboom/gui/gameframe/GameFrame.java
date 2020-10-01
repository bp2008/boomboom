package com.brian.boomboom.gui.gameframe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.BoomBoomGame;
import com.brian.boomboom.Globals;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.world.WorldMap;

public class GameFrame
{
	int[] playerHealthBars;
	Color[] playerHealthColors;
	private static final int healthBarBorderWidth = 4;
	private static final int healthBarOwnershipWidth = 480; // (1920 / 4) == one quarter of screen width;
	private static final int healthBarInnerHeight = 16;
	private static final int healthBarOuterHeight = healthBarInnerHeight + healthBarBorderWidth * 2; // inner height + top and bottom borders
	private static final int healthBarInnerWidth = 200;
	private static final int healthBarOuterWidth = healthBarInnerWidth + healthBarBorderWidth * 2; // inner width + left and right borders
	private static final int healthScalingFactor = 2; // The number that must be multiplied by the health value to get
														// the health bar width to draw.

	public GameFrame()
	{
		playerHealthBars = new int[4];
		playerHealthColors = new Color[4];
		for (int i = 0; i < playerHealthBars.length; i++)
		{
			playerHealthBars[i] = -1;
			playerHealthColors[i] = Color.WHITE;
		}
	}

	public void Update(WorldMap worldMap)
	{
		for (int i = 0; i < playerHealthBars.length; i++)
		{
			if (i < worldMap.players.length)
			{
				playerHealthBars[i] = worldMap.players[i].health;
				playerHealthColors[i] = worldMap.players[i].healthColor;
			}
			else
			{
				playerHealthBars[i] = -1;
				playerHealthColors[i] = Color.WHITE;
			}
		}
	}

	public void Draw(SpriteBatch batch)
	{
		// Draw top bar (shades of purple)
		batch.setColor(99 / 255f, 101 / 255f, 165 / 255f, 1);
		batch.draw(Globals.pixelWhiteTexture, 0, 1080 - (BoomBoomGame.fMenuHeightPx - 8), 0, 0, 1, 1, 1920,
				BoomBoomGame.fMenuHeightPx - 16, 0);
		batch.setColor(88 / 255f, 90 / 255f, 154 / 255f, 1);
		batch.draw(Globals.pixelWhiteTexture, 0, 1080 - (BoomBoomGame.fMenuHeightPx - 4), 0, 0, 1, 1, 1920, 4, 0);
		batch.draw(Globals.pixelWhiteTexture, 0, 1080 - 8, 0, 0, 1, 1, 1920, 4, 0);
		batch.setColor(66 / 255f, 70 / 255f, 132 / 255f, 1);
		batch.draw(Globals.pixelWhiteTexture, 0, 1080 - BoomBoomGame.fMenuHeightPx, 0, 0, 1, 1, 1920, 4, 0);
		batch.draw(Globals.pixelWhiteTexture, 0, 1080 - 4, 0, 0, 1, 1, 1920, 4, 0);

		// Draw player health bars.

		// Calculate health bar position (centered vertically and horizontally in its portion of the game frame)
		float healthBarX = (healthBarOwnershipWidth - healthBarOuterWidth) / 2;
		float healthBarY = 1080 - ((BoomBoomGame.fMenuHeightPx - 8) - ((BoomBoomGame.fMenuHeightPx - 16 - healthBarOuterHeight) / 2));

		for (int i = 0; i < playerHealthBars.length; i++)
		{
			if (playerHealthBars[i] < 0)
				continue;
			// // draw(Texture, x, y, origin x, origin y, width, height, scale x, scale y, r)
			// Draw outer border of health bar
			
			// Draw solid color border
//			batch.setColor(55 / 255f, 60 / 255f, 121 / 255f, 1);
//			batch.draw(Globals.pixelWhiteTexture, (healthBarOwnershipWidth * i) + healthBarX, healthBarY, 0, 0, 1, 1,
//					healthBarOuterWidth, healthBarOuterHeight, 0);
			
			// Draw "3d-ish" border
			// Draw Top
			batch.setColor(66 / 255f, 70 / 255f, 132 / 255f, 1);
			batch.draw(Globals.pixelWhiteTexture, (healthBarOwnershipWidth * i) + healthBarX, healthBarY + (healthBarOuterHeight - healthBarBorderWidth), 0, 0, 1, 1,
			healthBarOuterWidth - healthBarBorderWidth, healthBarBorderWidth, 0);
			// Draw Right
			batch.setColor(44 / 255f, 50 / 255f, 110 / 255f, 1);
			batch.draw(Globals.pixelWhiteTexture, (healthBarOwnershipWidth * i) + healthBarX + (healthBarOuterWidth - healthBarBorderWidth), healthBarY + healthBarBorderWidth, 0, 0, 1, 1,
			healthBarBorderWidth, healthBarOuterHeight - healthBarBorderWidth, 0);
			// Draw Bottom
			batch.setColor(150 / 255f, 155 / 255f, 210 / 255f, 1);
			batch.draw(Globals.pixelWhiteTexture, (healthBarOwnershipWidth * i) + healthBarX + healthBarBorderWidth, healthBarY, 0, 0, 1, 1,
			healthBarOuterWidth - healthBarBorderWidth, healthBarBorderWidth, 0);
//			// Draw inner health bar background - only needed if drawing border in one draw call
//			batch.setColor(66 / 255f, 70 / 255f, 132 / 255f, 1);
//			batch.draw(Globals.pixelWhiteTexture, (healthBarOwnershipWidth * i) + healthBarX + healthBarBorderWidth,
//					healthBarY + healthBarBorderWidth, 0, 0, 1, 1, healthBarInnerWidth, healthBarInnerHeight, 0);
			// Draw player's actual health meter
			batch.setColor(playerHealthColors[i]);
			batch.draw(Globals.pixelWhiteTexture, (healthBarOwnershipWidth * i) + healthBarX + healthBarBorderWidth,
					healthBarY + healthBarBorderWidth, 0, 0, 1, 1, playerHealthBars[i] * healthScalingFactor,
					healthBarInnerHeight, 0);
			
			if(Settings.showScoresByHealth)
			{
				// Draw player's session score:
				Globals.fontAndy.setColor(playerHealthColors[i]);
				Globals.fontAndy.draw(batch, String.valueOf(Settings.getPlayerSessionScore(i)), (healthBarOwnershipWidth * i) + 6, 1068);
				Globals.fontAndy.setColor(Color.WHITE);
			}
		}
		if(Settings.showScoresByHealth)
		{
			Globals.fontAndy.setColor(0.25f, 0.25f, 0.25f, 1);
			Globals.fontAndy.draw(batch, String.valueOf(Settings.getPlayerSessionScore(-1)), (healthBarOwnershipWidth * 4) - 120, 1068);
			Globals.fontAndy.setColor(Color.WHITE);
		}
		batch.setColor(Color.WHITE);
	}
}
