package com.brian.boomboom.bomb;

import java.util.LinkedList;
import java.util.Queue;

import com.brian.boomboom.util.Point;

public class ExplosionManager
{
	private static Queue<Explosion> explosionPool = new LinkedList<Explosion>();

	public static Explosion getExplosion(int explosionPower, Point position, ExplosionDirection direction,
			ExplosionType explosionType)
	{
		Explosion e = explosionPool.poll();
		if (e == null)
			return new Explosion(explosionPower, position, direction, explosionType);
		e.Recycle(explosionPower, position, direction, explosionType);
		return e;
	}

	public static void returnExplosionToPool(Explosion e)
	{
		explosionPool.offer(e);
	}
}