package com.brian.boomboom.util;

/**
 * This class represents a time snapshot which updates only when the tick() function is called. This makes it ideal for
 * timing of game logic, since the time values will not update during rendering or anything.
 * 
 * @author 2012 Brian Pearce
 */
public class GameTime
{
	private static long startTime;
	private static long gameTime;
	private static long realTime;
	private static boolean isPaused;
	// private static long pausedAtTime;
	private static long lastTickTime;
	private static int animationFrame_w2;
	private static int animationFrame_w3;
	private static int animationFrame_w3_slow;
	static
	{
		reset();
	}
	public static void reset()
	{
		startTime = realTime = lastTickTime = Utilities.getTimeInMs();
		gameTime = 0;
		isPaused = false;
		animationFrame_w2 = animationFrame_w3 = animationFrame_w3_slow = 0;
	}

	/**
	 * Call this when the game gets paused. Game time will stop increasing.
	 */
	public static void pause()
	{
		isPaused = true;
	}

	/**
	 * Call this when the game gets unpaused. Game time will resume increasing.
	 */
	public static void unpause()
	{
		isPaused = false;
	}

	/**
	 * Call this one time at the beginning of each main loop iteration.
	 */
	public static void tick()
	{
		realTime = Utilities.getTimeInMs();
		if (isPaused)
		{
			lastTickTime = realTime;
			return;
		}
		gameTime += realTime - lastTickTime;
		lastTickTime = realTime;
		animationFrame_w2 = (gameTime % 250) > 124 ? 1 : 0;
		animationFrame_w3 = (int) (gameTime % 500);
		if (animationFrame_w3 < 125)
			animationFrame_w3 = 0;
		else if (animationFrame_w3 < 250)
			animationFrame_w3 = 1;
		else if (animationFrame_w3 < 375)
			animationFrame_w3 = 2;
		else
			animationFrame_w3 = 1;
		animationFrame_w3_slow = (int) (gameTime % 1500);
		if (animationFrame_w3_slow < 375)
			animationFrame_w3_slow = 0;
		else if (animationFrame_w3_slow < 750)
			animationFrame_w3_slow = 1;
		else if (animationFrame_w3_slow < 1125)
			animationFrame_w3_slow = 2;
		else
			animationFrame_w3_slow = 1;
	}

	/**
	 * Returns the current time in the game world (in milliseconds). This time does not change while the game is paused.
	 * 
	 * @return The current time in the game world (in milliseconds). This time does not change while the game is paused.
	 */
	public static long getGameTime()
	{
		return gameTime;
	}

	/**
	 * Returns the current time in the real world (in milliseconds). This time does change while the game is paused.
	 * 
	 * @return The current time in the real world (in milliseconds). This time does change while the game is paused.
	 */
	public static long getRealTime()
	{
		return realTime;
	}

	/**
	 * Returns the real-world time when the game was initialized.
	 * 
	 * @return The real-world time when the game was initialized.
	 */
	public static long getStartTime()
	{
		return startTime;
	}

	/**
	 * Returns true if the game is paused, false if it is not paused.
	 * 
	 * @return true if the game is paused, false if it is not paused.
	 */
	public static boolean isPaused()
	{
		return isPaused;
	}

	/**
	 * Returns the current animation frame for a 2-frame world block.
	 * 
	 * @return The current animation frame for a 2-frame world block.
	 */
	public static int get2wAnimationFrame()
	{
		return animationFrame_w2;
	}

	/**
	 * Returns the current animation frame for a 3-frame world block.
	 * 
	 * @return The current animation frame for a 3-frame world block.
	 */
	public static int get3wAnimationFrame()
	{
		return animationFrame_w3;
	}

	/**
	 * Returns the current animation frame for a slow 3-frame world block.
	 * 
	 * @return The current animation frame for a slow 3-frame world block.
	 */
	public static int get3wSlowAnimationFrame()
	{
		return animationFrame_w3_slow;
	}
}
