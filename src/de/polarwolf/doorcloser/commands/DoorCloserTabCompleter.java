package de.polarwolf.doorcloser.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class DoorCloserTabCompleter implements TabCompleter {

	protected final DoorCloserCommand doorCloserCommand;
	

	public DoorCloserTabCompleter(DoorCloserCommand doorCloserCommand) {
		this.doorCloserCommand = doorCloserCommand;
	}
	

	public List<String> handleTabComplete(String[] args) {
		if (args.length == 0) {
			return new ArrayList<>();			
		}

		if (args.length==1) {
			return doorCloserCommand.listCommands();
		}

		return new ArrayList<>();			
	}
	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return handleTabComplete(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();			
	}	

}
