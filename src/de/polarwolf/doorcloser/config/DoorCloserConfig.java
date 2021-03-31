// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class DoorCloserConfig {
	
	protected final Plugin plugin;
	protected boolean debug = false;
	
	protected static final String SECONDSTOREMAINOPEN_KEY = "Time";
	protected static final int SECONDSTOREMAINOPEN_DEFAULT = 5;
	protected int secondsToRemainOpenData = SECONDSTOREMAINOPEN_DEFAULT;

	protected static final String SYNCHRONIZEDOUBLEDOOR_KEY = "SynchronizeDoubleDoorOpen";
	protected static final boolean SYNCHRONIZEDOUBLEDOOR_DEFAULT = true;
	protected boolean synchronizeDoubleDoorData = SYNCHRONIZEDOUBLEDOOR_DEFAULT;

	protected static final String OPTIMISTICDOUBLEDOORSYNC_KEY = "OptimisticDoubleDoorSync";
	protected static final boolean OPTIMISTICDOUBLEDOORSYNC_DEFAULT = true;
	protected boolean optimisticDoubleDoorSync = OPTIMISTICDOUBLEDOORSYNC_DEFAULT;
	
	protected static final String PLAYSOUND_KEY = "PlaySound";
	protected static final boolean PLAYSOUND_DEFAULT = true;
	protected boolean playSoundData = PLAYSOUND_DEFAULT;

	protected static final String IGNOREIFINCREATIVE_KEY = "IgnoreIfInCreative";
	protected static final boolean IGNOREIFINCREATIVE_DEFAULT = true;
	protected boolean ignoreIfInCreativeData = IGNOREIFINCREATIVE_DEFAULT;

	protected static final String IGNOREIFSNEAKING_KEY = "IgnoreIfSneaking";
	protected static final boolean IGNOREIFSNEAKING_DEFAULT = false;
	protected boolean ignoreIfSneakingData = IGNOREIFSNEAKING_DEFAULT;

	protected static final String TRAPDOORSINSCOPE_KEY = "TrapDoorBlocks";
	protected List<Material> trapDoorsInScopeData = new ArrayList<>();

	protected static final String GATESINSCOPE_KEY = "GateBlocks";
	protected List<Material> gatesInScopeData = new ArrayList<>();

	protected static final String DOORSINSCOPE_KEY = "DoorBlocks";
	protected List<Material> doorsInScopeData = new ArrayList<>();

	public DoorCloserConfig(Plugin plugin) {
		this.plugin = plugin;
		readConfigValues();
		dumpConfigValues(false);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean newDebug) {
		if (debug != newDebug) {
			debug = newDebug;
			if (newDebug) {
				plugin.getLogger().info("Debug was enabled.");
			} else {
				plugin.getLogger().info("Debug was disabled.");
			}
		}
	}

	public int getSecondsToRemainOpen() {
		return secondsToRemainOpenData;
	}

	public boolean isSynchronizeDoubleDoor() {
		return synchronizeDoubleDoorData;
	}

	public boolean isOptimisticDoubleDoorSync() {
		return optimisticDoubleDoorSync;
	}

	public boolean isPlaySound() {
		return playSoundData;
	}
	
	public boolean isIgnoreIfInCreative() {
		return ignoreIfInCreativeData;
	}
	
	public boolean isIgnoreIfSneaking() {
		return ignoreIfSneakingData;
	}

	public List<Material> getTrapDoorsInScope() {
		return new ArrayList<>(trapDoorsInScopeData);
	}

	public List<Material> getGatesInScope() {
		return new ArrayList<>(gatesInScopeData);
	}

	public List<Material> getDoorsInScope() {
		return new ArrayList<>(doorsInScopeData);
	}
	
	protected void printUnknownMaterialWarning (String materialName, String materialType) {
		plugin.getLogger().warning("Unexpected value '" + materialName + "' in config "+ materialType +"  list.");
	}

	protected void readConfigValues()
	{		

		// save the default config, if it's not already present
		plugin.saveDefaultConfig();		
		
		// read settings
		secondsToRemainOpenData = plugin.getConfig().getInt(SECONDSTOREMAINOPEN_KEY, SECONDSTOREMAINOPEN_DEFAULT);
		synchronizeDoubleDoorData = plugin.getConfig().getBoolean(SYNCHRONIZEDOUBLEDOOR_KEY, SYNCHRONIZEDOUBLEDOOR_DEFAULT);
		playSoundData = plugin.getConfig().getBoolean(PLAYSOUND_KEY, PLAYSOUND_DEFAULT);
		optimisticDoubleDoorSync = plugin.getConfig().getBoolean(OPTIMISTICDOUBLEDOORSYNC_KEY, OPTIMISTICDOUBLEDOORSYNC_DEFAULT);
		ignoreIfInCreativeData = plugin.getConfig().getBoolean(IGNOREIFINCREATIVE_KEY, IGNOREIFINCREATIVE_DEFAULT);
		ignoreIfSneakingData = plugin.getConfig().getBoolean(IGNOREIFSNEAKING_KEY, IGNOREIFSNEAKING_DEFAULT);

		// the actual blocks to interact with		
		List<String> trapDoorsInScopeStrings = plugin.getConfig().getStringList(TRAPDOORSINSCOPE_KEY);
		List<String> gatesInScopeStrings = plugin.getConfig().getStringList(GATESINSCOPE_KEY);
		List<String> doorsInScopeStrings = plugin.getConfig().getStringList(DOORSINSCOPE_KEY);

		// convert string to material
		trapDoorsInScopeData.clear();
		gatesInScopeData.clear();
		doorsInScopeData.clear();
		
		for (String s : trapDoorsInScopeStrings) {
			Material m = Material.matchMaterial (s);	
			if (m != null) {
				trapDoorsInScopeData.add(m);
			} else {
				printUnknownMaterialWarning(s, "trap door");
			}
		}
		
		for (String s : gatesInScopeStrings) {
			Material m = Material.matchMaterial(s);
			if (m != null) {
				gatesInScopeData.add(m);
			} else {
				printUnknownMaterialWarning(s, "gate");
			}
		}
		
		for (String s : doorsInScopeStrings) {
			Material m = Material.matchMaterial(s);
			if (m != null) {
				doorsInScopeData.add(m);
			} else {
				printUnknownMaterialWarning(s, "door");
			}
		}
	}
	
	protected void dumpConfigValues(boolean verbose) {
		if (trapDoorsInScopeData.isEmpty() && gatesInScopeData.isEmpty() && doorsInScopeData.isEmpty()) {
			plugin.getLogger().warning("No doors, gates, or trap doors configured to auto-close. Is the config file up to date?" );
			plugin.getLogger().warning("The DoorCloser plugin will still run and consume resources.");
			plugin.getLogger().warning("Update the configuration file and then use the '/doorcloser reload' command to reload it.");
		}
		else
		{
			if (verbose) { 
				plugin.getLogger().info("Count of trap doors in scope: " + trapDoorsInScopeData.size());
				plugin.getLogger().info("Count of gate types in scope: " + gatesInScopeData.size());
				plugin.getLogger().info("Count of door types in scope: " + doorsInScopeData.size());
			}
		}
		if (verbose) {
			plugin.getLogger().info("Seconds to remain open: " + secondsToRemainOpenData);
			plugin.getLogger().info("Ignore if in creative mode: " + ignoreIfInCreativeData);
			plugin.getLogger().info("Ignore if sneaking: " + ignoreIfSneakingData);
			plugin.getLogger().info("Play sound: " + playSoundData);
		}
	}
	
	public void reload() {
		plugin.reloadConfig();
		readConfigValues();
		dumpConfigValues(true);
		plugin.getLogger().info("Settings reloaded from configuration file.");
	}

}
