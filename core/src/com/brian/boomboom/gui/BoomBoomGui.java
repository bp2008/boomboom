package com.brian.boomboom.gui;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.brian.boomboom.BoomBoomGame;
import com.brian.boomboom.Globals;
import com.brian.boomboom.input.Direction;
import com.brian.boomboom.item.Item;
import com.brian.boomboom.item.ItemType;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.util.ColorHelper;
import com.brian.boomboom.util.GameTime;

public class BoomBoomGui
{
	boolean isActive = true;

	public Skin skin;
	Stage stage;

	LogoActor logoActor = null;

	GuiState gs = GuiState.None;

	ArrayList<Label> labels;
	ArrayList<Label> settingLabels;
	float modifier = 435f;

	int selectedIndex = 0;

	float logoScaler = 0f;
	float logoW = 1265;
	float logoH = 571;
	float logoX = 10;
	float logoY = 499;
	float tempLogoW = logoW;
	float tempLogoH = logoH;
	float tempLogoX = logoX;
	float tempLogoY = logoY;
	float tempLogoScaler;
	float hue = 0f;

	int itemGuidePage = 0;

	BoomBoomGame game;

	public BoomBoomGui(BoomBoomGame boomBoomGame, Camera camera)
	{
		game = boomBoomGame;

		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		ScalingViewport viewport = new ScalingViewport(Scaling.stretch, 1920, 1080);
		viewport.setCamera(camera);
		stage = new Stage(viewport);
		LoadState(GuiState.MainMenu);
	}

	public void dispose()
	{
		stage.dispose();
		skin.dispose();
	}

	public void draw()
	{
		if (!isActive)
			return;
		float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);

		if (labels != null)
		{
			Label lblSelected = GetSelectedLabel();
			if (lblSelected != null)
			{
				hue += delta / 2f;
				com.badlogic.gdx.graphics.Color colorGDX = ColorHelper.FromHSV(hue, 1f, 1f);
				lblSelected.setColor(colorGDX);
			}
		}

		if (gs == GuiState.MainMenu)
		{
			Label playLabel = GetLabelWithName("play");
			if (playLabel != null)
			{
				int numPlayers = Globals.inputManager.numControllers;
				if (numPlayers == 1)
					playLabel.setText("Play (1 controller active)");
				else
					playLabel.setText("Play (" + numPlayers + " controllers active)");
			}
		}

		stage.act(delta);
		SetLogoBounds(delta);
		stage.draw();
	}

	private void SetLogoBounds(float delta)
	{
		logoScaler = ((GameTime.getGameTime() % modifier) / modifier);

		tempLogoScaler = 1 - (Math.abs((logoScaler - (int) logoScaler) - 0.5f) * 0.1f);

		tempLogoW = logoW * tempLogoScaler;
		tempLogoH = logoH * tempLogoScaler;
		tempLogoX = logoX - (tempLogoW - logoW) / 2;
		tempLogoY = logoY - (tempLogoH - logoH) / 2;

		logoActor.setBounds(tempLogoX, tempLogoY, tempLogoW, tempLogoH);
	}

	public void LoadState(GuiState gs)
	{
		this.gs = gs;
		isActive = true;
		stage.clear();
		labels = new ArrayList<Label>();
		settingLabels = new ArrayList<Label>();

		if (gs == GuiState.MainMenu)
		{
			TextureRegionDrawable trd = new TextureRegionDrawable(Globals.menuBackgroundSprite);
			Table tbl = new Table(skin);
			tbl.setBackground(trd);
			tbl.setFillParent(true);

			tbl.add(new Label(" ", skin));
			tbl.row();
			tbl.add(new Label(" ", skin));
			tbl.row();

			Label label = new Label("Play", skin);
			label.setName("play");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Options", skin);
			label.setName("options");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Item Guide", skin);
			label.setName("itemguide");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Credits", skin);
			label.setName("credits");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Scores", skin);
			label.setName("scores");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Exit", skin);
			label.setName("exit");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			stage.addActor(tbl);

			logoActor = new LogoActor(Globals.logoTexture);

			stage.addActor(logoActor);

			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());

			SetSelectedState(0);

			game.MainMenuOpen();
		}
		else if (gs == GuiState.IngameNewGameMessage)
		{
			Table tbl = new Table(skin);
			tbl.setBackground("dialogDim");
			tbl.setFillParent(true);

			tbl.add(new Label("Dead players may start the next round", skin));
			tbl.row();
			tbl.add(new Label("by pressing the menu button...", skin));
			tbl.row();

			stage.addActor(tbl);
		}
		else if (gs == GuiState.IngameMenu)
		{
			game.pause();

			Table tbl = new Table(skin);
			tbl.setBackground("dialogDim65");
			tbl.setFillParent(true);

			Label label = new Label("Resume", skin);
			label.setName("resume");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("End Game", skin);
			label.setName("end");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			stage.addActor(tbl);

			SetSelectedState(0);
		}
		else if (gs == GuiState.Options)
		{
			TextureRegionDrawable trd = new TextureRegionDrawable(Globals.menuBackgroundSprite);
			Table tbl = new Table(skin);
			tbl.setBackground(trd);
			tbl.setFillParent(true);

			tbl.add(new Label("                    ", skin));
			tbl.add(new Label("                    ", skin));
			tbl.row();
			tbl.add(new Label("                    ", skin));
			tbl.add(new Label("                    ", skin));
			tbl.row();
			tbl.add(new Label("                    ", skin));
			tbl.add(new Label("                    ", skin));
			tbl.row();
			
			Label label = new Label("Options", skin);
			tbl.add(label);
			tbl.row();

			tbl.add(new Label("                    ", skin));
			tbl.add(new Label("                    ", skin));
			tbl.row();

			label = new Label("Level Select: ", skin);
			label.setName("levelselect_lbl");
			labels.add(label);
			tbl.add(label);

			label = new Label(Settings.GetLevelSelectionString(), skin);
			label.setName("levelselect");
			settingLabels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Veteran Mode: ", skin);
			label.setName("faststart_lbl");
			labels.add(label);
			tbl.add(label);

			label = new Label(Settings.IsFastStart() ? "ON" : "OFF", skin);
			label.setName("faststart");
			settingLabels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Overscan Compensation: ", skin);
			label.setName("overscan_lbl");
			labels.add(label);
			tbl.add(label);

			label = new Label(String.valueOf(Settings.zoom), skin);
			label.setName("overscan");
			settingLabels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Show Scores During Game: ", skin);
			label.setName("scoredisplay_lbl");
			labels.add(label);
			tbl.add(label);

			label = new Label(Settings.showScoresByHealth ? "Yes" : "No", skin);
			label.setName("scoredisplay");
			settingLabels.add(label);
			tbl.add(label);
			tbl.row();

			label = new Label("Back to Main Menu", skin);
			label.setName("back");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			stage.addActor(tbl);

			logoActor = new LogoActor(Globals.logoTexture);

			stage.addActor(logoActor);

			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());

			SetSelectedState(0);
		}
		else if (gs == GuiState.ItemGuide)
		{
			TextureRegionDrawable trd = new TextureRegionDrawable(Globals.menuBackgroundSprite);
			Table tbl = new Table(skin);

			tbl.add(new Label("", skin));
			Label label = new Label("Back to Main Menu", skin);
			label.setName("back");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			// tbl.add(new Label("", skin));
			// Label label = new Label("Item Guide", skin);
			// tbl.add(label);
			// tbl.row();
			// tbl.add(new Label("", skin));
			// label = new Label("Powerups", skin);
			// tbl.add(label);
			// tbl.row();

			// int itemCount = 20;
			int pages = 5;

			if (itemGuidePage > 0)
			{
				tbl.add(new Label("", skin));
				label = new Label("Previous Page", skin);
				label.setName("prev");
				labels.add(label);
				tbl.add(label);
				tbl.row();
			}

			if (itemGuidePage == 0)
			{
				ItemGuideHelper.AddItemRow(ItemType.Bomb, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Power, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Foot, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Rollerskates, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Pizza, tbl, labels, skin);
			}
			else if (itemGuidePage == 1)
			{
				ItemGuideHelper.AddItemRow(ItemType.Disease, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.AngelOfDeath, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Hammer, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.AutoHammer, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Detonator, tbl, labels, skin);
			}
			else if (itemGuidePage == 2)
			{
				ItemGuideHelper.AddItemRow(ItemType.Heatseeker, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Flamethrower, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Laser, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.RemoteTeleporter, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.HolyChalice, tbl, labels, skin);
			}
			else if (itemGuidePage == 3)
			{
				ItemGuideHelper.AddItemRow(ItemType.HealthMedicine, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.HealthMedicine2, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.LineBomb, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Randomfuse, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Shortfuse, tbl, labels, skin);
			}
			else if (itemGuidePage == 4)
			{
				ItemGuideHelper.AddItemRow(ItemType.Popsicle, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.TwinPop, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Banana, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.SwapPortal, tbl, labels, skin);
				ItemGuideHelper.AddItemRow(ItemType.Earthquake, tbl, labels, skin);
			}

			if (itemGuidePage + 1 < pages)
			{
				tbl.add(new Label("", skin));
				label = new Label("Next Page", skin);
				label.setName("next");
				labels.add(label);
				tbl.add(label);
				tbl.row();
			}

			label = new Label("", skin);
			label.setName("itemdescription");
			settingLabels.add(label);

			Table tblOuter = new Table(skin);
			tblOuter.setBackground(trd);
			tblOuter.setFillParent(true);

			tblOuter.add(tbl);
			tblOuter.row();
			tblOuter.add(label);
			tblOuter.row();

			stage.addActor(tblOuter);

			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());

			SetSelectedState(0);

			SetCurrentItemGuideDescription();
		}
		else if (gs == GuiState.Credits)
		{
			TextureRegionDrawable trd = new TextureRegionDrawable(Globals.menuBackgroundSprite);
			Table tbl = new Table(skin);
			tbl.setBackground(trd);
			tbl.setFillParent(true);
			
			Label label = new Label("BoomBoom created by", skin);
			tbl.add(label);
			tbl.row();
			
			label = new Label("Brian <briansgames.com>", skin);
			label.setName("brian");
			labels.add(label);
			tbl.add(label);
			tbl.row();
			
			tbl.add(new Label(" ", skin));
			tbl.row();

			label = new Label("Theme and Retro Level Music by", skin);
			label.setName("souleyetitle");
			tbl.add(label);
			tbl.row();

			label = new Label("Souleye <souleye.se>", skin);
			label.setName("souleye");
			labels.add(label);
			tbl.add(label);
			tbl.row();
			
			tbl.add(new Label(" ", skin));
			tbl.row();

			label = new Label("Other Levels Music by", skin);
			label.setName("ericskifftitle");
			tbl.add(label);
			tbl.row();

			label = new Label("Eric Skiff <ericskiff.com>", skin);
			label.setName("ericskiff");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			tbl.add(new Label(" ", skin));
			tbl.row();
			
			label = new Label("Back to Main Menu", skin);
			label.setName("back");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			stage.addActor(tbl);

			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());

			SetSelectedState(0);
		}
		else if (gs == GuiState.Scores)
		{
			TextureRegionDrawable trd = new TextureRegionDrawable(Globals.menuBackgroundSprite);
			Table tbl = new Table(skin);
			tbl.setBackground(trd);
			tbl.setFillParent(true);

			tbl.add(new Label("Scores", skin));
			tbl.add(new Label("Session / Overall", skin));
			tbl.row();

			tbl.add(new Label("                    ", skin));
			tbl.add(new Label("                    ", skin));
			tbl.row();
			tbl.add(new Label("Player 1:", skin));
			tbl.add(new Label(Settings.p1SessionScore + " / " + Settings.p1Score, skin));
			tbl.row();
			tbl.add(new Label("Player 2:", skin));
			tbl.add(new Label(Settings.p2SessionScore + " / " + Settings.p2Score, skin));
			tbl.row();
			tbl.add(new Label("Player 3:", skin));
			tbl.add(new Label(Settings.p3SessionScore + " / " + Settings.p3Score, skin));
			tbl.row();
			tbl.add(new Label("Player 4:", skin));
			tbl.add(new Label(Settings.p4SessionScore + " / " + Settings.p4Score, skin));
			tbl.row();
			tbl.add(new Label("Ties:", skin));
			tbl.add(new Label(Settings.tieSessionScore + " / " + Settings.tieScore, skin));
			tbl.row();
			tbl.add(new Label("                    ", skin));
			tbl.add(new Label("                    ", skin));
			tbl.row();

			Label label = new Label("Back to Main Menu", skin);
			label.setName("back");
			labels.add(label);
			tbl.add(label);
			tbl.row();

			stage.addActor(tbl);

			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());
			stage.addActor(new LightActor());

			SetSelectedState(0);
		}
	}

	private void SetSelectedState(int idx)
	{
		if (gs == GuiState.IngameNewGameMessage)
			selectedIndex = 0;
		else if (gs == GuiState.MainMenu || gs == GuiState.IngameMenu || gs == GuiState.Options
				|| gs == GuiState.Credits || gs == GuiState.ItemGuide || gs == GuiState.Scores)
		{
			if(idx < 0)
				selectedIndex = labels.size() - 1;
			else if(idx >= labels.size())
				selectedIndex = 0;
			else
				selectedIndex = idx;
			for (int i = 0; i < labels.size(); i++)
			{
				if (i == selectedIndex)
					labels.get(i).setColor(Color.WHITE);
				else
					labels.get(i).setColor(Color.GRAY);
			}
		}
	}

	public boolean DirectionButtonPressed(int playerIndex, Direction dir)
	{
		if (!BlockInput())
			return false;
		if (gs == GuiState.MainMenu || gs == GuiState.IngameMenu || gs == GuiState.Options || gs == GuiState.Credits
				|| gs == GuiState.ItemGuide)
		{
			if (dir == Direction.Down)
				SetSelectedState(selectedIndex + 1);
			else if (dir == Direction.Up)
				SetSelectedState(selectedIndex - 1);
			if (gs == GuiState.ItemGuide)
				SetCurrentItemGuideDescription();
		}
		if (gs == GuiState.Options)
		{
			if (dir == Direction.Left)
				UseButtonPressed(playerIndex);
			else if (dir == Direction.Right)
				RightSettingsButtonPressed(playerIndex);
		}
		return true;
	}

	public void SetCurrentItemGuideDescription()
	{
		String selectedLabelName = GetSelectedLabelName();
		Label settingLabel = GetSettingLabelWithName("itemdescription");
		if (selectedLabelName != "next" && selectedLabelName != "prev" && selectedLabelName != "back")
		{
			try
			{
				ItemType itemType = ItemType.valueOf(selectedLabelName);
				settingLabel.setText(ItemGuideHelper.TextWrap(Item.getItemInfo(itemType).Description));
			}
			catch (Exception ex)
			{
				settingLabel.setText("Error");
			}
		}
		else
			settingLabel.setText("");
	}

	public boolean BackButtonPressed(int playerIndex)
	{
		return StartButtonPressed(playerIndex);
	}

	public boolean BombButtonPressed(int playerIndex)
	{
		if (gs == GuiState.Options)
			return RightSettingsButtonPressed(playerIndex);
		return UseButtonPressed(playerIndex);
	}

	public boolean UseButtonPressed(int playerIndex)
	{
		if (!BlockInput())
			return false;

		if (gs == GuiState.MainMenu)
		{
			String selectedLabelName = GetSelectedLabelName();
			if (selectedLabelName.equals("play"))
			{
				game.StartGame();
				DeactivateGui();
			}
			else if (selectedLabelName.equals("options"))
			{
				LoadState(GuiState.Options);
			}
			else if (selectedLabelName.equals("itemguide"))
			{
				itemGuidePage = 0;
				LoadState(GuiState.ItemGuide);
			}
			else if (selectedLabelName.equals("credits"))
			{
				LoadState(GuiState.Credits);
			}
			else if (selectedLabelName.equals("scores"))
			{
				LoadState(GuiState.Scores);
			}
			else if (selectedLabelName.equals("exit"))
			{
				Gdx.app.exit();
			}
		}
		else if (gs == GuiState.IngameMenu)
		{
			String selectedLabelName = GetSelectedLabelName();
			if (selectedLabelName.equals("resume"))
			{
				DeactivateGui();
				game.resume();
			}
			else if (selectedLabelName.equals("end"))
			{
				game.resume();
				game.EndGame();
				LoadState(GuiState.MainMenu);
			}
		}
		else if (gs == GuiState.Options)
		{
			Label lbl = GetSelectedLabel();
			if (lbl != null)
			{
				String selectedLabelName = lbl.getName();
				if (selectedLabelName.equals("levelselect_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("levelselect");
					if (settingLabel != null)
					{
						Settings.worldIndex--;
						if (Settings.worldIndex < -1 || Settings.worldIndex >= Settings.numWorlds)
							Settings.worldIndex = Settings.numWorlds - 1;
						Settings.Save();
						settingLabel.setText(Settings.GetLevelSelectionString());
					}
				}
				else if (selectedLabelName.equals("faststart_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("faststart");
					if (settingLabel != null)
					{
						Settings.SetFastStart(!Settings.IsFastStart());
						Settings.Save();
						settingLabel.setText(Settings.IsFastStart() ? "ON" : "OFF");
					}
				}
				else if (selectedLabelName.equals("overscan_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("overscan");
					if (settingLabel != null)
					{
						int zoom = Settings.zoom - 1;
						if (zoom < 0)
							zoom = 0;
						if(zoom != Settings.zoom)
						{
							Settings.zoom = zoom;
							Settings.Save();
							game.SetZoom();
						}
						settingLabel.setText(String.valueOf(zoom));
					}
				}
				else if (selectedLabelName.equals("scoredisplay_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("scoredisplay");
					if (settingLabel != null)
					{
						Settings.showScoresByHealth = !Settings.showScoresByHealth;
						Settings.Save();
						settingLabel.setText(Settings.showScoresByHealth ? "Yes" : "No");
					}
				}
				else if (selectedLabelName.equals("back"))
				{
					LoadState(GuiState.MainMenu);
				}
			}
		}
		else if (gs == GuiState.ItemGuide)
		{
			String selectedLabelName = GetSelectedLabelName();
			if (selectedLabelName.equals("back"))
			{
				LoadState(GuiState.MainMenu);
			}
			else if (selectedLabelName.equals("next"))
			{
				itemGuidePage++;
				LoadState(GuiState.ItemGuide);
			}
			else if (selectedLabelName.equals("prev"))
			{
				itemGuidePage--;
				LoadState(GuiState.ItemGuide);
			}
		}
		else if (gs == GuiState.Credits)
		{
			String selectedLabelName = GetSelectedLabelName();
			if (selectedLabelName.equals("brian"))
			{
				game.browserOpener.OpenUrl("http://www.briansgames.com/");
			}
			else if (selectedLabelName.equals("souleye"))
			{
				game.browserOpener.OpenUrl("http://www.souleye.se/");
			}
			else if (selectedLabelName.equals("ericskiff"))
			{
				game.browserOpener.OpenUrl("http://www.ericskiff.com/");
			}
			else if (selectedLabelName.equals("back"))
			{
				LoadState(GuiState.MainMenu);
			}
		}
		else if (gs == GuiState.Scores)
		{
			LoadState(GuiState.MainMenu);
		}
		return true;
	}

	public boolean RightSettingsButtonPressed(int playerIndex)
	{
		if (!BlockInput())
			return false;

		if (gs == GuiState.Options)
		{
			Label lbl = GetSelectedLabel();
			if (lbl != null)
			{
				String selectedLabelName = lbl.getName();
				if (selectedLabelName.equals("levelselect_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("levelselect");
					if (settingLabel != null)
					{
						Settings.worldIndex++;
						if (Settings.worldIndex < -1 || Settings.worldIndex >= Settings.numWorlds)
							Settings.worldIndex = -1;
						Settings.Save();
						settingLabel.setText(Settings.GetLevelSelectionString());
					}
				}
				else if (selectedLabelName.equals("faststart_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("faststart");
					if (settingLabel != null)
					{
						Settings.SetFastStart(!Settings.IsFastStart());
						Settings.Save();
						settingLabel.setText(Settings.IsFastStart() ? "ON" : "OFF");
					}
				}
				else if (selectedLabelName.equals("overscan_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("overscan");
					if (settingLabel != null)
					{
						int zoom = Settings.zoom + 1;
						if (zoom > 100)
							zoom = 100;
						if(zoom != Settings.zoom)
						{
							Settings.zoom = zoom;
							Settings.Save();
							game.SetZoom();
						}
						settingLabel.setText(String.valueOf(zoom));
					}
				}
				else if (selectedLabelName.equals("scoredisplay_lbl"))
				{
					Label settingLabel = GetSettingLabelWithName("scoredisplay");
					if (settingLabel != null)
					{
						Settings.showScoresByHealth = !Settings.showScoresByHealth;
						Settings.Save();
						settingLabel.setText(Settings.showScoresByHealth ? "Yes" : "No");
					}
				}
				else if (selectedLabelName.equals("back"))
				{
					LoadState(GuiState.MainMenu);
				}
			}
		}

		return true;
	}

	public boolean StartButtonPressed(int playerIndex)
	{
		if (gs == GuiState.IngameNewGameMessage)
		{
			if (game.worldMap.numPlayersAlive < 2 && game.worldMap.players.length > playerIndex
					&& game.worldMap.players[playerIndex].health <= 0)
			{
				Settings.RecordWin(game.worldMap);
				game.resume();
				game.EndGame();
				game.StartGame();
				DeactivateGui();
			}
		}
		else if (gs == GuiState.None)
		{
			LoadState(GuiState.IngameMenu);
		}
		return BlockInput();
	}

	public boolean BlockInput()
	{
		return isActive && gs != GuiState.IngameNewGameMessage;
	}

	private Label GetSelectedLabel()
	{
		if (selectedIndex > -1 && selectedIndex < labels.size())
			return labels.get(selectedIndex);
		return null;
	}

	private Label GetLabelWithName(String name)
	{
		for (int i = 0; i < labels.size(); i++)
			if (labels.get(i).getName().equals(name))
				return labels.get(i);
		return null;
	}

	private Label GetSettingLabelWithName(String name)
	{
		for (int i = 0; i < settingLabels.size(); i++)
			if (settingLabels.get(i).getName().equals(name))
				return settingLabels.get(i);
		return null;
	}

	private String GetSelectedLabelName()
	{
		Label lbl = GetSelectedLabel();
		if (lbl == null)
			return "";
		return lbl.getName();
	}

	private void DeactivateGui()
	{
		gs = GuiState.None;
		isActive = false;
	}
}
