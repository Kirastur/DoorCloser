// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.events;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.doorcloser.utils.DoorCloserUtil;

public class DoorCloseTask extends BukkitRunnable{
	
	protected final DoorCloserUtil util;
	protected final Block clickedBlock;
	protected final Block pairedDoorBlock;
	
	public DoorCloseTask(DoorCloserUtil util, Block clickedBlock, Block pairedDoorBlock) {
		this.util = util;
		this.clickedBlock = clickedBlock;
		this.pairedDoorBlock = pairedDoorBlock;	
	}
	
	protected void handleClose() {
		if (clickedBlock == null) {
			util.printWarning("Null main door block sent to handleClose.");
			return;
		}
		
		Boolean changedClickedOpen = util.closeDoor(clickedBlock);
		Boolean changedPairedOpen = util.synchronizePairedDoor(pairedDoorBlock, false);
		if (changedClickedOpen || changedPairedOpen) {
			util.playCloseNoise(clickedBlock);
		}
	}
	
	@Override
	public void run() {
		util.printDebug("DEBUG: Schedule started.");
		handleClose();
		util.printDebug("DEBUG: Schedule finished.");
	}	

}
