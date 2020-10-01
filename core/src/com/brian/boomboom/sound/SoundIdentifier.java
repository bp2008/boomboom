package com.brian.boomboom.sound;

public class SoundIdentifier
{
	public SoundType soundType;
	public int soundIndex;
	public long soundId;
	
	public SoundIdentifier(SoundType type, int index, long id)
	{
		soundType = type;
		soundIndex = index;
		soundId = id;
	}
}
