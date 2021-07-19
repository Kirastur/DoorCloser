package de.polarwolf.doorcloser.commands;

public enum Message {

	OK ("OK"),
	ERROR ("ERROR"),
	JAVA_EXCEPTOPN ("Java Exception Error"),
	UNKNOWN_PARAMETER ("Unknown parameter"),
	TOO_MANY_PARAMETERS ("Too many parameters"),
	HELP ("Valid commands are: "),
	INFO ("Currently running tasks: "),
	RELOAD_DONE ("Settings reloaded from configuration file"),
	READY ("The butler is now instructed to close the doors behind you"),
	FINISH ("The butler is off work now"),
	LOAD_ERROR ("Error loading configuration");
	
	private final String messageText;
	

	private Message(String messageText) {
		this.messageText = messageText;
	}

	
	public String getMessage() {
		return messageText;
	}

}
