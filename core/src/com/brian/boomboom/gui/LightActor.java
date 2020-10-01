package com.brian.boomboom.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.brian.boomboom.Globals;
import com.brian.boomboom.util.ColorHelper;
import com.brian.boomboom.util.GameTime;

public class LightActor extends Actor
{
	TextureRegion logoTextureRegion;
	float hue = 0f;
	com.badlogic.gdx.graphics.Color colorGDX;

	float acceleration = 0.1f;
	float speedModifier = 0.1f;
	float dx = 0f;
	float dy = 0f;
	float startX = 0f;
	float startY = 0f;
	float targetX = 0f;
	float targetY = 0f;
	int timeToReachTarget = 1000;
	int halfTimeToReachTarget = 500;
	int timeSpentMoving = 0;
	int timeRemainingToReachTarget = 1000;
	long lastGameTime = 0;

	int size = 512;
	int halfSize = size / 2;

	public LightActor()
	{
		this.logoTextureRegion = new TextureRegion(Globals.light2Texture);

		setX(Globals.nextInt(0 - size, 1921));
		setY(Globals.nextInt(0 - size, 1081));

		startX = getX();
		startY = getY();

		targetX = Globals.nextInt(0 - size, 1921);
		targetY = Globals.nextInt(0 - size, 1081);

		dx = targetX - startX;
		dy = targetY - startY;

		timeToReachTarget = (int) (Math.sqrt(Math.pow(Math.abs(dx), 2) + Math.pow(Math.abs(dy), 2)) * 2);
		halfTimeToReachTarget = timeToReachTarget / 2;
		timeSpentMoving = 0;
		timeRemainingToReachTarget = timeToReachTarget;
		lastGameTime = GameTime.getGameTime();

		hue = Globals.nextInt(0, 256) / 255f;

		colorGDX = ColorHelper.FromHSV(hue, 1f, 1f);

		this.setWidth(size);
		this.setHeight(size);
	}

	@Override
	public void act(float delta)
	{
		timeSpentMoving += GameTime.getGameTime() - lastGameTime;
		lastGameTime = GameTime.getGameTime();

		hue += delta / 8f;
		if (hue > 1)
			hue -= (int) hue;
		colorGDX = ColorHelper.FromHSV(hue, 1f, 1f);

		// dx = (targetX - startX) * acceleration * speedModifier;
		// dy = (targetY - startY) * acceleration * speedModifier;

		timeRemainingToReachTarget = timeToReachTarget - timeSpentMoving;

		if (timeRemainingToReachTarget < 0 || timeRemainingToReachTarget > timeToReachTarget)
		{
			startX = getX();
			startY = getY();

			targetX = Globals.nextInt(-size, 1921);
			targetY = Globals.nextInt(-size, 1081);

			dx = targetX - startX;
			dy = targetY - startY;

			timeToReachTarget = (int) (Math.sqrt(Math.pow(Math.abs(dx), 2) + Math.pow(Math.abs(dy), 2)) * 2);
			if (timeToReachTarget < 20)
				timeToReachTarget = 20;
			halfTimeToReachTarget = timeToReachTarget / 2;
			timeSpentMoving = 0;
			timeRemainingToReachTarget = timeToReachTarget;
		}
		else
		{
			setX(easeInOutQuad(timeSpentMoving, startX, dx, timeToReachTarget));
			setY(easeInOutQuad(timeSpentMoving, startY, dy, timeToReachTarget));
		}
	}

	private float easeInOutQuad(float t, float b, float c, float d)
	{
		if ((t /= d / 2) < 1)
			return c / 2 * t * t + b;
		return -c / 2 * ((--t) * (t - 2) - 1) + b;
	}

//	private float easeInOutQuart(float t, float b, float c, float d)
//	{
//		if ((t /= d / 2) < 1)
//			return c / 2 * t * t * t * t + b;
//		return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
//	}

	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(colorGDX);
		batch.draw(logoTextureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(),
				getScaleY(), getRotation());
	}
}
