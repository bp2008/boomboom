package com.brian.boomboom.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.brian.boomboom.BoomBoomGame;

public class DesktopLauncher {
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BoomBoom";
		cfg.width = 1920;
		cfg.height = 1080;

		new LwjglApplication(new BoomBoomGame(new DesktopBrowserOpener()), cfg);
	}
}
