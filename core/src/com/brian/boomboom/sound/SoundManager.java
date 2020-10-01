package com.brian.boomboom.sound;

import java.util.ArrayList;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.brian.boomboom.Globals;
import com.brian.boomboom.settings.Settings;
import com.brian.boomboom.util.GameTime;

public class SoundManager
{
	public TreeMap<SoundType, Sound[]> sounds = new TreeMap<SoundType, Sound[]>();
	public ArrayList<Music> songs = new ArrayList<Music>();
	public TreeMap<SoundType, Long> soundTypesLastPlayed = new TreeMap<SoundType, Long>();
	int playingSong = -1;

	/**
	 * Plays a random sound of the specified type. Some sound types may have only one sound.
	 * 
	 * @param type
	 *            The type of sound to play.
	 * @return A SoundIdentifier which can be used to access to the particular sound while it is playing (to modify or
	 *         stop it).
	 */
	public SoundIdentifier PlaySound(SoundType type)
	{
		// TODO: Limit concurrent sounds to whatever OUYA is capable of playing.  Or 32 or something.  Maybe a limit of 8 concurrent of each sound type, too.
		if (sounds.size() == 0)
			return null;
		
		long currentTime = GameTime.getRealTime();
		Long soundLastPlayed = soundTypesLastPlayed.get(type);
		long timeSinceLastPlay = soundLastPlayed == null ? Long.MAX_VALUE : (currentTime - soundLastPlayed);
		if(timeSinceLastPlay < 200)
			return null;
		soundTypesLastPlayed.put(type, currentTime);
		
		// Choose a sound to play
		Sound[] soundArray = sounds.get(type);
		int soundIndex = Globals.rndGenerator.nextInt(soundArray.length);

		// Set the volume
		float effectiveVolume = Settings.soundVolume;
		if (type == SoundType.FootstepsHardConcrete)
			effectiveVolume *= 0.5f;

		// Start playing the sound and return an identifier for it.
		return new SoundIdentifier(type, soundIndex, soundArray[soundIndex].play(effectiveVolume));
	}

	public void StopSound(SoundIdentifier identifier)
	{
		if (sounds.size() == 0 || identifier == null)
			return;
		sounds.get(identifier.soundType)[identifier.soundIndex].stop(identifier.soundId);
	}

	public void StopAllSounds()
	{
		for (Sound[] soundArray : sounds.values())
			for (Sound sound : soundArray)
				sound.stop();
	}

	public void PlaySong(int songIndex)
	{
		if(songIndex < 0)
			songIndex = musicSongs.length + songIndex;
		if(songIndex == playingSong)
			return;
		StopMusic();
		ResumeMusic(songIndex);
	}

	public void PauseMusic()
	{
		if (playingSong < 0 || playingSong >= songs.size())
			return;
		songs.get(playingSong).pause();
	}

	public void ResumeMusic()
	{
		ResumeMusic(playingSong);
	}

	public void ResumeMusic(int songIndex)
	{
		if (songIndex < 0 || songIndex >= songs.size())
			return;
		playingSong = songIndex;
		songs.get(songIndex).setLooping(true);
		songs.get(songIndex).setVolume(Settings.musicVolume);
		songs.get(songIndex).play();
	}

	public void StopMusic()
	{
		if (playingSong < 0 || playingSong >= songs.size())
			return;
		songs.get(playingSong).stop();

		playingSong = -1;
	}

	public void loadContent()
	{
		sounds.clear();
		sounds.put(SoundType.FootstepsWood, LoadSounds(footstepsWoodSounds));
		sounds.put(SoundType.FootstepsTile, LoadSounds(footstepsTileSounds));
		sounds.put(SoundType.FootstepsSoftConcrete, LoadSounds(footstepsSoftConcreteSounds));
		sounds.put(SoundType.FootstepsHardConcrete, LoadSounds(footstepsHardConcreteSounds));
		sounds.put(SoundType.FootstepsSnow, LoadSounds(footstepsSnowSounds));
		sounds.put(SoundType.FootstepsGravel, LoadSounds(footstepsGravelSounds));
		sounds.put(SoundType.FootstepsGrass, LoadSounds(footstepsGrassSounds));
		sounds.put(SoundType.Bomb, LoadSounds(bombSounds));
		sounds.put(SoundType.Firecracker, LoadSounds(firecrackerSounds));
		sounds.put(SoundType.HammerCreate, LoadSounds(hammerCreateSounds));
		sounds.put(SoundType.HammerDestroy, LoadSounds(hammerDestroySounds));
		sounds.put(SoundType.HammerDestroyWorldStandard, LoadSounds(hammerWorldStandardSounds));
		sounds.put(SoundType.Hurt, LoadSounds(hurtSounds));
		sounds.put(SoundType.ItemHeal, LoadSounds(itemHealSounds));
		sounds.put(SoundType.UseHeal, LoadSounds(useHealSounds));
		sounds.put(SoundType.ItemOther, LoadSounds(itemSounds));
		sounds.put(SoundType.Zap, LoadSounds(zapSounds));
		sounds.put(SoundType.Fuse, LoadSounds(fuseSounds));
		sounds.put(SoundType.AngelOfDeathArrive, LoadSounds(angelOfDeathArriveSounds));
		sounds.put(SoundType.AngelFire, LoadSounds(angelFireSounds));
		sounds.put(SoundType.Freeze, LoadSounds(freezeSounds));
		sounds.put(SoundType.Earthquake, LoadSounds(earthquakeSounds));
		sounds.put(SoundType.Slip, LoadSounds(slipSounds));
		songs = LoadMusic(musicSongs);
	}

	public void unloadContent()
	{
		for (Music m : songs)
		{
			m.stop();
			m.dispose();
		}
		songs.clear();
		for (Sound[] soundArray : sounds.values())
			for (Sound s : soundArray)
			{
				s.stop();
				s.dispose();
			}
		sounds.clear();
	}

	private Sound[] LoadSounds(String[] soundPaths)
	{
		Sound[] sounds = new Sound[soundPaths.length];
		for (int i = 0; i < soundPaths.length; i++)
			sounds[i] = Gdx.audio.newSound(Gdx.files.internal(soundPaths[i]));
		return sounds;
	}

	private ArrayList<Music> LoadMusic(String[] songPaths)
	{
		ArrayList<Music> songs = new ArrayList<Music>(songPaths.length);
		for (int i = 0; i < songPaths.length; i++)
			songs.add(Gdx.audio.newMusic(Gdx.files.internal(songPaths[i])));
		return songs;
	}

	private String[] footstepsWoodSounds = new String[] {
			"data/Sounds/Footsteps/Wood/38876__swuing__footstep_wood.ogg",
			"data/Sounds/Footsteps/Wood/9917__Snoman__wood1.ogg", "data/Sounds/Footsteps/Wood/9918__Snoman__wood2.ogg",
			"data/Sounds/Footsteps/Wood/9919__Snoman__wood3.ogg", "data/Sounds/Footsteps/Wood/9920__Snoman__wood4.ogg",
			"data/Sounds/Footsteps/Wood/39044__wildweasel__wood3.ogg",
			"data/Sounds/Footsteps/Wood/Squeak/18062__Corsica_S__squeak_1.ogg",
			"data/Sounds/Footsteps/Wood/Squeak/18065__Corsica_S__squeak_2.ogg", };
	private String[] footstepsTileSounds = new String[] { "data/Sounds/Footsteps/Tile/32639__carrigsound__Step_2.ogg",
			"data/Sounds/Footsteps/Tile/32641__carrigsound__Step_4.ogg",
			"data/Sounds/Footsteps/Tile/32642__carrigsound__Step_5.ogg",
			"data/Sounds/Footsteps/Tile/32643__carrigsound__Step_6.ogg",
			"data/Sounds/Footsteps/Tile/32644__carrigsound__Step_7.ogg" };
	private String[] footstepsSoftConcreteSounds = new String[] {
			"data/Sounds/Footsteps/Soft Concrete/38873__swuing__footstep_concrete.ogg",
			"data/Sounds/Footsteps/Soft Concrete/32646__carrigsound__Step_9.ogg" };
	private String[] footstepsHardConcreteSounds = new String[] {
			"data/Sounds/Footsteps/Hard Concrete/76186__movingplaid__wood_step_4.ogg",
			"data/Sounds/Footsteps/Hard Concrete/76187__movingplaid__concrete_step_1.ogg",
			"data/Sounds/Footsteps/Hard Concrete/76189__movingplaid__concrete_step_3.ogg",
			"data/Sounds/Footsteps/Hard Concrete/76190__movingplaid__concrete_step_4.ogg" };
	private String[] footstepsSnowSounds = new String[] { "data/Sounds/Footsteps/Snow/9913__Snoman__snow1.ogg",
			"data/Sounds/Footsteps/Snow/9914__Snoman__snow2.ogg", "data/Sounds/Footsteps/Snow/9915__Snoman__snow3.ogg",
			"data/Sounds/Footsteps/Snow/9916__Snoman__snow4.ogg" };
	private String[] footstepsGrassSounds = new String[] { "data/Sounds/Footsteps/Grass/9904__Snoman__grass1.ogg",
			"data/Sounds/Footsteps/Grass/9905__Snoman__grass2.ogg",
			"data/Sounds/Footsteps/Grass/9906__Snoman__grass3.ogg",
			"data/Sounds/Footsteps/Grass/9907__Snoman__grass4.ogg",
			"data/Sounds/Footsteps/Grass/9912__Snoman__grass5.ogg" };
	private String[] footstepsGravelSounds = new String[] { "data/Sounds/Footsteps/Gravel/9908__Snoman__gravel1.ogg",
			"data/Sounds/Footsteps/Gravel/9909__Snoman__gravel2.ogg",
			"data/Sounds/Footsteps/Gravel/9910__Snoman__gravel3.ogg",
			"data/Sounds/Footsteps/Gravel/9911__Snoman__gravel4.ogg" };
	private String[] firecrackerSounds = new String[] { "data/Sounds/Firecracker/111164__NoiseCollector__firecracker_eggnog.ogg" };
	private String[] bombSounds = new String[] { "data/Sounds/Bomb/110115__ryansnook__Small_Explosion.ogg",
			"data/Sounds/Bomb/31874__HardPCM__Chip037.ogg", "data/Sounds/Bomb/51464__smcameron__bombexplosion.ogg",
			"data/Sounds/Bomb/51466__smcameron__flak_hit.ogg" };
	private String[] hammerWorldStandardSounds = new String[] {
			"data/Sounds/Hammer/103630__Benboncan__Large_Anvil_Steel_Hammer_2.ogg",
			"data/Sounds/Hammer/103632__Benboncan__Large_Anvil_Steel_Hammer_4.ogg" };
	private String[] hammerDestroySounds = new String[] { "data/Sounds/Hammer/19015__l0calh05t__anvil_strike_3.ogg",
			"data/Sounds/Hammer/19020__l0calh05t__anvil_strike_8.ogg" };
	private String[] hammerCreateSounds = new String[] { "data/Sounds/Hammer/96132__BMacZero__Bing1.ogg" };
	private String[] hurtSounds = new String[] { "data/Sounds/Hurt/104697__grunz__grunz_ow.ogg",
			"data/Sounds/Hurt/44428__thecheeseman__hurt1.ogg", "data/Sounds/Hurt/44429__thecheeseman__hurt2.ogg",
			"data/Sounds/Hurt/44430__thecheeseman__hurt3.ogg" };
	private String[] itemSounds = new String[] { "data/Sounds/Item Pickup/49966__simon.rue__studs_moln_v4.ogg" };
	private String[] itemHealSounds = new String[] { "data/Sounds/Item Pickup/39026__wildweasel__KeyPickup.ogg" };
	private String[] useHealSounds = new String[] { "data/Sounds/Item Pickup/39041__wildweasel__itemup2.wav" };
	private String[] zapSounds = new String[] { "data/Sounds/Zap/117740__Donalfonso__Kurzschluss.ogg" };
	private String[] fuseSounds = new String[] { "data/Sounds/Fuse/8320__Ned_Bouhalassa__Sparkler.ogg" };
	private String[] angelOfDeathArriveSounds = new String[] { "data/Sounds/Angel/8067__annannienann__Low_D_Arh.ogg" };
	private String[] angelFireSounds = new String[] { "data/Sounds/Angel/25073__FreqMan__whoosh04.ogg" };
	private String[] freezeSounds = new String[] { "data/Sounds/Freeze.ogg" };
	private String[] earthquakeSounds = new String[] { "data/Sounds/Earthquake.ogg" };
	private String[] slipSounds = new String[] { "data/Sounds/Slip.ogg" };
	private String[] musicSongs = new String[] { "data/Music/Eric_Skiff_-_03_-_Chibi_Ninja.mp3",
			"data/Music/Eric_Skiff_-_02_-_Underclocked_underunderclocked_mix.mp3", "data/Music/Eric_Skiff_-_08_-_Ascending.mp3", "data/Music/Eric_Skiff_-_05_-_Come_and_Find_Me.mp3", "data/Music/souleye_-_drawn.ogg",
			"data/Music/Eric_Skiff_-_01_-_A_Night_Of_Dizzy_Spells.mp3", "data/Music/souleye_-_inzane-loop.ogg" };
}
