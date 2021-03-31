// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class DoorCloserTabCompleter implements TabCompleter {

	protected final Plugin plugin;
	
	public DoorCloserTabCompleter(Plugin plugin) {
		this.plugin=plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> commandNames = new ArrayList<>();
		if (args.length == 1) {
			commandNames.add("debugdisable");
			commandNames.add("debugenable");
			commandNames.add("reload");
		}
		return commandNames;
	}

}
