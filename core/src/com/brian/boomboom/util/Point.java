package com.brian.boomboom.util;

import com.badlogic.gdx.math.Vector2;

public class Point
{
	public int x;
	public int y;

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int IntDistanceFrom(int x, int y)
	{
		return Math.abs(this.x - x) + Math.abs(this.y - y);
	}

	public int IntDistanceFrom(Point p)
	{
		return IntDistanceFrom(p.x, p.y);
	}

	public Point Minus(Point toSubtract)
	{
		return new Point(x - toSubtract.x, y - toSubtract.y);
	}

	public Point Plus(Point toAdd)
	{
		return new Point(x + toAdd.x, y + toAdd.y);
	}

	public Vector2 Minus(Vector2 toSubtract)
	{
		return new Vector2(x - toSubtract.x, y - toSubtract.y);
	}

	public Vector2 Plus(Vector2 toAdd)
	{
		return new Vector2(x + toAdd.x, y + toAdd.y);
	}

	public Vector2 Vector_Minus_Point(Vector2 v)
	{
		return new Vector2(v.x - x, v.y - y);
	}

	public static Vector2 Vector_Minus_Point(Vector2 v, Point toSubtract)
	{
		return new Vector2(v.x - toSubtract.x, v.y - toSubtract.y);
	}

	/**
	 * Returns true if this Point has the same coordinates as the other Point.
	 * @param otherPoint The other Point to compare to this point.
	 * @return true if this Point has the same coordinates as the other Point.
	 */
	public boolean Equals(Point otherPoint)
	{
		return x == otherPoint.x && y == otherPoint.y;
	}

	public Point copy()
	{
		return new Point(x, y);
	}

	public void Add(Point delta)
	{
		x += delta.x;
		y += delta.y;
	}

	public void Subtract(Point delta)
	{
		x -= delta.x;
		y -= delta.y;
	}

	/**
	 * Sets this Point's x and y to match the x and y values of the passed-in point.
	 * 
	 * @param p
	 *            The point to make this point equal to.
	 */
	public void setEqualTo(Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}

	public void Swap(Point position)
	{
		int tempX = position.x;
		int tempY = position.y;
		position.setEqualTo(this);
		x = tempX;
		y = tempY;
	}
}
