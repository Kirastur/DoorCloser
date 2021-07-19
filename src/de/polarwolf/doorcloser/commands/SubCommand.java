package de.polarwolf.doorcloser.commands;

public enum SubCommand {

	HELP ("help"),
	DEBUGENABLE ("debugenable"),
	DEBUGDISABLE ("debugdisable"),
	DUMP ("dump"),
	INFO ("info"),
	RELOAD ("reload");

	private final String command;


	private SubCommand(String command) {
		this.command = command;
	}


	public String getCommand() {
		return command;
	}

}
