// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import de.polarwolf.doorcloser.exception.DoorCloserException;

public class ConfigManager {

	protected final Plugin plugin;
	protected ConfigData configData = new ConfigData();
	protected boolean debug = false;

	
	public ConfigManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	
	public ConfigData getConfigData() {
		return configData;
	}
	
	
	public boolean isDebug() {
		return debug;
	}


	public void setDebug(boolean newDebug) {
		if (debug != newDebug) {
			debug = newDebug;
			if (newDebug) {
				plugin.getLogger().info("Debug is enabled.");
			} else {
				plugin.getLogger().info("Debug is disabled.");
			}
		}
	}
	
	
	protected void dumpConfigData() {
		plugin.getLogger().info("Seconds to remain open: " + configData.getSecondsToRemainOpen());
		plugin.getLogger().info("Ignore if in creative mode: " + configData.isIgnoreIfInCreative());
		plugin.getLogger().info("Ignore if sneaking: " + configData.isIgnoreIfSneaking());
		plugin.getLogger().info("Play sound: " + configData.isPlaySound());
		plugin.getLogger().info("Synchronize DoubleDoor: " + configData.isSynchronizeDoubleDoor());
		plugin.getLogger().info("Optimistic DoubleDoor sync: " + configData.isOptimisticDoubleDoorSync());
		plugin.getLogger().info("Count of doors, gates and trapdoors in scope: " + configData.openablesInScope.size());
	}

	
	public void reload() throws DoorCloserException {
		ConfigurationSection config = plugin.getConfig(); 
		ConfigData newConfigData = new ConfigData();
		newConfigData.load(config);
		configData = newConfigData;
		if (debug) {
			dumpConfigData();
		}
	}
	
}
