package com.brian.boomboom.input;

import com.badlogic.gdx.controllers.Controller;

public class WiiController
{
	public static boolean nameCheck(Controller c)
	{
		return nameCheck(c.getName());
	}
	public static boolean nameCheck(String name)
	{
		return name.equals("Dual Box WII");
	}
	public static final int BUTTON_Y = 3;
	public static final int BUTTON_B = 2;
	public static final int BUTTON_A = 1;
	public static final int BUTTON_X = 0;
	public static final int BUTTON_L = 6;
	public static final int BUTTON_R = 7;
	public static final int BUTTON_TRIGGER_L = 4;
	public static final int BUTTON_TRIGGER_R = 5;
	public static final int BUTTON_SELECT = 8;
	public static final int BUTTON_START = 9;
	public static final int BUTTON_HOME = 10;

	public static final int AXIS_LEFT_Y = 0;
	public static final int AXIS_LEFT_X = 1;
	public static final int AXIS_RIGHT_Y = 2;
	public static final int AXIS_RIGHT_X = 3;
//	public static final int AXIS_SHOULDERS = 4;

	// The D-PAD is handled by a POV hat in the libGDX Controller library.
}
