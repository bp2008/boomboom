package com.brian.boomboom;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.gui.BoomBoomGui;
import com.brian.boomboom.gui.gameframe.GameFrame;
import com.brian.boomboom.input.InputManager;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Utilities;
import com.brian.boomboom.world.MenuWorld;
import com.brian.boomboom.world.WorldMap;
import com.brian.boomboom.world.WorldTypes;

public class BoomBoomGame extends ApplicationAdapter
{
	public final static double menuHeightPx = 74.2857142857143d;
	public final static float fMenuHeightPx = 74.2857142857143f;
	private OrthographicCamera camera;
	private OrthographicCamera pixelPerfectCamera;
	private OrthographicCamera menuCamera;
	private SpriteBatch batch;

	public WorldMap worldMap;
	public GameFrame gameFrame;
	private MenuWorld menuWorld = null;

	public static BoomBoomGui gui;

	int fpsCounter = 0;
	String fps = "0";
	long nextFpsUpdate = Utilities.getTimeInMs();
	public static boolean is_on_ouya = Ouya.runningOnOuya;
	public BrowserOpener browserOpener;

	public BoomBoomGame(BrowserOpener browserOpener)
	{
		this.browserOpener = browserOpener;
	}

	@Override
	public void create()
	{
		GameTime.reset();
		Settings.Load();

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// The following calculations assume a 1920 x 1080 pixel screen and a default game world size of 21 x 11 blocks.
		// The menu goes above the game world.
		double menuHeightFractionOfScreen = menuHeightPx / 1080d; // The fraction of screen height taken up by the
																	// menu.
		double menuHeightScreenPixels = menuHeightFractionOfScreen * h; // The number of vertical pixels at the top of
																		// the screen allotted to the health bar menu.
		double gameScreenAvailableHeight = ((double) h - menuHeightScreenPixels); // The number of vertical pixels left
																					// over for the gameplay screen.

		double t1 = menuHeightScreenPixels / gameScreenAvailableHeight; // The number of menu heights that would fit in
																		// the gameplay area.
		double t2 = (t1 * Settings.worldHeight) + Settings.worldHeight; // The number of world blocks to use for the
																		// main camera height.

		// double screenAspectRatio = (double)w / gameScreenAvailableHeight;
		// double worldAspectRatio = (double)Settings.worldWidth / (double)Settings.worldHeight;

		// double aspectRatioDifference = screenAspectRatio - worldAspectRatio;

		// if(Math.abs(desiredAspectRatio - worldAspectRatio) > 0.01)
		// {
		// // world is too tall - we should draw it with black bars evenly split between both sides.
		// // Find out how much space there needs to be.
		// }

		// //////////////////////////////// //
		// Set up cameras. //
		// //////////////////////////////// //
		// The main "camera" is for the game world
		camera = new OrthographicCamera((float) Settings.worldWidth, (float) t2);
		camera.translate(camera.viewportWidth / 2, camera.viewportHeight / 2);

		// The camera now fills the screen.
		// We need to move it down to menu height
		// We allocate 74 pixels of a 1080p screen to the menu.
		camera.update();

		menuCamera = new OrthographicCamera(1920, 1080);
		menuCamera.translate(960, 540);
		menuCamera.update();

		pixelPerfectCamera = new OrthographicCamera(w, h);

		SetZoom();
		
		batch = new SpriteBatch();

		Globals.inputManager = new InputManager();
		// //////////////////////////////// //
		// Load Textures, Sounds, and Music //
		// //////////////////////////////// //
		Globals.loadContent();
		// Set up input processing
		Gdx.input.setInputProcessor(Globals.inputManager);
		// Load Game Frame (Health bars)
		gameFrame = new GameFrame();
		// Load GUI
		gui = new BoomBoomGui(this, menuCamera);
		// Load special menu world object
		menuWorld = new MenuWorld();
	}
	public void SetZoom()
	{
		camera.zoom = menuCamera.zoom = pixelPerfectCamera.zoom = Settings.GetZoom();
		camera.update();
		menuCamera.update();
		pixelPerfectCamera.update();
	}
	@Override
	public void dispose()
	{
		gui.dispose();
		batch.dispose();
		Gdx.input.setInputProcessor(null);
		Globals.inputManager.Shutdown();
		Globals.inputManager = null;
		Globals.unloadContent();
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update GameTime
		GameTime.tick();

		Globals.inputManager.Update();

		// // Draw Menu world background
		if (worldMap != null && worldMap.worldIndex == 6)
			menuWorld.DrawBackground();
		batch.begin();

		if (worldMap != null)
		{
			if(worldMap.activeEarthquake != null)
			{
				worldMap.activeEarthquake.Update(camera);
				if(worldMap.activeEarthquake.deleteMe)
					worldMap.activeEarthquake = null;
			}
			
			batch.setProjectionMatrix(camera.combined);
			worldMap.Update();

			gameFrame.Update(worldMap);

			worldMap.Draw(batch);
		}

		batch.setProjectionMatrix(menuCamera.combined);

		if (worldMap != null)
		{
			gameFrame.Draw(batch);
		}

		// Draw FPS
		fpsCounter++;
		if (nextFpsUpdate < GameTime.getRealTime())
		{
			fps = String.valueOf(fpsCounter);
			fpsCounter = 0;
			nextFpsUpdate = GameTime.getRealTime() + 1000;
		}
		
		batch.setProjectionMatrix(pixelPerfectCamera.combined);
		//Globals.fontAndy.draw(batch,fps, 2, pixelPerfectCamera.viewportHeight);

//		Globals.fontAndy.setColor(0, 0, 0, 1);
//		Globals.fontAndy.draw(batch, fps, 2, 1066);
//		Globals.fontAndy.setColor(Color.WHITE);

		//Globals.debugFont.draw(batch, inputManager.debug, 2, pixelPerfectCamera.viewportHeight - 30);

		batch.end();

		// Draw Menu world foreground
		if (worldMap != null && worldMap.worldIndex == 6)
			menuWorld.DrawForeground();

		gui.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		pixelPerfectCamera.viewportWidth = width;
		pixelPerfectCamera.viewportHeight = height;
		pixelPerfectCamera.position.set((float) width / 2, (float) height / 2, 0f);
		pixelPerfectCamera.zoom = Settings.GetZoom();
		pixelPerfectCamera.update();
	}

	@Override
	public void pause()
	{
		GameTime.pause();
		Globals.soundManager.PauseMusic();
		Globals.soundManager.StopAllSounds();
	}

	@Override
	public void resume()
	{
		Globals.soundManager.ResumeMusic();
		GameTime.unpause();
	}

	// //////////////////
	// /////////////////
	// ////////////////
	// ///////////////
	// "Menu Event Handlers"
	// \\\\\\\\\\\\\\\
	// \\\\\\\\\\\\\\\\
	// \\\\\\\\\\\\\\\\\
	// \\\\\\\\\\\\\\\\\\

	public void StartGame()
	{
		int worldIndex = Settings.worldIndex;
		if (worldIndex < 0)
			worldIndex = Globals.rndGenerator.nextInt(Settings.numWorlds);

		if (worldIndex != 6)
			Globals.soundManager.StopMusic();

		Settings.numPlayers = Globals.inputManager.numControllers;
		if (Settings.numPlayers < 2)
			Settings.numPlayers = 2;
		else if (Settings.numPlayers > 4)
			Settings.numPlayers = 4;

		worldMap = new WorldMap(Settings.worldWidth, Settings.worldHeight, WorldTypes.Standard, worldIndex);
		Globals.soundManager.PlaySong(worldMap.worldIndex);
	}

	public void EndGame()
	{
		worldMap.Unload(camera);
		worldMap = null;
		Globals.soundManager.StopMusic();
	}

	public void MainMenuOpen()
	{
		// Start menu music
		Globals.soundManager.PlaySong(Settings.mainMenuLevel);
	}
}

// /*******************************************************************************
// * Copyright 2011 See AUTHORS file.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//
// package com.brian.boomboom;
//
// import com.badlogic.gdx.ApplicationListener;
// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.Input.Keys;
// import com.badlogic.gdx.graphics.GL10;
// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.graphics.g2d.TextureRegion;
// import com.badlogic.gdx.scenes.scene2d.Actor;
// import com.badlogic.gdx.scenes.scene2d.Stage;
// import com.badlogic.gdx.scenes.scene2d.ui.Button;
// import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
// import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
// import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
// import com.badlogic.gdx.scenes.scene2d.ui.Image;
// import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
// import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
// import com.badlogic.gdx.scenes.scene2d.ui.Label;
// import com.badlogic.gdx.scenes.scene2d.ui.List;
// import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
// import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
// import com.badlogic.gdx.scenes.scene2d.ui.Skin;
// import com.badlogic.gdx.scenes.scene2d.ui.Slider;
// import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
// import com.badlogic.gdx.scenes.scene2d.ui.Table;
// import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
// import com.badlogic.gdx.scenes.scene2d.ui.TextField;
// import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
// import com.badlogic.gdx.scenes.scene2d.ui.Window;
// import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
// import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
//
// public class BoomBoomGame implements ApplicationListener {
// String[] listEntries = {"This is a list entry", "And another one", "The meaning of life", "Is hard to come by",
// "This is a list entry", "And another one", "The meaning of life", "Is hard to come by", "This is a list entry",
// "And another one", "The meaning of life", "Is hard to come by", "This is a list entry", "And another one",
// "The meaning of life", "Is hard to come by", "This is a list entry", "And another one", "The meaning of life",
// "Is hard to come by"};
//
// public final static double menuHeightPx = 74.2857142857143d;
// public final static float fMenuHeightPx = 74.2857142857143f;
// Skin skin;
// Stage stage;
// SpriteBatch batch;
// Texture texture1;
// Texture texture2;
// Label fpsLabel;
//
// @Override
// public void create () {
// batch = new SpriteBatch();
// skin = new Skin(Gdx.files.internal("data/uiskin.json"));
// texture1 = new Texture(Gdx.files.internal("data/badlogicsmall.jpg"));
// texture2 = new Texture(Gdx.files.internal("data/badlogic.jpg"));
// TextureRegion image = new TextureRegion(texture1);
// TextureRegion imageFlipped = new TextureRegion(image);
// imageFlipped.flip(true, true);
// TextureRegion image2 = new TextureRegion(texture2);
// stage = new Stage(1920, 1080, true);
// stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
// Gdx.input.setInputProcessor(stage);
//
// // Group.debug = true;
//
// ImageButtonStyle style = new ImageButtonStyle(skin.get(ButtonStyle.class));
// style.imageUp = new TextureRegionDrawable(image);
// style.imageDown = new TextureRegionDrawable(imageFlipped);
// ImageButton iconButton = new ImageButton(style);
//
// Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin, "toggle");
// Button imgButton = new Button(new Image(image), skin);
// Button imgToggleButton = new Button(new Image(image), skin, "toggle");
//
// Label myLabel = new Label("this is some text.", skin);
// myLabel.setWrap(true);
//
// Table t = new Table();
// t.row();
// t.add(myLabel);
//
// t.layout();
//
// CheckBox checkBox = new CheckBox("Check me", skin);
// final Slider slider = new Slider(0, 10, 1, false, skin);
// TextField textfield = new TextField("", skin);
// textfield.setMessageText("Click here!");
// SelectBox dropdown = new SelectBox(new String[] {"Android", "Windows", "Linux", "OSX"}, skin);
// Image imageActor = new Image(image2);
// ScrollPane scrollPane = new ScrollPane(imageActor);
// List list = new List(listEntries, skin);
// ScrollPane scrollPane2 = new ScrollPane(list, skin);
// scrollPane2.setFlickScroll(false);
// SplitPane splitPane = new SplitPane(scrollPane, scrollPane2, false, skin, "default-horizontal");
// fpsLabel = new Label("fps:", skin);
//
// // configures an example of a TextField in password mode.
// final Label passwordLabel = new Label("Textfield in password mode: ", skin);
// final TextField passwordTextField = new TextField("", skin);
// passwordTextField.setMessageText("password");
// passwordTextField.setPasswordCharacter('*');
// passwordTextField.setPasswordMode(true);
//
// // window.debug();
// Window window = new Window("Dialog", skin);
// window.setPosition(0, 0);
// window.defaults().spaceBottom(10);
// window.row().fill().expandX();
// window.add(iconButton);
// window.add(buttonMulti);
// window.add(imgButton);
// window.add(imgToggleButton);
// window.row();
// window.add(checkBox);
// window.add(slider).minWidth(100).fillX().colspan(3);
// window.row();
// window.add(dropdown);
// window.add(textfield).minWidth(100).expandX().fillX().colspan(3);
// window.row();
// window.add(splitPane).fill().expand().colspan(4).maxHeight(200);
// window.row();
// window.add(passwordLabel).colspan(2);
// window.add(passwordTextField).minWidth(100).expandX().fillX().colspan(2);
// window.row();
// window.add(fpsLabel).colspan(4);
// window.pack();
//
// // stage.addActor(new Button("Behind Window", skin));
// stage.addActor(window);
// window.debug();
//
// textfield.setTextFieldListener(new TextFieldListener() {
// public void keyTyped (TextField textField, char key) {
// if (key == '\n') textField.getOnscreenKeyboard().show(false);
// }
// });
//
// slider.addListener(new ChangeListener() {
// public void changed (ChangeEvent event, Actor actor) {
// Gdx.app.log("UITest", "slider: " + slider.getValue());
// }
// });
//
// iconButton.addListener(new ChangeListener() {
// public void changed (ChangeEvent event, Actor actor) {
// new Dialog("Some Dialog", skin, "dialog") {
// protected void result (Object object) {
// System.out.println("Chosen: " + object);
// }
// }.text("Are you enjoying this demo?").button("Yes", true).button("No", false).key(Keys.ENTER, true)
// .key(Keys.ESCAPE, false).show(stage);
// }
// });
// }
//
// @Override
// public void render () {
// Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
// Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//
// fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
//
// stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
// stage.draw();
// Table.drawDebug(stage);
// }
//
// @Override
// public void resize (int width, int height) {
// // stage.setViewport(width/2, height/2, false);
// }
//
// @Override
// public void dispose () {
// stage.dispose();
// skin.dispose();
// texture1.dispose();
// texture2.dispose();
// }
//
// @Override
// public void pause()
// {
//
// }
//
// @Override
// public void resume()
// {
//
// }
// }

