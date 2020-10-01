package com.brian.boomboom.item;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brian.boomboom.Globals;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.util.GameTime;
import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.Levels;

/**
 * The procedure for adding an item is: 1. Add the item type to ItemType.java 2. Add the item texture region in
 * getTextureRegion below. 3. Add the item's Info strings in getItemInfo below. 4. Give the item some spawning tickets
 * in InitializeTickets below. 5. IF CHARGES, add charges to Recycle below. 6. Add the item to the item guide GUI. 7.
 * Add pickup in Entity.java 7. Add implementation in Player.java
 * 
 * @author Brian
 * 
 */
public class Item
{
	private static boolean angelOfDeathCreated = false;
	private static SortedMap<ItemType, Point> ticketRange = InitializeTickets(0);
	private static int maxTickets;

	public static void Reset(int worldIndex)
	{
		angelOfDeathCreated = false;
		ticketRange = InitializeTickets(worldIndex);
	}

	public int charges;
	public ItemType itemType;
	public long spawnTime;

	public Item(ItemType type)
	{
		Recycle(type);
	}

	public void Recycle(ItemType type)
	{
		switch (type)
		{
			case Flamethrower:
				charges = 5;
				break;
			case Laser:
				charges = 7; // was 10
				break;
			case Heatseeker:
				charges = 4; // was 7
				break;
			case HealthMedicine2:
				charges = 1;
				break;
			case RemoteTeleporter:
				charges = 1;
				break;
			case Banana:
				charges = 1;
				break;
			default:
				charges = -1;
				break;
		}
		this.itemType = type;
		spawnTime = GameTime.getGameTime();
	}

	public void Copy(Item item)
	{
		this.itemType = item.itemType;
		this.charges = item.charges;
		this.spawnTime = item.spawnTime;
	}

	public void Draw(Point position, SpriteBatch batch)
	{
		if (itemType != ItemType.None)
			batch.draw(getTextureRegion(itemType), position.x, position.y, 0, 0, 1, 1, 1, 1, 0);
	}

	public static TextureRegion getTextureRegion(ItemType drawItemType)
	{
		switch (drawItemType)
		{
			case AngelOfDeath:
				return Globals.itemTexture[15];
			case AutoHammer:
				return Globals.itemTexture[19];
			case Banana:
				return Globals.itemTexture[25];
			case Bomb:
				return Globals.itemTexture[0];
			case Detonator:
				return Globals.itemTexture[6];
			case Disease:
				return Globals.itemTexture[14];
			case Earthquake:
				return Globals.itemTexture[28];
			case Flamethrower:
				return Globals.itemTexture[7];
			case Foot:
				return Globals.itemTexture[2];
			case Hammer:
				return Globals.itemTexture[9];
			case HealthMedicine:
				return Globals.itemTexture[8];
			case HealthMedicine2:
				return Globals.itemTexture[13];
			case Heatseeker:
				return Globals.itemTexture[5];
			case HolyChalice:
				return Globals.itemTexture[18];
			case Laser:
				return Globals.itemTexture[16];
			case LineBomb:
				return Globals.itemTexture[12];
			case None:
				return Globals.itemTexture[0];
			case Pizza:
				return Globals.itemTexture[4];
			case Popsicle:
				return Globals.itemTexture[20];
			case Power:
				return Globals.itemTexture[1];
			case Randomfuse:
				return Globals.itemTexture[10];
			case RemoteTeleporter:
				return Globals.itemTexture[17];
			case Rollerskates:
				return Globals.itemTexture[3];
			case Shortfuse:
				return Globals.itemTexture[11];
			case SwapPortal:
				return Globals.itemTexture[24];
			case TwinPop:
				return Globals.itemTexture[21];
		}
		return Globals.itemTexture[0];
	}

	public static ItemInfo getItemInfo(ItemType itemType)
	{
		switch (itemType)
		{
			case AngelOfDeath:
				return new ItemInfo("Angel of Death", "???");
			case Banana:
				return new ItemInfo("Banana", "Heals a small amount when eaten.  Please do not litter.");
			case Bomb:
				return new ItemInfo("Extra Bomb", "+1 Bomb Limit");
			case Detonator:
				return new ItemInfo("Detonator", "Remote bomb detonator.");
			case Disease:
				return new ItemInfo("Disease", "It is highly contagious.");
			case Earthquake:
				return new ItemInfo("Earthquake", "Rock'n'roll baby!");
			case Flamethrower:
				return new ItemInfo("Hairspray & Lighter",
						"Shoots a short burst of flame in the direction you are facing.  Has a limited number of charges.");
			case Foot:
				return new ItemInfo("Bomb Kicker", "Gives you the ability to kick bombs.");
			case Hammer:
				return new ItemInfo("Hammer", "Allows you to create and destroy standard blocks.");
			case AutoHammer:
				return new ItemInfo("Auto Hammer",
						"While activated, blocks are automatically created behind you as you move.  Be careful!");
			case HealthMedicine:
				return new ItemInfo("Health Medicine",
						"Instantly heals you to full, but destroys any item you are currently holding.");
			case HealthMedicine2:
				return new ItemInfo(
						"Double Health Medicine",
						"Instantly heals you to full, but destroys any item you are currently holding.  Comes with one additional dose that can be used later.");
			case Heatseeker:
				return new ItemInfo("Heat-Seeking Missiles",
						"Smart missiles that chase an enemy.  Has a limited number of charges.");
			case HolyChalice:
				return new ItemInfo("Holy Chalice", "???");
			case Laser:
				return new ItemInfo("Laser",
						"Shoots a long range laser in the direction you are facing.  Has a limited number of charges.");
			case LineBomb:
				return new ItemInfo("Line Bomb", "Causes a line of bombs to extend in the direction you are facing.");
			case None:
				return new ItemInfo("None", "No item.");
			case Pizza:
				return new ItemInfo("Pizza",
						"A small heal that does not destroy your current item.  Contains no preservatives.");
			case Popsicle:
				return new ItemInfo("Popsicle", "The next player you touch is frozen for a short amount of time.");
			case Power:
				return new ItemInfo("Power", "+1 Bomb Power");
			case Randomfuse:
				return new ItemInfo("Cheap Bombs", "Made in China");
			case RemoteTeleporter:
				return new ItemInfo("Teleporter", "Single-use emergency teleporter.");
			case Rollerskates:
				return new ItemInfo("Rollerskates", "Increases your movement speed.");
			case Shortfuse:
				return new ItemInfo("Short Fuse", "Causes your bombs to have a short fuse time.");
			case SwapPortal:
				return new ItemInfo("Portal", "You and a random living player swap positions on the map.");
			case TwinPop:
				return new ItemInfo("Twin Pop", "The next player you touch is frozen for a medium amount of time.");
		}
		return new ItemInfo("Unknown", "Nothing to see here...");
	}

	public void GenerateRandomItem()
	{
		int num = Globals.rndGenerator.nextInt(maxTickets);
		Iterator<ItemType> itemKeys = ticketRange.keySet().iterator();
		while (itemKeys.hasNext())
		{
			ItemType key = itemKeys.next();
			Point p = ticketRange.get(key);
			if (p.x <= num && p.y > num)
			{
				if (key == ItemType.AngelOfDeath)
				{
					if (angelOfDeathCreated)
					{
						GenerateRandomItem();
						return;
					}
					angelOfDeathCreated = true;
				}
				Recycle(key);
				return;
			}
		}
		Recycle(ItemType.None);
	}

	private static SortedMap<ItemType, Point> InitializeTickets(int worldIndex)
	{
		SortedMap<ItemType, Integer> itemLotteryTickets = new TreeMap<ItemType, Integer>();
		// The higher the number each item is given, the higher the chance of that item appearing.
		itemLotteryTickets.put(ItemType.Bomb, 850);
		itemLotteryTickets.put(ItemType.Power, 600);
		itemLotteryTickets.put(ItemType.Foot, 225);
		itemLotteryTickets.put(ItemType.Rollerskates, 250);
		itemLotteryTickets.put(ItemType.Flamethrower, 125);
		itemLotteryTickets.put(ItemType.Heatseeker, 125);
		itemLotteryTickets.put(ItemType.Randomfuse, 100);
		itemLotteryTickets.put(ItemType.Shortfuse, 100);
		itemLotteryTickets.put(ItemType.Detonator, 75);
		itemLotteryTickets.put(ItemType.Hammer, 75);
		itemLotteryTickets.put(ItemType.AutoHammer, 30);
		itemLotteryTickets.put(ItemType.HealthMedicine2, 75);
		itemLotteryTickets.put(ItemType.HealthMedicine, 100);
		itemLotteryTickets.put(ItemType.Pizza, 150);
		itemLotteryTickets.put(ItemType.RemoteTeleporter, 75);
		itemLotteryTickets.put(ItemType.HolyChalice, 75);
		itemLotteryTickets.put(ItemType.LineBomb, 75);
		itemLotteryTickets.put(ItemType.Disease, 100);
		itemLotteryTickets.put(ItemType.Laser, 15);
		itemLotteryTickets.put(ItemType.Banana, 125);
		itemLotteryTickets.put(ItemType.Earthquake, 10);
		itemLotteryTickets.put(ItemType.Popsicle, 125);
		itemLotteryTickets.put(ItemType.SwapPortal, 75);
		itemLotteryTickets.put(ItemType.TwinPop, 75);
		
		if (worldIndex == Levels.Desert || Settings.angelOfDeathEverywhereState >= 2)
			itemLotteryTickets.put(ItemType.AngelOfDeath, 60);
		else if (Settings.angelOfDeathEverywhereState == 1)
			itemLotteryTickets.put(ItemType.AngelOfDeath, 10);
		
		// We are adding ItemType.None with a number of tickets equal to 90% of the existing tickets. This leaves
		// ItemType.None holding about 47.37% of all the tickets.
		itemLotteryTickets.put(ItemType.None, (int) (Sum(itemLotteryTickets.values()) * 0.90));

		maxTickets = Sum(itemLotteryTickets.values());

		SortedMap<ItemType, Point> ticketRange = new TreeMap<ItemType, Point>();

		Iterator<ItemType> itemKeys = itemLotteryTickets.keySet().iterator();
		int runningTotal = 0;
		while (itemKeys.hasNext())
		{
			ItemType key = itemKeys.next();
			int ticketCount = itemLotteryTickets.get(key);
			ticketRange.put(key, new Point(runningTotal, ticketCount + runningTotal));
			runningTotal += ticketCount;
		}
		return ticketRange;
	}

	private static int Sum(Collection<Integer> iList)
	{
		int sum = 0;
		Iterator<Integer> it = iList.iterator();
		while (it.hasNext())
			sum += it.next();
		return sum;
	}

	// / <summary>
	// / Returns the duration of the specified disease, in ms.
	// / </summary>
	// / <param name="disease">The disease to get the duration of.</param>
	// / <returns>The duration of the specified disease, in ms.</returns>
	public static long DiseaseTime(DiseaseType disease)
	{
		switch (disease)
		{
			case Diarrhea:
				return 15000;
			default:
				return 0;
		}
	}
}
