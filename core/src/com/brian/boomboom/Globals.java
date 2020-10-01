package com.brian.boomboom;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brian.boomboom.drawing.CharTextureColoringRequirements;
import com.brian.boomboom.drawing.CharTextureDefinition;
import com.brian.boomboom.drawing.CharTextureManager;
import com.brian.boomboom.drawing.CharTextureType;
import com.brian.boomboom.input.InputManager;
import com.brian.boomboom.sound.SoundManager;

public class Globals
{
	// Misc objects
	public static Random rndGenerator = new Random();

	public static InputManager inputManager;

	// Fonts
	public static BitmapFont fontDebug;
	public static BitmapFont fontAndy;

	// Textures
	/**
	 * A two-dimensional array of World texture regions. The first index is the world index, and the second index is the
	 * texture index.
	 */
	public static TextureRegion[][] worldTextures;
	/**
	 * An array of World texture regions. This array corresponds to the currently loaded world.
	 */
	public static TextureRegion[] worldTexture;
	/**
	 * An object which handles drawing of player characters.
	 */
	public static CharTextureManager oldManCharTextureManager;
	/**
	 * The Texture Atlas, which contains all standard graphics.
	 */
	private static TextureAtlas atlas;

	/**
	 * An array of Item texture regions.
	 */
	public static TextureRegion[] itemTexture;

	/**
	 * An array of Bomb texture regions.
	 */
	public static TextureRegion[] bombTexture;

	/**
	 * An array of Detonator texture regions.
	 */
	public static TextureRegion[] detonatorTexture;

	/**
	 * Explosion texture for dynamically tinted explosions.
	 */
	public static TextureRegion[] explosionWhiteTexture;

	/**
	 * Explosion texture for Angel-caused explosions.
	 */
	public static TextureRegion[] explosionAngelTexture;

	/**
	 * Explosion texture for players who have summoned the Angel of Death.
	 */
	public static TextureRegion[] explosionBlueTexture;

	/**
	 * Explosion texture for normal explosions.
	 */
	public static TextureRegion[] explosionTexture;

	/**
	 * Rocket textures.
	 */
	public static TextureRegion[] rocketTexture;

	/**
	 * PixelWhite texture.
	 */
	public static TextureRegion pixelWhiteTexture;

	/**
	 * Angel texture.
	 */
	public static TextureRegion angelTexture;

	/**
	 * Background Texture
	 */
	public static Texture menuBackgroundTexture;
	
	/**
	 * World 2 background texture
	 */
	//public static TextureRegion world2BackgroundTexture;

	/**
	 * Background Sprite
	 */
	public static Sprite menuBackgroundSprite;

	/**
	 * Logo Texture
	 */
	public static Texture logoTexture;

	/**
	 * Texture for Light1
	 */
	public static Texture light1Texture;
	
	/**
	 * Texture for Light2
	 */
	public static Texture light2Texture;
	

	/**
	 * Texture for Banana Peel
	 */
	public static TextureRegion bananaPeelTexture;

	public static SoundManager soundManager = new SoundManager();

	/**
	 * Call this when it is time to load content (you must call unloadContent() before calling this again)
	 */
	public static void loadContent()
	{
		atlas = new TextureAtlas(Gdx.files.internal("data/BBTex1.pack"));
		// Load world texture
		ArrayList<TextureRegion[]> worldTexTemp = new ArrayList<TextureRegion[]>();
		TextureRegion[] texRegions = atlas.findRegions("World0").toArray(TextureRegion.class);
		while (texRegions.length > 0)
		{
			worldTexTemp.add(texRegions);
			int worldIndex = worldTexTemp.size();
//			if(worldIndex == 2)
//			{
//				// Special loading code for world 2
//				texRegions = new TextureRegion[9];
//				texRegions[0] = texRegions[1] = texRegions[2] = texRegions[6] = atlas.findRegion("Rock0");
//				texRegions[3] = texRegions[4] = texRegions[5] = atlas.findRegion("LavaLiquid");
//				texRegions[7] = atlas.findRegion("Rock1");
//				texRegions[8] = atlas.findRegion("Rock2");
//			}
//			else
//			{
				texRegions = atlas.findRegions("World" + worldIndex).toArray(TextureRegion.class);
//			}
		}
		worldTextures = new TextureRegion[0][0];
		worldTextures = worldTexTemp.toArray(worldTextures);
		
		// Load "special" world textures
		//world2BackgroundTexture = atlas.findRegion("LavaBackground");

		// Load character textures
		oldManCharTextureManager = new CharTextureManager();
		CharTextureDefinition ctd0 = new CharTextureDefinition("Char_Old_Man_0_NC",
				CharTextureColoringRequirements.AlreadyColored);
		CharTextureDefinition ctd1 = new CharTextureDefinition("Char_Old_Man_1_C0",
				CharTextureColoringRequirements.NeedsColored, 0);
		CharTextureDefinition ctd2 = new CharTextureDefinition("Char_Old_Man_2_C0",
				CharTextureColoringRequirements.NeedsColored, 0);
		CharTextureDefinition ctd3 = new CharTextureDefinition("Char_Old_Man_3_C1",
				CharTextureColoringRequirements.NeedsColored, 1);
		CharTextureDefinition ctd4 = new CharTextureDefinition("Char_Old_Man_4_C1",
				CharTextureColoringRequirements.NeedsColored, 1);
		CharTextureDefinition ctd5 = new CharTextureDefinition("Char_Old_Man_5_NC_AngelEyes",
				CharTextureColoringRequirements.AlreadyColored, CharTextureType.AngelEyes);
		CharTextureDefinition ctd6 = new CharTextureDefinition("Char_Old_Man_6_NC_WinterHat",
				CharTextureColoringRequirements.AlreadyColored, CharTextureType.WinterHat);
		CharTextureDefinition ctd7 = new CharTextureDefinition("Char_Old_Man_7_C2_WinterHat",
				CharTextureColoringRequirements.NeedsColored, 2, CharTextureType.WinterHat);
		CharTextureDefinition ctd8 = new CharTextureDefinition("Char_Old_Man_8_NC_LaserSpecs",
				CharTextureColoringRequirements.AlreadyColored, CharTextureType.LaserSpecs);
		oldManCharTextureManager.Load(atlas, new CharTextureDefinition[] { ctd0, ctd1, ctd2, ctd3, ctd4, ctd5, ctd6,
				ctd7, ctd8 });

		// Load item textures
		itemTexture = atlas.findRegions("Items").toArray(TextureRegion.class);

		// Load bomb textures
		bombTexture = atlas.findRegions("Bomb").toArray(TextureRegion.class);

		// Load detonator textures
		detonatorTexture = atlas.findRegions("Detonator").toArray(TextureRegion.class);

		// Load Explosion textures
		explosionWhiteTexture = atlas.findRegions("ExplosionWhite").toArray(TextureRegion.class);
		explosionAngelTexture = atlas.findRegions("ExplosionAngel").toArray(TextureRegion.class);
		explosionBlueTexture = atlas.findRegions("ExplosionBlue").toArray(TextureRegion.class);
		explosionTexture = atlas.findRegions("Explosion").toArray(TextureRegion.class);

		// Load Rocket textures
		rocketTexture = atlas.findRegions("Rocket").toArray(TextureRegion.class);

		// Load PixelWhite texture
		pixelWhiteTexture = atlas.findRegion("PixelWhite");

		// Load Angel texture
		angelTexture = atlas.findRegion("Angel");
		
		// Load Banana Peel Texture
		bananaPeelTexture = atlas.findRegion("BananaPeel");

		// Load Menu Textures
		menuBackgroundTexture = new Texture(Gdx.files.internal("data/BoomBoomBg.png"));
		menuBackgroundSprite = new Sprite(menuBackgroundTexture);
		logoTexture = new Texture(Gdx.files.internal("data/BoomBoomLogoTex.png"));
		
		// Load Light Textures
		light1Texture = new Texture(Gdx.files.internal("data/Light1.png"));
		light2Texture = new Texture(Gdx.files.internal("data/Light2.png"));

		// Load Fonts
		fontDebug = new BitmapFont();
		fontAndy = new BitmapFont(Gdx.files.internal("data/Andy.fnt"), false);

		// Load SoundManager
		soundManager.loadContent();
	}

	/**
	 * Call this when it is time to unload content (you must call loadContent() before calling this)
	 */
	public static void unloadContent()
	{
		oldManCharTextureManager.Unload();
		atlas.dispose();
		menuBackgroundTexture.dispose();
		logoTexture.dispose();
		light1Texture.dispose();
		light2Texture.dispose();
		
		fontDebug.dispose();
		fontAndy.dispose();
		soundManager.StopAllSounds();
		soundManager.StopMusic();
		soundManager.unloadContent();
	}

	public static void initWorldTexture(int worldIndex)
	{
		if (worldIndex < 0 || worldIndex >= worldTextures.length)
			worldIndex = 0;
		worldTexture = worldTextures[worldIndex];
	}

	// private static TextureRegion[] initSquareTextureRegions(Texture tex, int itemWidth, int itemHeight)
	// {
	// int numWide = tex.getWidth() / itemWidth;
	// int numHigh = tex.getHeight() / itemHeight;
	// int numRegions = numWide * numHigh;
	// // Create array and fill it
	// TextureRegion[] texRegions = new TextureRegion[numRegions];
	// for (int j = numHigh - 1; j >= 0; j--)
	// for (int i = numWide - 1; i >= 0; i--)
	// texRegions[i + (j * numWide)] = new TextureRegion(tex, i * itemWidth, j * itemHeight, itemWidth,
	// itemHeight);
	// return texRegions;
	// }

	public static int nextInt(int minInclusive, int maxExclusive)
	{
		return minInclusive + rndGenerator.nextInt(maxExclusive - minInclusive);
	}
}
