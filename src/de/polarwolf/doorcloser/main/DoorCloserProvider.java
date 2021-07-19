package de.polarwolf.doorcloser.main;

import de.polarwolf.doorcloser.api.DoorCloserAPI;

public class DoorCloserProvider {

	private static DoorCloserAPI doorCloserAPI;
	
	private DoorCloserProvider() {
	}

	protected static void setAPI (DoorCloserAPI newAPI) {
		doorCloserAPI=newAPI;
	}
		    
	public static DoorCloserAPI getAPI() {
		return doorCloserAPI;
	}

}
