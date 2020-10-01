package com.brian.boomboom.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.brian.boomboom.BoomBoomGame;
import com.brian.boomboom.Globals;
import com.brian.boomboom.gui.LightActor;

public class MenuWorld
{
	Stage stageBg;
	Stage stageFg;
	
	public MenuWorld()
	{
		// Set up background stage
		ScalingViewport viewport = new ScalingViewport(Scaling.stretch, 1920, 1080);
		stageBg = new Stage(viewport);
		
		TextureRegionDrawable trd = new TextureRegionDrawable(Globals.menuBackgroundSprite);
		Table tbl = new Table(BoomBoomGame.gui.skin);
		tbl.setBackground(trd);
		tbl.setFillParent(true);
		
		stageBg.addActor(tbl);

		// Set up foreground stage
		stageFg = new Stage(viewport);

		stageFg.addActor(new LightActor());
		stageFg.addActor(new LightActor());
		stageFg.addActor(new LightActor());
		stageFg.addActor(new LightActor());
		stageFg.addActor(new LightActor());
	}

	public void dispose()
	{
		stageBg.dispose();
		stageFg.dispose();
	}
	public void DrawBackground()
	{
		float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
		stageBg.act(delta);
		stageBg.draw();
	}
	public void DrawForeground()
	{
		float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
		stageFg.act(delta);
		stageFg.draw();
	}
}
