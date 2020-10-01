package com.brian.boomboom.settings;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.brian.boomboom.util.BBLog;
import com.brian.boomboom.util.string;
import com.brian.boomboom.world.WorldMap;

public class Settings
{
	public static final int numWorlds = 7;
	public static final int maxPlayers = 4;
	public static final int mainMenuLevel = 6;
	public static int numPlayers = 2;
	public static int worldWidth = 21; // Default: 21
	public static int worldHeight = 11; // Default: 11
	public static int worldIndex = -1;
	public static int angelOfDeathEverywhereState = 1;
	public static boolean canPlaceBombInFire = false;
	public static int blockRocketPathfindingCost = 15;
	public static int rocketDamage = 20;
	public static int iMaxDamageAtOnce = 3;
	public static float soundVolume = 0.4f;
	public static float musicVolume = 0.4f;
	public static int iRunSpeedDefault = 1;
	public static int entityHealthDefault = 100;
	public static int iStartingBombCount = 1;
	public static boolean showScoresByHealth = true;

	public static int p1SessionScore = 0;
	public static int p2SessionScore = 0;
	public static int p3SessionScore = 0;
	public static int p4SessionScore = 0;
	public static int tieSessionScore = 0;
	public static int p1Score = 0;
	public static int p2Score = 0;
	public static int p3Score = 0;
	public static int p4Score = 0;
	public static int tieScore = 0;
	public static int zoom = 0;
	
	public static float GetZoom()
	{
		return 1f + (0.005f * zoom);
	}

	public static void Load()
	{
		try
		{
			FileHandle file = Gdx.files.local("settings.txt");
			if (!file.exists())
				return;

			String text = file.readString();
			if (text == null)
				return;

			String[] parts = text.split("\n");
			if (parts.length < 9)
				return;

			p1Score = Integer.parseInt(parts[0]);
			p2Score = Integer.parseInt(parts[1]);
			p3Score = Integer.parseInt(parts[2]);
			p4Score = Integer.parseInt(parts[3]);
			tieScore = Integer.parseInt(parts[4]);
			worldIndex = Integer.parseInt(parts[5]);
			SetFastStart(Integer.parseInt(parts[6]) == 1);
			zoom = Integer.parseInt(parts[7]);
			showScoresByHealth = Integer.parseInt(parts[8]) == 1;
			if (parts.length < 11)
				return;
			worldWidth = Integer.parseInt(parts[9]);
			if(worldWidth % 2 == 0)
				worldWidth += 1;
			worldHeight = Integer.parseInt(parts[10]);
			if(worldHeight % 2 == 0)
				worldHeight += 1;
		}
		catch (Exception ex)
		{
			BBLog.debug(ex, Settings.class);
		}
	}

	public static void Save()
	{
		try
		{
			ArrayList<String> strings = new ArrayList<String>();
			strings.add(String.valueOf(p1Score));
			strings.add(String.valueOf(p2Score));
			strings.add(String.valueOf(p3Score));
			strings.add(String.valueOf(p4Score));
			strings.add(String.valueOf(tieScore));
			strings.add(String.valueOf(worldIndex));
			strings.add(String.valueOf(IsFastStart() ? "1" : "0"));
			strings.add(String.valueOf(zoom));
			strings.add(String.valueOf(showScoresByHealth ? "1" : "0"));
			strings.add(String.valueOf(worldWidth));
			strings.add(String.valueOf(worldHeight));

			FileHandle file = Gdx.files.local("settings.txt");
			file.writeString(string.JoinStringArrayList("\n", strings), false);
		}
		catch (Exception ex)
		{
			BBLog.debug(ex, Settings.class);
		}
	}

	public static String GetLevelSelectionString()
	{
		if (worldIndex < -1 || worldIndex >= numWorlds)
			worldIndex = -1;
		switch (worldIndex)
		{
			case -1:
				return "Random";
			case 0:
				return "Forest";
			case 1:
				return "Ancient Desert";
			case 2:
				return "Wasteland";
			case 3:
				return "Temple";
			case 4:
				return "Retro";
			case 5:
				return "Snow";
			case 6:
				return "BoomBoom";
		}
		return "Error";
	}

	public static boolean IsFastStart()
	{
		if (iRunSpeedDefault == 5)
			return true;
		return false;
	}

	public static void SetFastStart(boolean on)
	{
		if (on)
		{
			iRunSpeedDefault = 5;
			iStartingBombCount = 2;
			entityHealthDefault = 1;
		}
		else
		{
			iRunSpeedDefault = 1;
			iStartingBombCount = 1;
			entityHealthDefault = 100;
		}
	}

	public static void RecordWin(WorldMap worldMap)
	{
		if (worldMap.numPlayersAlive == 0)
		{
			// Nobody won; it is a tie
			Settings.tieSessionScore++;
			Settings.tieScore++;
		}
		else
		{
			for (int i = 0; i < worldMap.players.length; i++)
				if (worldMap.players[i].health > 0)
				{
					// This player won
					int player = i + 1;
					switch (player)
					{
						case 1:
							Settings.p1SessionScore++;
							Settings.p1Score++;
							break;
						case 2:
							Settings.p2SessionScore++;
							Settings.p2Score++;
							break;
						case 3:
							Settings.p3SessionScore++;
							Settings.p3Score++;
							break;
						case 4:
							Settings.p4SessionScore++;
							Settings.p4Score++;
							break;
						default:
							break;
					}
				}
		}
		Settings.Save();
	}

	public static int getPlayerSessionScore(int index)
	{
		if(index == 0)
			return Settings.p1SessionScore;
		if(index == 1)
			return Settings.p2SessionScore;
		if(index == 2)
			return Settings.p3SessionScore;
		if(index == 3)
			return Settings.p4SessionScore;
		return Settings.tieSessionScore;
	}
}
