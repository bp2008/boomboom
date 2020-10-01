package com.brian.boomboom.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.BoomBoomGame;
import com.brian.boomboom.Globals;
import com.brian.boomboom.bomb.Bomb;
import com.brian.boomboom.bomb.BombManager;
import com.brian.boomboom.bomb.Explosion;
import com.brian.boomboom.bomb.ExplosionDirection;
import com.brian.boomboom.bomb.ExplosionEffect;
import com.brian.boomboom.bomb.ExplosionManager;
import com.brian.boomboom.bomb.ExplosionType;
import com.brian.boomboom.entity.AngelOfDeath;
import com.brian.boomboom.entity.Entity;
import com.brian.boomboom.entity.Player;
import com.brian.boomboom.gui.GuiState;
import com.brian.boomboom.input.Direction;
import com.brian.boomboom.item.DiseaseType;
import com.brian.boomboom.item.Item;
import com.brian.boomboom.item.ItemType;
import com.brian.boomboom.pathfinder.Path;
import com.brian.boomboom.rocket.Rocket;
import com.brian.boomboom.rocket.RocketManager;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.sound.SoundType;
import com.brian.boomboom.util.BBLog;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;

public class WorldMap
{
	public Square[][] worldMap;
	public Point[] spawnPositions;
	public List<Entity> entities;
	public Player[] players;
	public BlockLayout blockLayout;
	public int worldWidth;
	public int worldHeight;
	public Set<Bomb> activeBombs;
	public Set<Rocket> activeRockets;
	public Set<Explosion> explosions;

	public int numPlayersAlive;
	private boolean enabledRestartMessage = false;

	public Earthquake activeEarthquake = null;

	public AngelOfDeath angelOfDeath;

	public int worldIndex;

	public WorldMap(int width, int height, WorldTypes worldType, int worldIndex)
	{
		GameTime.reset();
		numPlayersAlive = Settings.numPlayers;
		this.worldIndex = worldIndex;

		Item.Reset(worldIndex);
		Globals.initWorldTexture(worldIndex);
		this.worldWidth = width;
		this.worldHeight = height;
		worldMap = new Square[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				worldMap[i][j] = new Square(i, j);

		blockLayout = new BlockLayout(width, height, worldType);
		try
		{
			spawnPositions = blockLayout.Initialize(worldMap, Settings.numPlayers);
			entities = new ArrayList<Entity>();
			players = new Player[Settings.numPlayers];
			for (int i = 0; i < Settings.numPlayers; i++)
			{
				players[i] = new Player(spawnPositions[i], this, i);
				entities.add(players[i]);
			}
			Globals.inputManager.setWorld(this);
		}
		catch (Exception e)
		{
			BBLog.debug(e, this);
		}
		activeBombs = new HashSet<Bomb>();
		activeRockets = new HashSet<Rocket>();
		explosions = new HashSet<Explosion>();
	}

	public void Unload(OrthographicCamera camera)
	{
		if (activeEarthquake != null)
			activeEarthquake.EndQuake(camera);
		activeEarthquake = null;
		Globals.inputManager.setWorld(null);
	}

	public void Draw(SpriteBatch batch)
	{
		// Make these variable local for a small potential optimization
		int worldWidth = this.worldWidth;
		int worldHeight = this.worldHeight;

		// Draw World and Items
		for (int i = 0; i < worldWidth; i++)
			for (int j = 0; j < worldHeight; j++)
				worldMap[i][j].Draw(batch, worldIndex);

		// Draw Entities
		for (int i = 0; i < entities.size(); i++)
			entities.get(i).Draw(batch);

		// Draw Rockets
		Iterator<Rocket> rocketIt = activeRockets.iterator();
		while (rocketIt.hasNext())
			rocketIt.next().Draw(batch);

		// Draw Bombs
		Iterator<Bomb> bombIt = activeBombs.iterator();
		while (bombIt.hasNext())
			bombIt.next().Draw(batch);

		// Draw Angel of Death
		if (angelOfDeath != null)
			angelOfDeath.Draw(batch);

		// Draw Explosions
		Iterator<Explosion> explosionIt = explosions.iterator();
		while (explosionIt.hasNext())
			explosionIt.next().Draw(batch);
	}

	public void Update()
	{
		// Check number of living players
		numPlayersAlive = 0;
		for (int i = 0; i < players.length; i++)
			if (players[i].health > 0)
				numPlayersAlive++;

		if (!enabledRestartMessage && numPlayersAlive < 2)
		{
			enabledRestartMessage = true;
			BoomBoomGame.gui.LoadState(GuiState.IngameNewGameMessage);
		}

		// Update world and items
		UpdateWorld();
		// Spread Diseases
		SpreadDiseases();
		// Update Entities - Players are first to be updated
		for (int i = 0; i < entities.size(); i++)
			entities.get(i).Update();
		// Update Angel of Death
		if (angelOfDeath != null)
		{
			angelOfDeath.Update();
			if (angelOfDeath.deleteMe)
				angelOfDeath = null;
		}
		// Update Rockets
		Iterator<Rocket> rocketIt = activeRockets.iterator();
		while (rocketIt.hasNext())
		{
			Rocket r = rocketIt.next();
			if (r.Update(this))
			{
				rocketIt.remove();
				RocketManager.returnRocketToPool(r);
			}
		}
		// Update Bombs
		Iterator<Bomb> bombIt = activeBombs.iterator();
		while (bombIt.hasNext())
		{
			Bomb b = bombIt.next();
			if (b.Update())
			{
				bombIt.remove();
				ExplodeBomb(b);
			}
		}
		// Update Explosions
		Iterator<Explosion> explosionIt = explosions.iterator();
		while (explosionIt.hasNext())
		{
			Explosion exp = explosionIt.next();
			exp.Update(this);
			if (exp.explosionStrength <= 0)
			{
				explosionIt.remove();
				RemoveExplosionFromWorld(exp);
			}
		}
	}

	private void UpdateWorld()
	{
		int xl = worldWidth;
		int yl = worldHeight;
		for (int i = 0; i < xl; i++)
		{
			for (int j = 0; j < yl; j++)
			{
				Square sq = worldMap[i][j];
				if (sq.hadFireLastUpdate && sq.fireCount <= 0)
				{
					if (sq.squareType == SquareTypes.WorldStandard || sq.squareType == SquareTypes.Hammered)
					{
						sq.squareType = SquareTypes.Empty;
						sq.item.GenerateRandomItem();
					}
				}
				if (sq.fireCount > 0 && sq.item.itemType != ItemType.None)
					sq.item.Recycle(ItemType.None);
				sq.hadFireLastUpdate = sq.fireCount > 0;
			}
		}
	}

	private void SpreadDiseases()
	{
		for (int i = 0; i < players.length; i++)
		{
			Player pl = players[i];
			if (pl.diseaseType != DiseaseType.None)
				for (int j = 0; j < players.length; j++)
					if (pl.position.Equals(players[j].position) && pl != players[j])
						players[j].InfectMe(pl.diseaseType);
		}
	}

	private void RemoveExplosionFromWorld(Explosion exp)
	{
		worldMap[exp.position.x][exp.position.y].fireCount--;
		ExplosionManager.returnExplosionToPool(exp);
	}

	public boolean PositionIsWalkable(Point newPosition)
	{
		if (newPosition.x < 0 || newPosition.y < 0 || newPosition.x >= worldWidth || newPosition.y >= worldHeight)
			return false;
		return worldMap[newPosition.x][newPosition.y].IsWalkable();
	}

	public boolean BombIsHere(Point newPosition)
	{
		if (newPosition.x < 0 || newPosition.y < 0 || newPosition.x >= worldWidth || newPosition.y >= worldHeight)
			return false;
		return worldMap[newPosition.x][newPosition.y].HasBomb();
	}

	public boolean BlockHasFire(Point position)
	{
		if (position.x < 0 || position.y < 0 || position.x >= worldWidth || position.y >= worldHeight)
			return false;
		return worldMap[position.x][position.y].fireCount > 0;
	}

	public boolean PositionCanReceiveKickedBomb(Point newPosition)
	{
		if (newPosition.x < 0 || newPosition.y < 0 || newPosition.x >= worldWidth || newPosition.y >= worldHeight)
			return false;
		return worldMap[newPosition.x][newPosition.y].IsWalkable() && !PlayerIsHere(newPosition)
				&& worldMap[newPosition.x][newPosition.y].item.itemType == ItemType.None;
	}

	public boolean TryKickBomb(Point proposed, Point delta, int kickStrength)
	{
		Bomb b = GetBombHere(proposed);
		if (b != null && PositionCanReceiveKickedBomb(proposed.Plus(delta)))
		{
			b.StartKick(delta, kickStrength);
			return true;
		}
		return false;
	}

	private boolean PlayerIsHere(Point newPosition)
	{
		for (int i = 0; i < players.length; i++)
			if (players[i].position.Equals(newPosition))
				return true;
		return false;
	}

	/**
	 * Explodes the specified bomb, removes it from the owning entity's active bomb set, removes it from the world
	 * square, and returns the bomb to the Bomb Manager. Before calling this, you should have already removed the bomb
	 * from the world's bomb set.
	 * 
	 * @param bomb
	 */
	public void ExplodeBomb(Bomb bomb)
	{
		bomb.StopMyFuseSound();
		if (bomb.owner.bombPower < 5)
			Globals.soundManager.PlaySound(SoundType.Firecracker);
		else
			Globals.soundManager.PlaySound(SoundType.Bomb);
		CreateExplosionBlocks(bomb);
		bomb.owner.RemoveBomb(bomb);
		// Do not remove from activeBombs -- this bomb has already been removed.
		NotifyBombRemoved(bomb.position.x, bomb.position.y);
		BombManager.returnBombToPool(bomb);
	}

	public void NotifyBombMoved(int oldX, int oldY, int newX, int newY, Bomb bomb)
	{
		worldMap[oldX][oldY].bomb = null;
		worldMap[newX][newY].bomb = bomb;
	}

	public void NotifyBombRemoved(int x, int y)
	{
		worldMap[x][y].bomb = null;
	}

	private void CreateExplosionBlocks(Bomb bomb)
	{
		// Add centerpoint
		ExplosionType type = bomb.owner.foundAngelOfDeath ? ExplosionType.Blue : ExplosionType.Normal;
		int power = bomb.owner.bombPower;
		Point pos = bomb.position.copy();
		explosions.add(ExplosionManager.getExplosion(power, pos, ExplosionDirection.Both, type));
		worldMap[pos.x][pos.y].fireCount++;
		int i;
		// Look up
		for (i = bomb.position.y - 1; i >= 0; i--)
		{
			pos.y = i;
			if (CreateSecondaryExplosion(--power, ExplosionDirection.Vertical, type, pos))
				break;
		}
		// Look down
		power = bomb.owner.bombPower;
		pos = bomb.position.copy();
		for (i = bomb.position.y + 1; i < worldHeight; i++)
		{
			pos.y = i;
			if (CreateSecondaryExplosion(--power, ExplosionDirection.Vertical, type, pos))
				break;
		}
		// Look left
		power = bomb.owner.bombPower;
		pos = bomb.position.copy();
		for (i = bomb.position.x - 1; i >= 0; i--)
		{
			pos.x = i;
			if (CreateSecondaryExplosion(--power, ExplosionDirection.Horizontal, type, pos))
				break;
		}

		// Look right
		power = bomb.owner.bombPower;
		pos = bomb.position.copy();
		for (i = bomb.position.x + 1; i < worldWidth; i++)
		{
			pos.x = i;
			if (CreateSecondaryExplosion(--power, ExplosionDirection.Horizontal, type, pos))
				break;
		}
	}

	/**
	 * Tries to create an explosion at the specified location and returns true if the explosion line should end after
	 * this one.
	 * 
	 * @param i
	 * @param horizontal
	 * @param explosionType
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean CreateSecondaryExplosion(int explosionPower, ExplosionDirection direction,
			ExplosionType explosionType, Point position)
	{
		ExplosionEffect eff = Explosion.blockCanBeAffectedByExplosion(worldMap[position.x][position.y]);
		if (eff == ExplosionEffect.NoEffect)
			return true;
		explosions.add(ExplosionManager.getExplosion(explosionPower, position, direction, explosionType));
		worldMap[position.x][position.y].fireCount++;
		if (eff == ExplosionEffect.ThisButNoFurther)
			return true;
		return false;
	}

	public Bomb AddBomb(Point position, Entity owner)
	{
		Globals.soundManager.PlaySound(SoundType.ItemOther);
		Bomb b = BombManager.getBomb(position, owner);
		activeBombs.add(b);
		worldMap[position.x][position.y].bomb = b;
		return b;
	}

	public void DoExplosionDamage(Point position, int explosionStrength, ExplosionType explosionType)
	{
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i).position.Equals(position))
			{
				if (entities.get(i).foundAngelOfDeath
						&& (explosionType == ExplosionType.Angel || explosionType == ExplosionType.Blue))
					continue;
				entities.get(i).TakeDamage(explosionStrength, explosionType);
			}
		}
	}

	public Bomb GetBombHere(Point position)
	{
		if (position.x < 0 || position.y < 0 || position.x >= worldWidth || position.y >= worldHeight)
			return null;
		return worldMap[position.x][position.y].bomb;
	}

	private Rocket GetRocketHere(Point position)
	{
		Iterator<Rocket> rocketIt = activeRockets.iterator();
		while (rocketIt.hasNext())
		{
			Rocket r = rocketIt.next();
			if (r.position.Equals(position))
				return r;
		}
		return null;
	}

	public Item GetItem(Point position)
	{
		return worldMap[position.x][position.y].item;
	}

	public void RemoveItem(Point position)
	{
		worldMap[position.x][position.y].item.Recycle(ItemType.None);
	}

	public void PlayerTakeItem(Entity entity, Point position)
	{
		entity.item.Copy(worldMap[position.x][position.y].item);
		entity.useLaserSkin = entity.item.itemType == ItemType.Laser;
		worldMap[position.x][position.y].item.Recycle(ItemType.None);
		entity.ConvertMyBombs();
	}

	public void CreateAngelOfDeath(Entity player)
	{
		player.foundAngelOfDeath = true;
		Globals.soundManager.PlaySound(SoundType.AngelOfDeathArrive);
		Entity enemy = FindAnyEnemyOf(player);
		if (enemy != null)
			angelOfDeath = new AngelOfDeath(this, enemy.position);
	}

	/**
	 * Returns true if the player has no active rockets on the map.
	 * 
	 * @param player
	 * @return
	 */
	public boolean PlayerHasNoRockets(Entity entity)
	{
		Iterator<Rocket> rocketIt = activeRockets.iterator();
		while (rocketIt.hasNext())
			if (rocketIt.next().owner == entity)
				return false;
		return true;
	}

	public void EntityUseHammer(Point hammerLocation)
	{
		if (hammerLocation.x < 0 || hammerLocation.y < 0 || hammerLocation.x >= worldWidth
				|| hammerLocation.y >= worldHeight)
			return;

		Square sq = worldMap[hammerLocation.x][hammerLocation.y];
		if (sq.item.itemType != ItemType.None || BombIsHere(hammerLocation) || PlayerIsHere(hammerLocation))
			return;
		// Explode all rockets on this square.
		Rocket r = GetRocketHere(hammerLocation);
		boolean rocketExploded = false;
		while (r != null)
		{
			ExplodeRocket(r);
			activeRockets.remove(r);
			RocketManager.returnRocketToPool(r);
			rocketExploded = true;
			r = GetRocketHere(hammerLocation);
		}
		if (rocketExploded)
		{
			sq.item.GenerateRandomItem();
			return;
		}
		if (sq.squareType == SquareTypes.Empty)
		{
			Globals.soundManager.PlaySound(SoundType.HammerCreate);
			sq.squareType = SquareTypes.Hammered;
			// Globals.lighting.AddHull(hammerLocation.x, hammerLocation.y, sq.squareType);
		}
		else if (sq.squareType == SquareTypes.Hammered)
		{
			Globals.soundManager.PlaySound(SoundType.HammerDestroy);
			sq.squareType = SquareTypes.Empty;
			// Globals.lighting.RemoveHull(hammerLocation.x, hammerLocation.y);
		}
		else if (sq.squareType == SquareTypes.WorldStandard)
		{
			Globals.soundManager.PlaySound(SoundType.HammerDestroyWorldStandard);
			sq.squareType = SquareTypes.Empty;
			// Globals.lighting.RemoveHull(hammerLocation.x, hammerLocation.y);
		}
	}

	public void EntityUseHeatseeker(Player player)
	{
		Entity target = FindAnyEnemyOf(player);
		if (target == null)
			return;
		Rocket rocket = RocketManager.getRocket(player, target);
		activeRockets.add(rocket);
	}

	public boolean EntityUseFlamethrower(Entity entity)
	{
		return EntityUseBeamWeapon(entity, false);
	}

	public boolean EntityUseLaser(Entity entity)
	{
		return EntityUseBeamWeapon(entity, true);
	}

	/**
	 * Uses the specified beam weapon from the position of the entity in the direction the entity is facing. If usage
	 * failed completely because the beam was blocked at the source, true is returned. If at least one explosion block
	 * was created, false is returned.
	 * 
	 * @param entity
	 * @param isLaser
	 * @return
	 */
	private boolean EntityUseBeamWeapon(Entity entity, boolean isLaser)
	{
		Point position = entity.position.copy();
		Point delta;
		ExplosionDirection dir = ExplosionDirection.Horizontal;
		if (entity.facing == Direction.Up)
		{
			dir = ExplosionDirection.Vertical;
			delta = new Point(0, 1);
		}
		else if (entity.facing == Direction.Down)
		{
			dir = ExplosionDirection.Vertical;
			delta = new Point(0, -1);
		}
		else if (entity.facing == Direction.Left)
			delta = new Point(-1, 0);
		else
			delta = new Point(1, 0);

		int beamLength = isLaser ? 8 : 4;
		for (int i = 0; i < beamLength; i++)
		{
			position.Add(delta);
			if (position.x < 0 || position.y < 0 || position.x >= worldWidth || position.y >= worldHeight)
			{
				if (i == 0)
					return true;
				break;
			}
			ExplosionEffect eff = Explosion.blockCanBeAffectedByExplosion(worldMap[position.x][position.y]);
			if (eff == ExplosionEffect.NoEffect)
			{
				if (i == 0)
					return true;
				break;
			}
			explosions.add(ExplosionManager.getExplosion(8, position, dir, isLaser ? ExplosionType.Laser
					: ExplosionType.Flamethrower));
			worldMap[position.x][position.y].fireCount++;
			if (eff == ExplosionEffect.ThisButNoFurther)
				break;
		}
		if (isLaser)
			Globals.soundManager.PlaySound(SoundType.Zap);
		else
			Globals.soundManager.PlaySound(SoundType.Firecracker);
		return false;
	}

	public void EntityUseRemoteTeleporter(Entity entity)
	{
		if (entity.position.Equals(entity.spawnPoint))
		{
			// Already at spawn point. Try to move player to a random enemy's spawn point.
			Entity randomEnemy = FindAnyEnemyOf(entity);
			if (randomEnemy == null)
				return;
			MoveEntityToNearestLocation(entity, randomEnemy.spawnPoint);
		}
		else
			MoveEntityToNearestLocation(entity, entity.spawnPoint);
	}

	private void MoveEntityToNearestLocation(Entity entity, Point loc)
	{
		if (worldMap[loc.x][loc.y].squareType == SquareTypes.Empty)
			entity.position = loc.copy();
		else
		{
			entity.position = FindNearestOpenLocation(loc.copy());
		}
	}

	private Point FindNearestOpenLocation(Point referenceLocation)
	{
		Point locationTemp = new Point(0, 0);
		Point locationNearest = referenceLocation.copy();
		int distanceNearest = Integer.MAX_VALUE;
		for (int i = 0; i < worldWidth; i++)
			for (int j = 0; j < worldHeight; j++)
				if (worldMap[i][j].squareType == SquareTypes.Empty)
				{
					locationTemp.x = i;
					locationTemp.y = j;
					int distance = referenceLocation.IntDistanceFrom(locationTemp);
					if (distance < distanceNearest)
					{
						locationNearest = locationTemp.copy();
						distanceNearest = distance;
					}
				}
		return locationNearest;
	}

	private Entity FindAnyEnemyOf(Entity entity)
	{
		List<Entity> targetCandidates = new ArrayList<Entity>();
		for (int i = 0; i < players.length; i++)
			if (players[i] != entity && players[i].health > 0)
				targetCandidates.add(players[i]);
		if (targetCandidates.size() == 0)
			return null;
		return targetCandidates.get(Globals.rndGenerator.nextInt(targetCandidates.size()));
	}

	public boolean RocketMoved(Rocket rocket)
	{
		Bomb bomb = GetBombHere(rocket.position);
		if (bomb != null)
		{
			ExplodeRocket(rocket, bomb);
			return true;
		}
		if (BlockHasFire(rocket.position))
		{
			ExplodeRocket(rocket);
			return true;
		}
		Square sq = worldMap[rocket.position.x][rocket.position.y];
		if (sq.squareType == SquareTypes.Hammered || sq.squareType == SquareTypes.WorldStandard
				|| (sq.squareType == SquareTypes.Empty && sq.item.itemType != ItemType.None))
		{
			ExplodeRocket(rocket);
			return true;
		}
		// Iterate through the entities and explode the rocket if the rocket is over any enemy entity.
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i) != rocket.owner && entities.get(i).position.Equals(rocket.position))
			{
				ExplodeRocket(rocket);
				return true;
			}
		}
		return false;
	}

	public void ExplodeRocket(Rocket rocket)
	{
		ExplodeRocket(rocket, null);
	}

	private void ExplodeRocket(Rocket rocket, Bomb bomb)
	{
		Globals.soundManager.PlaySound(SoundType.Bomb);
		Square sq = worldMap[rocket.position.x][rocket.position.y];
		if (sq.squareType == SquareTypes.WorldStandard || sq.squareType == SquareTypes.Hammered)
		{
			sq.squareType = SquareTypes.Empty;
			sq.item.GenerateRandomItem();
		}
		else if (sq.squareType == SquareTypes.Empty && sq.item.itemType != ItemType.None)
		{
			sq.item.Recycle(ItemType.None);
		}
		if (bomb != null)
		{
			activeBombs.remove(bomb);
			ExplodeBomb(bomb);
		}
		// Damage all entities in the rocket's position
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i).position.Equals(rocket.position))
				entities.get(i).TakeDamage(Settings.rocketDamage, ExplosionType.Rocket);
		}
	}

	public void PlayFootstepSound(Point position)
	{
		switch (worldIndex)
		{
			case Levels.Snow:
				Globals.soundManager.PlaySound(SoundType.FootstepsSnow);
				break;
			case Levels.Forest:
				Globals.soundManager.PlaySound(SoundType.FootstepsGrass);
				break;
			case Levels.Lava:
				Globals.soundManager.PlaySound(SoundType.FootstepsHardConcrete);
				break;
			case Levels.Desert:
			case Levels.Rocks:
				Globals.soundManager.PlaySound(SoundType.FootstepsGravel);
				break;
			case 11:
				Globals.soundManager.PlaySound(SoundType.FootstepsSoftConcrete);
				break;
			case 12:
				Globals.soundManager.PlaySound(SoundType.FootstepsWood);
				break;
			case Levels.Retro:
			default:
				Globals.soundManager.PlaySound(SoundType.FootstepsTile);
				break;
		}
	}

	public void AngelFire(AngelOfDeath angelOfDeath)
	{
		Globals.soundManager.PlaySound(SoundType.AngelFire);
		int xMin = angelOfDeath.target.x - 2;
		if (xMin < 0)
			xMin = 0;
		int xMax = angelOfDeath.target.x + 2;
		if (xMax >= worldWidth)
			xMax = worldWidth - 1;
		int yMin = 0;
		int yMax = worldHeight - 1;
		for (int x = xMin; x <= xMax; x++)
			for (int y = yMin; y <= yMax; y++)
			{
				worldMap[x][y].squareType = SquareTypes.Empty;
				worldMap[x][y].item = new Item(ItemType.None);
				worldMap[x][y].fireCount++;
				explosions.add(ExplosionManager.getExplosion(8, new Point(x, y), ExplosionDirection.Vertical,
						ExplosionType.Angel));
			}
	}

	public void AuraDamageOtherPlayersHere(Entity cause)
	{
		for (Entity ent : entities)
			if (ent != cause && ent.position.Equals(cause.position))
				ent.TakeDamage(2, ExplosionType.Aura);
	}

	public void SwapPlayerWithOtherLivingPlayer(Entity entity)
	{
		ArrayList<Entity> entsNotSharingPosition = new ArrayList<Entity>();
		for (Entity ent : entities)
			if (ent != entity && !ent.position.Equals(entity.position) && ent.health > 0)
				entsNotSharingPosition.add(ent);
		if (entsNotSharingPosition.size() > 0)
		{
			int index = Globals.rndGenerator.nextInt(entsNotSharingPosition.size());
			entsNotSharingPosition.get(index).position.Swap(entity.position);
		}
	}

	/**
	 * Freezes a player sharing your position. Returns true if the Popsicle or TwinPop is used and a player is frozen.
	 * 
	 * @param entity
	 *            The player with the Popsicle or TwinPop item.
	 * @return
	 */
	public boolean PopsicleEffectOtherPlayerHere(Entity entity)
	{
		ArrayList<Entity> entsSharingPosition = new ArrayList<Entity>();
		for (Entity ent : entities)
			if (ent != entity && ent.position.Equals(entity.position) && ent.health > 0)
				entsSharingPosition.add(ent);
		if (entsSharingPosition.size() > 0)
		{
			int index = Globals.rndGenerator.nextInt(entsSharingPosition.size());
			entsSharingPosition.get(index).PopsicleFreezeMe(entity.item.itemType);
			return true;
		}
		return false;
	}

	public void DropBananaPeel(Point position)
	{
		worldMap[position.x][position.y].hasBananaPeel = true;
	}

	public void Earthquake()
	{
		// Detonate Rockets
		Iterator<Rocket> rocketIt = activeRockets.iterator();
		while (rocketIt.hasNext())
		{
			Rocket r = rocketIt.next();
			rocketIt.remove();
			RocketManager.returnRocketToPool(r);
		}

		if (activeEarthquake == null)
		{
			activeEarthquake = new Earthquake(this);
			Globals.soundManager.PlaySound(SoundType.Earthquake);
		}
	}

	/**
	 * This is called by an Earthquake when it is time to rearrange the map.
	 */
	public void EarthquakeRandomize()
	{
		int attempts = 0;
		do
		{
			EarthquakeRandomizeInner();
		}
		while (++attempts < 25 && !LivingPlayersCanReachEachOther());
	}

	private void EarthquakeRandomizeInner()
	{
		int xl = worldWidth;
		int yl = worldHeight;
		for (int i = 0; i < xl; i++)
		{
			for (int j = 0; j < yl; j++)
			{
				int tx = Globals.rndGenerator.nextInt(xl), ty = Globals.rndGenerator.nextInt(yl);
				if (tx == i && ty == j)
					continue; // Don't bother swapping if the source position matches the target position.
				// Swap the squares
				Square tempSquare = worldMap[i][j];
				worldMap[i][j] = worldMap[tx][ty];
				worldMap[tx][ty] = tempSquare;

				// Swap the square positions
				Point tempPosition = worldMap[i][j].position.copy();
				worldMap[i][j].position.setEqualTo(worldMap[tx][ty].position);
				worldMap[tx][ty].position.setEqualTo(tempPosition);

				// Update the bomb positions
				if (worldMap[i][j].HasBomb())
					worldMap[i][j].bomb.position.setEqualTo(worldMap[i][j].position);
				if (worldMap[tx][ty].HasBomb())
					worldMap[tx][ty].bomb.position.setEqualTo(worldMap[tx][ty].position);

				// Move the explosions
				Iterator<Explosion> explosionIt = explosions.iterator();
				while (explosionIt.hasNext())
				{
					Explosion exp = explosionIt.next();
					if (exp.position.Equals(worldMap[i][j].position))
						exp.position.setEqualTo(worldMap[tx][ty].position);
					else if (exp.position.Equals(worldMap[tx][ty].position))
						exp.position.setEqualTo(worldMap[i][j].position);
				}

				// Move the entities
				for (int n = 0; n < players.length; n++)
				{
					if (players[n].position.Equals(worldMap[i][j].position))
						players[n].position.setEqualTo(worldMap[tx][ty].position);
					else if (players[n].position.Equals(worldMap[tx][ty].position))
						players[n].position.setEqualTo(worldMap[i][j].position);
				}
			}
		}
	}

	private boolean LivingPlayersCanReachEachOther()
	{
		// Create living players list
		ArrayList<Entity> playersLiving = new ArrayList<Entity>();
		for (int n = 0; n < players.length; n++)
		{
			if (players[n].health > 0)
				playersLiving.add(players[n]);
		}

		if (playersLiving.size() < 2)
			return true; // There are less than 2 players alive; path finding not necessary.
		for (int n = 1; n < playersLiving.size(); n++)
		{
			ArrayList<Point> points = Path.PathFind(this, playersLiving.get(0).position, playersLiving.get(n).position);
			if (points.size() == 1 && points.get(0).Equals(playersLiving.get(0).position))
				return false; // No path found
		}
		// If we get here, a path was found between all living players.
		return true;
	}

	public boolean IsSlippery(Point position)
	{
		if (worldMap[position.x][position.y].hasBananaPeel)
		{
			worldMap[position.x][position.y].hasBananaPeel = false;
			return true;
		}
		return false;
	}
}
