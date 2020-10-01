package com.brian.boomboom.bomb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brian.boomboom.Globals;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.Square;
import com.brian.boomboom.world.SquareTypes;
import com.brian.boomboom.world.WorldMap;

public class Explosion
{
	// private float[] LightIntensity = new float[] { 0f, 0.5f, 0.6f, 0.6f, 0.6f, 0.7f, 0.8f, 0.9f, 1f };
	// private float[] LightRange = new float[] { 0f, 3f, 3.3f, 3.6f, 3.9f, 4.2f, 4.3f, 4.4f, 4.5f };
	public Point position;
	public int explosionStrength;
	private TextureRegion[] explosionTexture;
	private int textureIndex;
	private boolean useTintColor = false;
	private Color explosionTintColor;
	private static Color colorOrange = new Color(1f, 0.5f, 0f, 1f);
	private static Color colorGreen = new Color(0f, 1f, 0f, 1f);
	private static Color colorWhite = Color.WHITE;
	public static int explosionFadeTime = 50;
	private long nextFadeTime;
	public ExplosionDirection direction;
	public ExplosionType explosionType = ExplosionType.Normal;

	public Explosion(int explosionPower, Point position, ExplosionDirection direction, ExplosionType explosionType)
	{
		Initialize(explosionPower, position, direction, explosionType);
	}

	private void Initialize(int explosionPower, Point position, ExplosionDirection direction,
			ExplosionType explosionType)
	{
		this.explosionStrength = explosionPower > 8 ? 8 : explosionPower;
		this.direction = direction;
		this.explosionType = explosionType;
		this.position = position.copy();
		this.nextFadeTime = GameTime.getGameTime() + explosionFadeTime;
		if (explosionType == ExplosionType.Flamethrower)
		{
			explosionTexture = Globals.explosionWhiteTexture;
			useTintColor = true;
			explosionTintColor = colorOrange;
		}
		else if (explosionType == ExplosionType.Laser)
		{
			explosionTexture = Globals.explosionWhiteTexture;
			useTintColor = true;
			explosionTintColor = colorGreen;
		}
		else if (explosionType == ExplosionType.Angel)
		{
			explosionTexture = Globals.explosionAngelTexture;
			useTintColor = false;
			explosionTintColor = colorWhite;
		}
		else if (explosionType == ExplosionType.Blue)
		{
			explosionTexture = Globals.explosionBlueTexture;
			useTintColor = false;
			explosionTintColor = colorWhite;
		}
		else
		{
			explosionTexture = Globals.explosionTexture;
			useTintColor = false;
			explosionTintColor = colorWhite;
		}
		SetTextureIndex();
	}

	public void Recycle(int explosionPower, Point position, ExplosionDirection direction, ExplosionType explosionType)
	{
		Initialize(explosionPower, position, direction, explosionType);
	}

	private void SetTextureIndex()
	{
		int str = explosionStrength;
		if(str > 8)
			str = 8;
		if(str < 1)
			textureIndex = 24;
		else if (direction == ExplosionDirection.Vertical)
			textureIndex = 8 - str;
		else if (direction == ExplosionDirection.Horizontal)
			textureIndex = 24 - str;
		else
			textureIndex = 16 - str;
	}

	public static ExplosionEffect blockCanBeAffectedByExplosion(Square sq)
	{
		if (sq.squareType == SquareTypes.Empty)
			return ExplosionEffect.FullEffect;
		else if (sq.squareType == SquareTypes.Hammered || sq.squareType == SquareTypes.WorldStandard)
			return ExplosionEffect.ThisButNoFurther;
		else if (sq.squareType == SquareTypes.Impenetrable)
			return ExplosionEffect.NoEffect;
		return ExplosionEffect.NoEffect;
	}

	public void Update(WorldMap worldMap)
	{
		if (GameTime.getGameTime() >= nextFadeTime)
		{
			nextFadeTime = GameTime.getGameTime() + explosionFadeTime;
			if (explosionStrength > 0)
			{
				worldMap.DoExplosionDamage(position, explosionStrength, explosionType);
			}
			explosionStrength--;
			SetTextureIndex();
		}
	}

	public void Draw(SpriteBatch batch)
	{
		if(textureIndex == 24)
			return;
		if (useTintColor)
			batch.setColor(explosionTintColor);
		batch.draw(explosionTexture[textureIndex], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
		if (useTintColor)
			batch.setColor(Color.WHITE);
	}
}

// package com.brian.boomboom.bomb;
//
// import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.graphics.g2d.TextureRegion;
// import com.brian.boomboom.Globals;
// import com.brian.boomboom.util.GameTime;
// import com.brian.boomboom.util.Point;
// import com.brian.boomboom.world.Square;
// import com.brian.boomboom.world.SquareTypes;
// import com.brian.boomboom.world.WorldMap;
//
// public class Explosion
// {
// // private float[] LightIntensity = new float[] { 0f, 0.5f, 0.6f, 0.6f, 0.6f, 0.7f, 0.8f, 0.9f, 1f };
// // private float[] LightRange = new float[] { 0f, 3f, 3.3f, 3.6f, 3.9f, 4.2f, 4.3f, 4.4f, 4.5f };
// public Point position;
// public int explosionStrength;
// private TextureRegion[] explosionTexture;
// private int textureIndex;
// private boolean useTintColor = false;
// private Color explosionTintColor;
// private static Color colorOrange = new Color(1f, 0.5f, 0f, 1f);
// private static Color colorGreen = new Color(0f, 1f, 0f, 1f);
// private static Color colorWhite = Color.WHITE;
// private static long explosionFadeTime = 50;
// private long nextFadeTime;
// public ExplosionDirection direction;
// public ExplosionType explosionType = ExplosionType.Normal;
//
// /**
// * For constructing explosions from bombs
// *
// * @param bomb
// * @param position
// * @param explosionFadesWithDistance
// * @param explosionType
// */
// public Explosion(Bomb bomb, Point position, boolean explosionFadesWithDistance, ExplosionType explosionType)
// {
// Initialize(bomb, position, explosionFadesWithDistance, explosionType);
// }
//
// /**
// * For constructing explosions not from bombs
// *
// * @param explosionPower
// * @param position
// * @param direction
// * @param explosionType
// */
// public Explosion(int explosionPower, Point position, ExplosionDirection direction, ExplosionType explosionType)
// {
// Initialize(explosionPower, position, direction, explosionType);
// }
//
// /**
// * For initializing explosions from bombs
// *
// * @param bomb
// * @param position
// * @param explosionFadesWithDistance
// * @param explosionType
// */
// private void Initialize(Bomb bomb, Point position, boolean explosionFadesWithDistance, ExplosionType explosionType)
// {
// // Set explosion strength
// if (!explosionFadesWithDistance)
// this.explosionStrength = bomb.owner.bombPower;
// else
// this.explosionStrength = bomb.owner.bombPower - position.IntDistanceFrom(bomb.position);
// this.explosionStrength = Math.min(8, this.explosionStrength);
// this.explosionStrength = Math.max(0, this.explosionStrength);
//
// // Set direction and textureIndex
// if (bomb.position.y != position.y)
// direction = ExplosionDirection.Vertical;
// else if (bomb.position.x != position.x)
// direction = ExplosionDirection.Horizontal;
// else
// direction = ExplosionDirection.Both;
//
// PartialInitialize(position, explosionType);
// }
//
// /**
// * For initializing explosions not from bombs.
// *
// * @param explosionPower
// * @param position
// * @param direction
// * @param explosionType
// */
// private void Initialize(int explosionPower, Point position, ExplosionDirection direction,
// ExplosionType explosionType)
// {
// this.explosionStrength = explosionPower;
// this.direction = direction;
// PartialInitialize(position, explosionType);
// }
//
// /**
// * A helper for the Initialize functions which performs actions common to both initialization methods.
// *
// * @param position
// * @param explosionType
// */
// private void PartialInitialize(Point position, ExplosionType explosionType)
// {
// this.explosionType = explosionType;
// this.position = position.copy();
// this.nextFadeTime = GameTime.getGameTime() + explosionFadeTime;
// if (explosionType == ExplosionType.Flamethrower)
// {
// explosionTexture = Globals.explosionWhiteTexture;
// useTintColor = true;
// explosionTintColor = colorOrange;
// }
// else if (explosionType == ExplosionType.Laser)
// {
// explosionTexture = Globals.explosionWhiteTexture;
// useTintColor = true;
// explosionTintColor = colorGreen;
// }
// else if (explosionType == ExplosionType.Angel)
// {
// explosionTexture = Globals.explosionAngelTexture;
// useTintColor = false;
// explosionTintColor = colorWhite;
// }
// else if (explosionType == ExplosionType.Blue)
// {
// explosionTexture = Globals.explosionBlueTexture;
// useTintColor = false;
// explosionTintColor = colorWhite;
// }
// else
// {
// explosionTexture = Globals.explosionTexture;
// useTintColor = false;
// explosionTintColor = colorWhite;
// }
// SetTextureIndex();
// }
//
// /**
// * For recycling explosions from bombs
// *
// * @param bomb
// * @param position
// * @param explosionFadesWithDistance
// * @param explosionType
// */
// public void Recycle(Bomb bomb, Point position, boolean explosionFadesWithDistance, ExplosionType explosionType)
// {
// Initialize(bomb, position, explosionFadesWithDistance, explosionType);
// }
//
// /**
// * For recycling explosions not from bombs
// *
// * @param explosionPower
// * @param position
// * @param direction
// * @param explosionType
// */
// public void Recycle(int explosionPower, Point position, ExplosionDirection direction, ExplosionType explosionType)
// {
// Initialize(explosionPower, position, direction, explosionType);
// }
//
// private void SetTextureIndex()
// {
// if (direction == ExplosionDirection.Vertical)
// textureIndex = 8 - explosionStrength;
// else if (direction == ExplosionDirection.Horizontal)
// textureIndex = 24 - explosionStrength;
// else
// textureIndex = 16 - explosionStrength;
// }
//
// public static ExplosionEffect blockCanBeAffectedByExplosion(Square sq)
// {
// if (sq.squareType == SquareTypes.Empty)
// return ExplosionEffect.FullEffect;
// else if (sq.squareType == SquareTypes.Hammered || sq.squareType == SquareTypes.WorldStandard)
// return ExplosionEffect.ThisButNoFurther;
// else if (sq.squareType == SquareTypes.Impenetrable)
// return ExplosionEffect.NoEffect;
// return ExplosionEffect.NoEffect;
// }
//
// public void Update(GameTime gameTime, WorldMap worldMap)
// {
// if (GameTime.getGameTime() >= nextFadeTime)
// {
// nextFadeTime = GameTime.getGameTime() + explosionFadeTime;
// if (explosionStrength > 0)
// {
// worldMap.DoExplosionDamage(position, explosionStrength, explosionType);
// }
// explosionStrength--;
// }
// }
//
// public void Draw(SpriteBatch batch)
// {
// batch.draw(explosionTexture[textureIndex], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
// }
// }