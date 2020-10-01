package com.brian.boomboom.entity;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.Globals;
import com.brian.boomboom.bomb.Bomb;
import com.brian.boomboom.bomb.ExplosionType;
import com.brian.boomboom.input.Direction;
import com.brian.boomboom.item.DiseaseType;
import com.brian.boomboom.item.Item;
import com.brian.boomboom.item.ItemType;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.sound.SoundType;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.WorldMap;

public abstract class Entity
{
	public Point spawnPoint;
	public Point position;
	public WorldMap worldMap;
	public Item item = new Item(ItemType.None);
	protected long lastRocketUsed = 0;
	/**
	 * Player can fire another rocket after this long IF he has NO other live rockets.
	 */
	public static long rocketShortDelay = 3000;
	/**
	 * Player can fire another rocket after this long EVEN IF he HAS other live rockets.
	 */
	public static long rocketFullDelay = 3000;
	protected static long auraDelay = 50;
	protected long nextAuraDamage = 0;
	// Bomb objects
	protected Set<Bomb> myActiveBombs = new HashSet<Bomb>();
	public int bombPower = 3;
	protected int bombCount = 1;
	protected int kickStrength = 0;
	public boolean foundAngelOfDeath = false;
	public DiseaseType diseaseType = DiseaseType.None;
	public boolean useLaserSkin;
	public int health = 1;
	protected boolean isPopsicleFrozen = false;
	protected boolean isEarthquakeFrozen = false;
	
	private int slipFreezeTime = 2500;
	public Direction slipDirection = Direction.None;

	// Movement objects
	protected long freezeUntil = 0;
	/**
	 * Game time that the player last moved.
	 */
	protected long nextMoveAllowed = 0;
	/**
	 * Time in ms between moves of the player and of rockets fired by the player.
	 */
	public double movementDelay = 200;
	protected Point positionProposed = new Point(0, 0);

	// Animation objects
	public Direction facing = Direction.Down;
	protected int animationStep = 1;
	protected boolean animationStepGoingBackwards = false;
	protected int animationFrameIndex = 7;
	protected long contractedDisease = -1000000;

	public Entity(Point position, WorldMap worldMap)
	{
		this.spawnPoint = position.copy();
		this.position = position.copy();
		this.worldMap = worldMap;
		this.health = Settings.entityHealthDefault;
		this.bombCount = Settings.iStartingBombCount;
		for (int i = 1; i < Settings.iRunSpeedDefault; i++)
		{
			this.movementDelay *= 0.9;
		}
	}

	public abstract void Draw(SpriteBatch batch);

	public abstract void Update();

	public void UpdateStart()
	{
		if (health <= 0)
		{
			IAmDead();
			return;
		}

		// Handle Aura Damage
		if (foundAngelOfDeath && GameTime.getGameTime() >= nextAuraDamage)
		{
			nextAuraDamage = GameTime.getGameTime() + auraDelay;
			worldMap.AuraDamageOtherPlayersHere(this);
		}

		// Handle popsicle freeze
		if ((item.itemType == ItemType.Popsicle || item.itemType == ItemType.TwinPop)
				&& worldMap.PopsicleEffectOtherPlayerHere(this))
			item.Recycle(ItemType.None);

		// Handle Disease Expiration
		if (diseaseType != DiseaseType.None
				&& GameTime.getGameTime() >= contractedDisease + Item.DiseaseTime(diseaseType))
			diseaseType = DiseaseType.None;
	}

	public void UpdateEnd()
	{
	}

	protected void IAmDead()
	{
		health = 0;
		animationFrameIndex = GetPlayerAnimationFrame();
	}

	public void RemoveBomb(Bomb bomb)
	{
		myActiveBombs.remove(bomb);
	}

	public void TakeDamage(int amount, ExplosionType explosionType)
	{
		if (health <= 0)
			return;
		boolean isHolyDamage = explosionType == ExplosionType.Aura || explosionType == ExplosionType.Angel;
		if (isHolyDamage && item.itemType == ItemType.HolyChalice)
			return;
		int maxDamageAtOnce = Settings.iMaxDamageAtOnce;
		if (explosionType == ExplosionType.Angel)
		{
			amount *= 2;
			maxDamageAtOnce = (int) (amount + 5);
		}
		Globals.soundManager.PlaySound(SoundType.Hurt);
		if (explosionType == ExplosionType.Rocket)
		{
		}
		// else if (Settings.bClassicMode)
		// amount = 2.084;
		else
			amount = Math.min(amount, maxDamageAtOnce);
		// TODO: Vibrate controller
		// if (amount > 0)
		// VibrationManager.Damage(this.playerIndex);
		health -= amount;
		if (health < 0)
			health = 0;
	}

	public void PickupItemIfItemExists()
	{
		Item itemOnSquare = worldMap.GetItem(position);
		if (itemOnSquare.itemType == ItemType.None)
			return;
		switch (itemOnSquare.itemType)
		{
			case Detonator:
				if (foundAngelOfDeath)
					worldMap.RemoveItem(position);
				else
					worldMap.PlayerTakeItem(this, position);
				break;
			case Flamethrower:
			case Laser:
			case Heatseeker:
			case Hammer:
			case HolyChalice:
			case RemoteTeleporter:
			case Shortfuse:
			case Randomfuse:
			case LineBomb:
			case AutoHammer:
			case Popsicle:
			case TwinPop:
			case Banana:
				worldMap.PlayerTakeItem(this, position);
				break;
			case HealthMedicine2:
				if (!foundAngelOfDeath && health < 100)
					health = 100;
				diseaseType = DiseaseType.None;
				worldMap.PlayerTakeItem(this, position);
				Globals.soundManager.PlaySound(SoundType.ItemHeal);
				return;
			case HealthMedicine:
				if (!foundAngelOfDeath)
					health = 100;
				useLaserSkin = false;
				diseaseType = DiseaseType.None;
				worldMap.RemoveItem(position);
				item.Recycle(ItemType.None);
				ConvertMyBombs();
				Globals.soundManager.PlaySound(SoundType.ItemHeal);
				return;
			case Pizza:
				if (!foundAngelOfDeath)
				{
					health += 20;
					if (health > 100)
						health = 100;
				}
				diseaseType = DiseaseType.None;
				// 1% chance increasing by 1% for every 6 seconds the pizza has been sitting there
				int rnd = Math.abs(Globals.rndGenerator.nextInt()) % 100;
				int age = (int) (GameTime.getGameTime() - itemOnSquare.spawnTime);
				int contaminationLevel = age / 6000;
				if (rnd <= contaminationLevel)
					InfectMe(DiseaseType.Diarrhea); // You got food poisoning!!
				worldMap.RemoveItem(position);
				Globals.soundManager.PlaySound(SoundType.ItemHeal);
				return;
			case Bomb:
				bombCount++;
				worldMap.RemoveItem(position);
				break;
			case Power:
				bombPower++;
				worldMap.RemoveItem(position);
				break;
			case Foot:
				kickStrength++;
				worldMap.RemoveItem(position);
				break;
			case Rollerskates:
				movementDelay *= 0.9;
				worldMap.RemoveItem(position);
				break;
			case Disease:
				InfectMe(DiseaseType.Diarrhea);
				worldMap.RemoveItem(position);
				break;
			case AngelOfDeath:
				worldMap.CreateAngelOfDeath(this);
				worldMap.RemoveItem(position);
				if (this.item.itemType == ItemType.Detonator)
					this.item.itemType = ItemType.None;
				ConvertMyBombs();
				return; // return makes it not play the ItemOther sound
			case SwapPortal:
				worldMap.RemoveItem(position);
				worldMap.SwapPlayerWithOtherLivingPlayer(this);
				return;
			case Earthquake:
				worldMap.RemoveItem(position);
				worldMap.Earthquake();
				return;
			default:
				break;
		}
		Globals.soundManager.PlaySound(SoundType.ItemOther);
	}

	public void InfectMe(DiseaseType disease)
	{
		if (diseaseType != DiseaseType.None)
			return; // Already have a disease -- cannot have two diseases.
		diseaseType = disease;
		contractedDisease = GameTime.getGameTime();
	}

	public void ConvertMyBombs()
	{
		for (Bomb b : myActiveBombs)
			b.ConvertBomb(false);
	}

	public void ExplodeMyBombs()
	{
		for (Bomb b : myActiveBombs)
			b.FlagForImmediateDetonation();
	}

	protected Point GetForwardDelta()
	{
		if (facing == Direction.Up)
			return new Point(0, 1);
		if (facing == Direction.Down)
			return new Point(0, -1);
		if (facing == Direction.Left)
			return new Point(-1, 0);
		return new Point(1, 0);
	}

	/**
	 * Tries to move the player to the specified position, returning true if successful.
	 * 
	 * @param newPosition
	 *            The location to move to.
	 * @return true if the move was successful
	 */
	protected boolean TryMove(Point newPosition)
	{
		worldMap.PlayFootstepSound(position);
		if (worldMap.PositionIsWalkable(newPosition)
				|| (this.kickStrength > 0 && worldMap.TryKickBomb(newPosition, newPosition.Minus(position),
						kickStrength)))
		{
			position.x = newPosition.x;
			position.y = newPosition.y;
			nextMoveAllowed = GameTime.getGameTime() + (long) movementDelay;
			IncrementAnimationStep();
			return true;
		}
		return false;
	}

	/**
	 * Tries to move the player in the specified direction, returning true if successful.
	 * 
	 * @param direction
	 *            The direction to move in.
	 * @return true if the move was successful
	 */
	protected boolean TryMove(Direction direction)
	{
		if (direction == Direction.Up)
		{
			positionProposed.x = position.x;
			positionProposed.y = position.y + 1;
		}
		else if (direction == Direction.Down)
		{
			positionProposed.x = position.x;
			positionProposed.y = position.y - 1;
		}
		else if (direction == Direction.Left)
		{
			positionProposed.x = position.x - 1;
			positionProposed.y = position.y;
		}
		else if (direction == Direction.Right)
		{
			positionProposed.x = position.x + 1;
			positionProposed.y = position.y;
		}
		boolean success = TryMove(positionProposed);
		if (success)
			Face(direction);
		return success;
	}

	protected int GetPlayerAnimationFrame()
	{
		if (health <= 0)
			return 12;
		if (facing == Direction.Up)
			return 0 + animationStep;
		else if (facing == Direction.Right)
			return 3 + animationStep;
		else if (facing == Direction.Down)
			return 6 + animationStep;
		else
			return 9 + animationStep;
	}

	protected void IncrementAnimationStep()
	{
		if (animationStepGoingBackwards)
			animationStep--;
		else
			animationStep++;
		if (animationStep < 1 || animationStep > 1)
			animationStepGoingBackwards = !animationStepGoingBackwards;
		animationFrameIndex = GetPlayerAnimationFrame();
	}

	protected void Face(Direction newDirection)
	{
		facing = newDirection;
		animationFrameIndex = GetPlayerAnimationFrame();
	}

	protected Point GetBlockInFrontOfMe()
	{
		if (facing == Direction.Up)
		{
			positionProposed.x = position.x;
			positionProposed.y = position.y + 1;
		}
		else if (facing == Direction.Down)
		{
			positionProposed.x = position.x;
			positionProposed.y = position.y - 1;
		}
		else if (facing == Direction.Left)
		{
			positionProposed.x = position.x - 1;
			positionProposed.y = position.y;
		}
		else if (facing == Direction.Right)
		{
			positionProposed.x = position.x + 1;
			positionProposed.y = position.y;
		}
		return positionProposed;
	}

	protected boolean isControlFrozen()
	{
		return GameTime.getGameTime() < freezeUntil;
	}

	protected boolean isControlFrozen(int timeOffset)
	{
		return GameTime.getGameTime() < freezeUntil + timeOffset;
	}

	public void PopsicleFreezeMe(ItemType itemType)
	{
		Globals.soundManager.PlaySound(SoundType.Freeze);
		if (itemType == ItemType.Popsicle)
			FreezeMe(2500);
		else
			// if (itemType == itemType.TwinPop)
			FreezeMe(4000);
		isPopsicleFrozen = true;
	}

	public void SlippedFallDown(Direction directionJustMoved)
	{
		Globals.soundManager.PlaySound(SoundType.Slip);
		FreezeMe(slipFreezeTime);
		slipDirection = directionJustMoved;
	}

	public void FreezeMe(int timeMs)
	{
		isPopsicleFrozen = isEarthquakeFrozen = false;
		freezeUntil = timeMs + GameTime.getGameTime();
	}
}
