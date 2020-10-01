package com.brian.boomboom.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.Globals;
import com.brian.boomboom.bomb.Explosion;
import com.brian.boomboom.input.Direction;
import com.brian.boomboom.input.KeyboardScheme;
import com.brian.boomboom.item.DiseaseType;
import com.brian.boomboom.item.ItemType;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.sound.SoundType;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.util.Utilities;
import com.brian.boomboom.world.Levels;
import com.brian.boomboom.world.WorldMap;

public class Player extends Entity
{
	// Misc objects
	private int playerIndex;
	private KeyboardScheme kbScheme;

	// Input helpers
	private Direction queuedMovement = Direction.None;
	private Direction queuedFace = Direction.None;
	private boolean plantBomb = false;
	private boolean useItem = false;
	// private boolean useBack = false;
	public Color healthColor = Color.WHITE;

	private Point previousPosition;

	public Player(Point point, WorldMap worldMap, int playerIndex)
	{
		super(point, worldMap);
		this.previousPosition = position.copy();
		this.playerIndex = playerIndex;
		if (playerIndex == 0)
			healthColor = Color.BLUE;
		else if (playerIndex == 1)
			healthColor = Color.BLACK;
		else if (playerIndex == 2)
			healthColor = Utilities.Color(0, 200, 0);
		else if (playerIndex == 3)
			healthColor = Utilities.Color(160, 30, 20);
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		boolean isControlFrozen = isControlFrozen();
		Globals.oldManCharTextureManager.Draw(batch, playerIndex, animationFrameIndex, position.x, position.y,
				diseaseType != DiseaseType.None, foundAngelOfDeath,
				item.itemType == ItemType.Laser && item.charges > 0, worldMap.worldIndex == Levels.Snow,
				isControlFrozen && isPopsicleFrozen, isControlFrozen ? slipDirection : Direction.None);
	}

	@Override
	public void Update()
	{
		UpdateStart();

		if (health <= 0)
		{
			IAmDead();
			return;
		}

		long gameTime = GameTime.getGameTime();

		boolean isControlFrozen = isControlFrozen();
		if (!isControlFrozen)
		{
			isPopsicleFrozen = isEarthquakeFrozen = false;
			slipDirection = Direction.None;
		}

		boolean isUnableToAct = isControlFrozen && (isPopsicleFrozen || isEarthquakeFrozen || slipDirection != Direction.None);
		if (isUnableToAct)
		{
			useItem = false;
			plantBomb = false;
			queuedMovement = Direction.None;
			queuedFace = Direction.None;
		}

		// Handle Bomb Drop
		if (!isUnableToAct && (plantBomb || diseaseType == DiseaseType.Diarrhea))
		{
			plantBomb = false;
			if (bombCount > myActiveBombs.size())
			{
				if ((Settings.canPlaceBombInFire && this.diseaseType != DiseaseType.Diarrhea)
						|| !worldMap.BlockHasFire(position))
				{
					if (!worldMap.BombIsHere(position))
						myActiveBombs.add(worldMap.AddBomb(position, this));
					if (item.itemType == ItemType.LineBomb)
					{
						// Player has the line bomb item. Add more bombs in the direction he is facing.
						Point delta = GetForwardDelta();
						positionProposed.x = position.x;
						positionProposed.y = position.y;
						while (bombCount > myActiveBombs.size())
						{
							positionProposed.x += delta.x;
							positionProposed.y += delta.y;
							if (worldMap.PositionCanReceiveKickedBomb(positionProposed)
									&& !worldMap.BlockHasFire(positionProposed))
								myActiveBombs.add(worldMap.AddBomb(positionProposed, this));
							else
								break;
						}
					}
				}
			}
		}

		// Handle Movement
		previousPosition.setEqualTo(position);
		boolean moved = false;
		Direction triedToMove = Direction.None;
		if (gameTime >= nextMoveAllowed && !isControlFrozen)
		{
			if (queuedMovement != Direction.None)
			{
				if (triedToMove == Direction.None)
					triedToMove = queuedMovement;
				moved = TryMove(queuedMovement);
				queuedMovement = Direction.None;
			}
			// Handle player movement direction.
			if (!moved && QueryWantsToMove(Direction.Up))
			{
				if (triedToMove == Direction.None)
					triedToMove = Direction.Up;
				moved = TryMove(Direction.Up);
			}
			if (!moved && QueryWantsToMove(Direction.Down))
			{
				if (triedToMove == Direction.None)
					triedToMove = Direction.Down;
				moved = TryMove(Direction.Down);
			}
			if (!moved && QueryWantsToMove(Direction.Left))
			{
				if (triedToMove == Direction.None)
					triedToMove = Direction.Left;
				moved = TryMove(Direction.Left);
			}
			if (!moved && QueryWantsToMove(Direction.Right))
			{
				if (triedToMove == Direction.None)
					triedToMove = Direction.Right;
				moved = TryMove(Direction.Right);
			}
			if (!moved && triedToMove != Direction.None)
			{
				// At least one move was attempted, but failed due to obstacle(s). We should consume the movement
				// attempt and animate the player anyway.
				// This provides the appearance of running into a wall and not going anywhere.
				nextMoveAllowed = GameTime.getGameTime() + (long) movementDelay;
				IncrementAnimationStep();
				Face(triedToMove);
			}
			if (moved && item.itemType == ItemType.AutoHammer
					&& Globals.inputManager.isActionButtonPressed(playerIndex))
				worldMap.EntityUseHammer(previousPosition);
			if (moved && worldMap.IsSlippery(position))
				SlippedFallDown(triedToMove);
		}
		else if (gameTime >= nextMoveAllowed && slipDirection != Direction.None)
		{
			moved = TryMove(slipDirection);
			if (moved && worldMap.IsSlippery(position))
				SlippedFallDown(slipDirection);
			nextMoveAllowed = GameTime.getGameTime() + (long) movementDelay;
		}
		if (!isUnableToAct)
		{
			// Handle player facing direction
			// For gamepad input, see which direction is held the farthest on the right stick and face that direction.
			// If no
			// direction, do not face.
			if (queuedFace != Direction.None)
			{
				Face(queuedFace);
				queuedFace = Direction.None;
			}
			Direction desiredFacingDirection = Globals.inputManager.getFacingDirection(this.playerIndex);
			if (desiredFacingDirection == Direction.Up && facing != Direction.Up)
				Face(Direction.Up);
			else if (desiredFacingDirection == Direction.Down && facing != Direction.Down)
				Face(Direction.Down);
			else if (desiredFacingDirection == Direction.Left && facing != Direction.Left)
				Face(Direction.Left);
			else if (desiredFacingDirection == Direction.Right && facing != Direction.Right)
				Face(Direction.Right);
		}

		PickupItemIfItemExists();

		// Handle Item Use
		if (useItem)
		{
			useItem = false;
			if (item.itemType == ItemType.Detonator)
				ExplodeMyBombs();
			else if (item.itemType == ItemType.Flamethrower && item.charges > 0 && !isControlFrozen(250))
			{
				if (!worldMap.EntityUseFlamethrower(this))
				{
					item.charges--; // Flamethrower use succeeded.
					FreezeMe((Explosion.explosionFadeTime * 8) + 1);
				}
			}
			else if (item.itemType == ItemType.Laser && item.charges > 0 && !isControlFrozen(250))
			{
				if (!worldMap.EntityUseLaser(this))
				{
					item.charges--; // Laser use succeeded.
					if (item.charges <= 0)
						useLaserSkin = false;
					FreezeMe((Explosion.explosionFadeTime * 8) + 1);
				}
			}
			else if (item.itemType == ItemType.Heatseeker
					&& item.charges > 0
					&& (gameTime >= lastRocketUsed + rocketFullDelay || (gameTime >= lastRocketUsed + rocketShortDelay && worldMap
							.PlayerHasNoRockets(this))))
			{
				lastRocketUsed = gameTime;
				item.charges--;
				worldMap.EntityUseHeatseeker(this);
			}
			else if (item.itemType == ItemType.Hammer)
				worldMap.EntityUseHammer(GetBlockInFrontOfMe());
			else if (item.itemType == ItemType.RemoteTeleporter && item.charges > 0)
			{
				item.charges--;
				worldMap.EntityUseRemoteTeleporter(this);
			}
			else if (item.itemType == ItemType.HealthMedicine2 && item.charges > 0)
			{
				if (health < 100 || diseaseType != DiseaseType.None)
				{
					if (!foundAngelOfDeath && health < 100)
						health = 100;
					diseaseType = DiseaseType.None;
					item.charges--;
					Globals.soundManager.PlaySound(SoundType.UseHeal);
				}
			}
			else if (item.itemType == ItemType.Banana && item.charges > 0)
			{
				if (!foundAngelOfDeath)
				{
					health += 20;
					if (health > 100)
						health = 100;
				}
				diseaseType = DiseaseType.None;
				item.charges--;
				Globals.soundManager.PlaySound(SoundType.UseHeal);
				worldMap.DropBananaPeel(this.position);
			}
		}

		UpdateEnd();
	}

	private boolean QueryWantsToMove(Direction dir)
	{
		if (Globals.inputManager.isDirectionPressed(dir, this.playerIndex))
			return true;
		long gameTime = GameTime.getGameTime();
		float moveAmount = Globals.inputManager.getAnalogMovementForce(this.playerIndex, dir);
		float deadZone = Globals.inputManager.getAnalogStickDeadZone(this.playerIndex);
		if (moveAmount > deadZone && gameTime >= nextMoveAllowed + (500f - (500f * moveAmount)))
			return true;
		return false;
	}

	public void Input_Bomb()
	{
		plantBomb = true;
	}

	public void Input_Use()
	{
		useItem = true;
	}

	public void Input_Back()
	{
		// useBack = true;
	}

	public int getPlayerIndex()
	{
		return playerIndex;
	}

	public void setKeyboardScheme(KeyboardScheme keyboardScheme)
	{
		kbScheme = keyboardScheme;
	}

	public KeyboardScheme getKeyboardScheme()
	{
		return kbScheme;
	}

	/**
	 * Queues a movement or direction facing command for the next update, even if the key is released before then.
	 * 
	 * @param direction
	 *            The direction to move or face.
	 * @param justLook
	 *            True if the command is to move, false if the player should just look that direction.
	 */
	public void QueueMove(Direction direction, boolean justLook)
	{
		if (!isControlFrozen())
		{
			if (justLook)
				queuedFace = direction; // NOTE: Queued facing is not used under the current hybrid event/polling driven
										// input scheme.
			else
				queuedMovement = direction;
		}
	}
}
