// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.scheduler;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.doorcloser.butler.ButlerManager;
import de.polarwolf.doorcloser.exception.DoorCloserException;

public class DoorCloseTask extends BukkitRunnable{
	
	protected final ButlerManager butlerManager;
	protected final Block clickedBlock;
	protected final Block pairedDoorBlock;
	

	public DoorCloseTask(ButlerManager butlerManager, Block clickedBlock, Block pairedDoorBlock) {
		this.butlerManager = butlerManager;
		this.clickedBlock = clickedBlock;
		this.pairedDoorBlock = pairedDoorBlock;	
	}
	

	public void handleClose(boolean isCancel) throws DoorCloserException {
		// Sanity check
		if (clickedBlock == null) {
			throw new DoorCloserException (null, "NULL block passed into handleClose", null);
		}
		
		boolean changedClickedOpen = butlerManager.closeDoor(clickedBlock);
		boolean changedPairedOpen = butlerManager.synchronizePairedDoor(pairedDoorBlock, false);
		if ((changedClickedOpen || changedPairedOpen) && !isCancel) {
			butlerManager.playCloseNoise(clickedBlock);
		}
	}
	

	@Override
	public void run() {
		try {
			butlerManager.printDebug("DEBUG: Schedule started.");
			butlerManager.removeDoorCloseTast(this);
			handleClose(false);
			butlerManager.printDebug("DEBUG: Schedule finished.");
		} catch (DoorCloserException e) {
			butlerManager.printWarning(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
