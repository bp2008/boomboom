package com.brian.boomboom.bomb;

import java.util.LinkedList;
import java.util.Queue;

import com.brian.boomboom.entity.Entity;
import com.brian.boomboom.util.Point;

public class BombManager
{
	private static Queue<Bomb> bombPool = new LinkedList<Bomb>();

	public static Bomb getBomb(Point position, Entity owner)
	{
		Bomb b = bombPool.poll();
		if (b == null)
			return new Bomb(position, owner);
		b.Recycle(position, owner);
		return b;
	}

	public static void returnBombToPool(Bomb b)
	{
		bombPool.offer(b);
	}
}
