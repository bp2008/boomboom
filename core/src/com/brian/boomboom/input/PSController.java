package com.brian.boomboom.input;

import com.badlogic.gdx.controllers.Controller;

public class PSController
{
	public static boolean nameCheck(Controller c)
	{
		return nameCheck(c.getName());
	}
	public static boolean nameCheck(String name)
	{
		return name.equals("Wireless Controller");
	}
	public static final int BUTTON_SQUARE = 0;
	public static final int BUTTON_X = 1;
	public static final int BUTTON_TRIANGLE = 3;
	public static final int BUTTON_CIRCLE = 2;
	public static final int BUTTON_L = 4;
	public static final int BUTTON_R = 5;
	public static final int BUTTON_TRIGGER_L = 6;
	public static final int BUTTON_TRIGGER_R = 7;
	public static final int BUTTON_SHARE = 8;
	public static final int BUTTON_OPTIONS = 9;
	public static final int BUTTON_LEFT_STICK = 10;
	public static final int BUTTON_RIGHT_STICK = 11;
	public static final int BUTTON_PLAYSTATION= 12;
	public static final int BUTTON_TOUCHPAD= 13;

	public static final int AXIS_LEFT_Y = 2;
	public static final int AXIS_LEFT_X = 3;
	public static final int AXIS_RIGHT_Y = 0;
	public static final int AXIS_RIGHT_X = 1;
//	public static final int AXIS_SHOULDERS = 4;

	// The D-PAD is handled by a POV hat in the libGDX Controller library.
}
