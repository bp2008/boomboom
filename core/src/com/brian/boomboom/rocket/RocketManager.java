package com.brian.boomboom.rocket;

import java.util.LinkedList;
import java.util.Queue;

import com.brian.boomboom.entity.Entity;

public class RocketManager
{
	private static Queue<Rocket> rocketPool = new LinkedList<Rocket>();

	public static Rocket getRocket(Entity owner, Entity target)
	{
		Rocket r = rocketPool.poll();
		if (r == null)
			return new Rocket(owner, target);
		r.Recycle(owner, target);
		return r;
	}

	public static void returnRocketToPool(Rocket r)
	{
		rocketPool.offer(r);
	}
}
