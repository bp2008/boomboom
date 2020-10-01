package com.brian.boomboom.bomb;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brian.boomboom.Globals;
import com.brian.boomboom.entity.Entity;
import com.brian.boomboom.item.ItemType;
import com.brian.boomboom.sound.SoundIdentifier;
import com.brian.boomboom.sound.SoundType;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;

public class Bomb
{
	public Point position;
	public Entity owner;
	private long msPerAnimationTick = 250;
	private long nextAnimationFrameUpdate;
	private int frame;
	private boolean animationReverse;
	private long fuseTime;
	private long fuseStartedTime;
	private SoundIdentifier myFuseSound = null;
	public ItemType bombType;
	public long nextKickMove;
	public long timeBetweenKickMoves = 200;
	public Point kickDelta = new Point(0, 0);
	private Point newPosition = new Point(0, 0);

	public Bomb(Point position, Entity owner)
	{
		Recycle(position, owner);
	}

	private void Initialize()
	{
		StopMyFuseSound();
		nextAnimationFrameUpdate = 0;
		frame = 1;
		animationReverse = false;
		kickDelta.x = kickDelta.y = 0;
		nextKickMove = -10000;
		bombType = ItemType.None;
		ConvertBomb(true);
	}

	public boolean Update()
	{
		if ((fuseTime != Long.MAX_VALUE && fuseStartedTime + fuseTime <= GameTime.getGameTime())
				|| owner.worldMap.BlockHasFire(position))
			return true;
		else if ((kickDelta.x != 0 || kickDelta.y != 0) && nextKickMove <= GameTime.getGameTime())
		{
			newPosition.x = position.x + kickDelta.x;
			newPosition.y = position.y + kickDelta.y;
			if (owner.worldMap.PositionCanReceiveKickedBomb(newPosition))
			{
				owner.worldMap.NotifyBombMoved(position.x, position.y, newPosition.x, newPosition.y, this);
				position.x = newPosition.x;
				position.y = newPosition.y;
				nextKickMove = GameTime.getGameTime() + timeBetweenKickMoves;
			}
			else
				kickDelta.x = kickDelta.y = 0;
		}
		return false;
	}

	public void Draw(SpriteBatch batch)
	{
		if (nextAnimationFrameUpdate <= GameTime.getGameTime())
		{
			IncrementAnimationFrame();
			nextAnimationFrameUpdate = GameTime.getGameTime() + msPerAnimationTick;
		}
		if (this.bombType == ItemType.Detonator)
			batch.draw(Globals.detonatorTexture[frame], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
		else
			batch.draw(Globals.bombTexture[frame], position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
	}

	public void ConvertBomb(boolean overrideFlag)
	{
		ItemType newBombType = owner.item.itemType;
		if (overrideFlag)
		{
			SetFuseTime(newBombType);
			return;
		}
		// Return if this action would not change the bomb type
		if (newBombType == this.bombType)
			return;

		// We only care if we are changing to or from Detonator
		if (this.bombType == ItemType.Detonator || newBombType == ItemType.Detonator)
			SetFuseTime(newBombType);
	}

	private void SetFuseTime(ItemType itemType)
	{
		this.bombType = itemType;
		if (bombType == ItemType.Randomfuse)
			fuseTime = Globals.nextInt(750, 3001);
		else if (bombType == ItemType.Shortfuse)
			fuseTime = 1500;
		else if (bombType == ItemType.Detonator)
			fuseTime = Long.MAX_VALUE;
		else
			fuseTime = 3000;
		fuseStartedTime = GameTime.getGameTime();

		if (bombType != ItemType.Detonator)
			myFuseSound = Globals.soundManager.PlaySound(SoundType.Fuse);
		else
			StopMyFuseSound();
	}

	public void StopMyFuseSound()
	{
		Globals.soundManager.StopSound(myFuseSound);
	}

	private void IncrementAnimationFrame()
	{
		if (animationReverse)
			frame--;
		else
			frame++;
		if (frame < 1 || frame > 1)
			animationReverse = !animationReverse;
	}

	public void StartKick(Point delta, int kickStrength)
	{
		this.timeBetweenKickMoves = 200;
		this.timeBetweenKickMoves *= Math.pow(0.9, kickStrength);
		kickDelta.x = delta.x;
		kickDelta.y = delta.y;
		owner.worldMap.NotifyBombMoved(position.x, position.y, position.x + delta.x, position.y + delta.y, this);
		position.x += delta.x;
		position.y += delta.y;
		nextKickMove = GameTime.getGameTime() + timeBetweenKickMoves;
	}

	public void Recycle(Point newPosition, Entity newOwner)
	{
		this.position = newPosition.copy();
		this.owner = newOwner;
		Initialize();
	}

	public void FlagForImmediateDetonation()
	{
		this.fuseTime = 0;
	}
}
