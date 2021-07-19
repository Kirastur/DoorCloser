// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.doorcloser.api.DoorCloserAPI;
import de.polarwolf.doorcloser.bstats.Metrics;
import de.polarwolf.doorcloser.butler.ButlerManager;
import de.polarwolf.doorcloser.commands.DoorCloserCommand;
import de.polarwolf.doorcloser.commands.DoorCloserTabCompleter;
import de.polarwolf.doorcloser.commands.Message;
import de.polarwolf.doorcloser.config.ConfigManager;
import de.polarwolf.doorcloser.exception.DoorCloserException;
import de.polarwolf.doorcloser.listener.ListenManager;

public final class Main extends JavaPlugin {
	
	public static final String COMMAND_NAME = "doorcloser";

	protected ConfigManager configManager;
	protected ButlerManager butlerManager;
	protected ListenManager listenManager;
	protected DoorCloserAPI doorCloserAPI;
	protected DoorCloserCommand doorCloserCommand;
	protected DoorCloserTabCompleter doorCloserTabCompleter;
	

	@Override
	public void onEnable() {
		
		// Prepare Configuration
		saveDefaultConfig();
		
		// Create ConfigManager
		configManager = new ConfigManager(this);
		
		// Create ButlerManager
		butlerManager = new ButlerManager(this, configManager);
		
		// Register EventHandler (Listener)
		listenManager = new ListenManager (this, configManager, butlerManager);
		listenManager.registerListener();
	    
	    // Initialize the API
	    doorCloserAPI = new DoorCloserAPI(this, configManager, butlerManager);
		DoorCloserProvider.setAPI(doorCloserAPI);
		
		// Register Command and TabCompleter
		doorCloserCommand = new DoorCloserCommand(this, doorCloserAPI);
		getCommand(COMMAND_NAME).setExecutor(doorCloserCommand);
		doorCloserTabCompleter = new DoorCloserTabCompleter(doorCloserCommand);
		getCommand(COMMAND_NAME).setTabCompleter(doorCloserTabCompleter);
		
		// Enable bStats Metrics
		// Please download the bstats-code direct form their homepage
		// or disable the following instruction
		new Metrics(this, Metrics.PLUGINID_DOORCLOSER);

		// Load Configuration Section
		try {
			configManager.reload();
		} catch (DoorCloserException e) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR " + e.getMessage());
			getLogger().warning(Message.LOAD_ERROR.getMessage());
			return;
		}
		
		if (configManager.getConfigData().getOpenablesInScope().isEmpty()) {
			getLogger().warning("No doors, gates, or trap doors configured to auto-close. Is the config file up to date?" );
			getLogger().warning("The DoorCloser plugin will still run and consume resources.");
			getLogger().warning("Update the configuration file and then use the '/doorcloser reload' command to reload it.");
			return;
		}
		
		getLogger().info(Message.READY.getMessage());
		
	}


	@Override
	public void onDisable() {
		DoorCloserProvider.setAPI(null);

		if (listenManager != null) {
			listenManager.unregisterListener();
		}
		
		if ((butlerManager != null) && (butlerManager.getTaskCount() > 0)) {
			butlerManager.cancelAll();
		}
		
		getLogger().info(Message.FINISH.getMessage());
	}
	
}
