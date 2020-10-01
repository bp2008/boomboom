package com.brian.boomboom.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.brian.boomboom.Globals;
import com.brian.boomboom.util.GameTime;

public class Earthquake
{
	private WorldMap game;
	private Vector3 sourceCameraPosition;
	private long referenceTime = -10000;
	private long timeToState2 = 1000;
	private long timeToState3 = 1000;
	public boolean deleteMe;
	private int myState = 0; // 1 and 2 are both shaking states. at the transition from 1 to 2, the blocks in the world
								// change.

	public Earthquake(WorldMap game)
	{
		this.game = game;
		referenceTime = GameTime.getGameTime();
	}

	public void Update(OrthographicCamera camera)
	{
		if (GameTime.isPaused())
			return;
		long timeNow = GameTime.getGameTime();
		float amount;
		switch (myState)
		{
			case 0: // Initialize
				sourceCameraPosition = camera.position.cpy();
				myState = 1;
				Update(camera);
				break;
			case 1: // Accelerate Shaking
				amount = (timeNow - referenceTime) / (float) timeToState2;
				camera.position.set(sourceCameraPosition);
				camera.translate((0.5f - Globals.rndGenerator.nextFloat()) * amount,
						(0.5f - Globals.rndGenerator.nextFloat()) * amount);
				camera.update();
				if (amount >= 1)
				{
					myState = 2;
					referenceTime = timeNow;
					game.EarthquakeRandomize();
					return;
				}
				break;
			case 2: // Decelerate Shaking
				amount = (timeNow - referenceTime) / (float) timeToState3;
				if (amount >= 1)
				{
					myState = 3;
					Update(camera);
					return;
				}
				else
				{
					amount = 1 - amount;
					camera.position.set(sourceCameraPosition);
					camera.translate((0.5f - Globals.rndGenerator.nextFloat()) * amount,
							(0.5f - Globals.rndGenerator.nextFloat()) * amount);
					camera.update();
				}
				break;
			case 3:
				EndQuake(camera);
				break;
		}
	}

	public void EndQuake(OrthographicCamera camera)
	{
		camera.position.set(sourceCameraPosition);
		camera.update();
		deleteMe = true;
		myState = 3;
	}
}
