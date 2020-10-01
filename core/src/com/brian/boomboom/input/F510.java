package com.brian.boomboom.input;

import com.badlogic.gdx.controllers.Controller;

public class F510
{
	public static boolean nameCheck(String name)
	{
		return name.contains("F510") || name.contains("F310") || name.contains("F710");
	}
	public static final int BUTTON_A = 0;
	public static final int BUTTON_B = 1;
	public static final int BUTTON_X = 2;
	public static final int BUTTON_Y = 3;
	public static final int BUTTON_L = 4;
	public static final int BUTTON_R = 5;
	public static final int BUTTON_BACK = 6;
	public static final int BUTTON_START = 7;
	public static final int BUTTON_LEFT_STICK = 8;
	public static final int BUTTON_RIGHT_STICK = 9;

	public static final int AXIS_LEFT_Y = 0;
	public static final int AXIS_LEFT_X = 1;
	public static final int AXIS_RIGHT_Y = 2;
	public static final int AXIS_RIGHT_X = 3;
	public static final int AXIS_SHOULDERS = 4;

	public static boolean IsXboxController(Controller c)
	{
		return c.getName().contains("XBOX 360");
	}

	// The D-PAD is handled by a POV hat in the libGDX Controller library.
}
