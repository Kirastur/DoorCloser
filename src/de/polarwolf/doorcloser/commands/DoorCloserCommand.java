package de.polarwolf.doorcloser.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.polarwolf.doorcloser.api.DoorCloserAPI;
import de.polarwolf.doorcloser.exception.DoorCloserException;
import de.polarwolf.doorcloser.main.Main;

public class DoorCloserCommand implements CommandExecutor {

	protected final Main main;
	protected final DoorCloserAPI doorCloserAPI;

	
	public DoorCloserCommand(Main main, DoorCloserAPI doorCloserAPI) {
		this.main = main;
		this.doorCloserAPI = doorCloserAPI;
	}
	

	public List<String> listCommands() {
		List<String> names = new ArrayList<>();
		for (SubCommand subCommand : SubCommand.values()) {
			names.add(subCommand.getCommand());
		}
		return names;
	}

	
	protected void cmdHelp(CommandSender sender) {
		String s = String.join(" ", listCommands());
		sender.sendMessage(Message.HELP.getMessage() + s);
	}
	
	
	protected void cmdDump(CommandSender sender) {
		Set<String> names = new TreeSet<>();
		for (Material material : doorCloserAPI.getOpenablesInScope()) {
			names.add(material.name());
		}
		String s = String.join(" ", names);
		sender.sendMessage(s);
		
	}
	
	
	protected void cmdInfo(CommandSender sender) {
		sender.sendMessage(Message.INFO.getMessage() + Integer.toString(doorCloserAPI.getTaskCount()));
	}
	
	
	protected void cmdReload(CommandSender sender) throws DoorCloserException {
		doorCloserAPI.reload();
		sender.sendMessage(Message.RELOAD_DONE.getMessage());
	}
	

	protected void cmdDebugEnable() {
		doorCloserAPI.setDebug(true);
	}


	protected void cmdDebugDisable() {
		doorCloserAPI.setDebug(false);
	}


	protected void dispatchCommand(CommandSender sender, SubCommand subCommand) throws DoorCloserException {
		switch (subCommand) {
			case HELP:			cmdHelp(sender);
								break;
			case DEBUGENABLE:	cmdDebugEnable();
								break;
			case DEBUGDISABLE:	cmdDebugDisable();
								break;
			case DUMP:			cmdDump(sender);
								break;
			case INFO:			cmdInfo(sender);
								break;
			case RELOAD:		cmdReload(sender);
								break;
			default: sender.sendMessage(Message.ERROR.getMessage());
		}
	}


	public SubCommand findSubCommand(String subCommandName) {
		for (SubCommand subCommand : SubCommand.values()) {
			if (subCommand.getCommand().equalsIgnoreCase(subCommandName)) {
				return subCommand;
			}
		}
		return null;
	}
	

	public boolean handleCommand(CommandSender sender, String[] args) throws DoorCloserException {
		if (args.length==0) {
			return false;
		}

		String subCommandName=args[0];
		SubCommand subCommand = findSubCommand(subCommandName);
		if (subCommand == null) {
			sender.sendMessage(Message.UNKNOWN_PARAMETER.getMessage());
			return true;
		}
		
		if (args.length > 1) {
			sender.sendMessage(Message.TOO_MANY_PARAMETERS.getMessage());
			return true;
		}			

		dispatchCommand (sender, subCommand);
		return true;
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return handleCommand(sender, args);
		} catch (DoorCloserException e) {
			main.getLogger().warning(Message.ERROR.getMessage()+ " " + e.getMessage());
			sender.sendMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(Message.JAVA_EXCEPTOPN.getMessage());
		}

		return true;
	}

}
