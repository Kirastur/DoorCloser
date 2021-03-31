// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.polarwolf.doorcloser.config.DoorCloserConfig;

public class DoorCloserCommand implements CommandExecutor {

	protected final Plugin plugin;
	protected final DoorCloserConfig doorCloserConfig;
	
	public DoorCloserCommand(Plugin plugin, DoorCloserConfig doorCloserConfig) {
		this.plugin = plugin;
		this.doorCloserConfig = doorCloserConfig;
	}
	
	protected void cmdReload() {
		doorCloserConfig.reload();
	}
	
	protected void cmdDebugEnable() {
		doorCloserConfig.setDebug(true);
	}

	protected void cmdDebugDisable() {
		doorCloserConfig.setDebug(false);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==0) {
			return false;
		}
		String subCommand=args[0];
		if (subCommand.equalsIgnoreCase("reload")) {
			cmdReload();
			return true;
		}
		if (subCommand.equalsIgnoreCase("debugenable")) {
			cmdDebugEnable();
			return true;
		}
		if (subCommand.equalsIgnoreCase("debugdisable")) {
			cmdDebugDisable();
			return true;
		}
		return false;
	}
	
}
