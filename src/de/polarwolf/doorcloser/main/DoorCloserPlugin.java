// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.main;

import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.doorcloser.bstats.MetricsLite;
import de.polarwolf.doorcloser.commands.DoorCloserCommand;
import de.polarwolf.doorcloser.commands.DoorCloserTabCompleter;
import de.polarwolf.doorcloser.config.DoorCloserConfig;
import de.polarwolf.doorcloser.events.InteractListener;
import de.polarwolf.doorcloser.utils.DoorCloserUtil;

public final class DoorCloserPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		
		// Load Config
		DoorCloserConfig doorCloserConfig = new DoorCloserConfig(this);
		
		// Create Utils
		DoorCloserUtil doorCloserUtil = new DoorCloserUtil (this, doorCloserConfig);
		
		// Enable Commands
		getCommand("doorcloser").setExecutor(new DoorCloserCommand(this, doorCloserConfig));
		getCommand("doorcloser").setTabCompleter(new DoorCloserTabCompleter(this));

		// Listen for DoorOpen Events
		getServer().getPluginManager().registerEvents(new InteractListener(doorCloserConfig, doorCloserUtil), this);
		
		// Enable bStats Metrics
		// Please download the bstats-code direct form their homepage
		// or disable the following instruction
		new MetricsLite(this, MetricsLite.PLUGINID_DOORCLOSER);

		// Initialization is done, print message
		getLogger().info("The butler is now instructed to close the doors behind you");
		
	}

}
