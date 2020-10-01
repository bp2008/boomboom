package com.brian.boomboom.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.brian.boomboom.util.ColorHelper;

public class LogoActor extends Actor
{
	TextureRegion logoTextureRegion;
	float hue = 0f;
	com.badlogic.gdx.graphics.Color colorGDX;

	public LogoActor(Texture logoTexture)
	{
		this.logoTextureRegion = new TextureRegion(logoTexture);
		colorGDX = ColorHelper.FromHSV(hue, 1f, 1f);
	}
	@Override
	public void act(float delta)
	{
		hue += delta;
		if(hue > 1)
			hue -= (int)hue;
		colorGDX = ColorHelper.FromHSV(hue, 1f, 1f);
	}
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(colorGDX);
		batch.draw(logoTextureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
}
