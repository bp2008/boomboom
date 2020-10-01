package com.brian.boomboom.drawing;

import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class CharTextureDefinition
{
	public String nameBase;
	public CharTextureColoringRequirements colorReq;
	public int colorIndex;
	public TextureRegion[] textures;
	public CharTextureType type = CharTextureType.Normal;

	public CharTextureDefinition(String nameBase, CharTextureColoringRequirements colorReq)
	{
		this.nameBase = nameBase;
		this.colorReq = colorReq;
	}
	
	public CharTextureDefinition(String nameBase, CharTextureColoringRequirements colorReq, CharTextureType type)
	{
		this.nameBase = nameBase;
		this.colorReq = colorReq;
		this.type = type;
	}

	public CharTextureDefinition(String nameBase, CharTextureColoringRequirements colorReq, int colorIndex)
	{
		this.nameBase = nameBase;
		this.colorReq = colorReq;
		this.colorIndex = colorIndex;
	}


	public CharTextureDefinition(String nameBase, CharTextureColoringRequirements colorReq, int colorIndex, CharTextureType type)
	{
		this.nameBase = nameBase;
		this.colorReq = colorReq;
		this.colorIndex = colorIndex;
		this.type = type;
	}

}
