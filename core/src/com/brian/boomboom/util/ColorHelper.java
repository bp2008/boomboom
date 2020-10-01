package com.brian.boomboom.util;

import com.badlogic.gdx.graphics.Color;

public class ColorHelper
{
	public static Color FromHSV(float hue, float saturation, float value)
	{
		hue %= 1;
	    int h = ((int)(hue * 6)) % 6;
	    float f = hue * 6 - h;
	    float p = value * (1 - saturation);
	    float q = value * (1 - f * saturation);
	    float t = value * (1 - (1 - f) * saturation);
	
	    switch (h) {
	      case 0: return new Color(value, t, p, 1f);
	      case 1: return new Color(q, value, p, 1f);
	      case 2: return new Color(p, value, t, 1f);
	      case 3: return new Color(p, q, value, 1f);
	      case 4: return new Color(t, p, value, 1f);
	      case 5: return new Color(value, p, q, 1f);
	      default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. (h=" + h + ") Input was " + hue + ", " + saturation + ", " + value);
	    }
	}
}