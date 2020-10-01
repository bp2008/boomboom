package com.brian.boomboom.gui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.brian.boomboom.item.Item;
import com.brian.boomboom.item.ItemType;

public class ItemGuideHelper
{
	public static void AddItemRow(ItemType itemType, Table tbl, ArrayList<Label> labels, Skin skin)
	{
		Image img = new Image(Item.getTextureRegion(itemType));
		img.setOrigin(img.getWidth() / 2f, img.getHeight() / 2f);
		img.setScale(4);
		tbl.add(img);

		Label label = new Label("   " + Item.getItemInfo(itemType).FriendlyName, skin);
		label.setName(itemType.name());
		labels.add(label);
		label.layout();
		tbl.add(label);
		tbl.getCell(label).align(Align.left);
		tbl.row();
	}

	public static String TextWrap(String text)
	{
		StringBuilder sb = new StringBuilder();
		int charsThisLine = 0;
		for (int i = 0; i < text.length(); i++, charsThisLine++)
		{
			if (charsThisLine < 50)
				sb.append(text.charAt(i));
			else
			{
				if (text.charAt(i) == ' ')
				{
					sb.append('\n');
					charsThisLine = -1;
				}
				else
					sb.append(text.charAt(i));
			}
		}
		return sb.toString();
	}
}
