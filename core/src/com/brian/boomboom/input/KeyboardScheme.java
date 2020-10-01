package com.brian.boomboom.input;

public class KeyboardScheme
{
	public int PlayerIndex;
	public int Up;
	public int Down;
	public int Left;
	public int Right;
	public int FaceUp;
	public int FaceDown;
	public int FaceLeft;
	public int FaceRight;
	public int Bomb;
	public int Use;
	public int Back;

	public KeyboardScheme(int playerIndex, int up, int down, int left, int right, int faceUp, int faceDown,
			int faceLeft, int faceRight, int bomb, int use, int back)
	{
		PlayerIndex = playerIndex;
		Up = up;
		Down = down;
		Left = left;
		Right = right;
		FaceUp = faceUp;
		FaceDown = faceDown;
		FaceLeft = faceLeft;
		FaceRight = faceRight;
		Bomb = bomb;
		Use = use;
		Back = back;
	}
}
