// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.scheduler;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.doorcloser.butler.ButlerManager;
import de.polarwolf.doorcloser.butler.ButlerUtils;
import de.polarwolf.doorcloser.config.ConfigManager;
import de.polarwolf.doorcloser.exception.DoorCloserException;

public class OneTickLaterTask extends BukkitRunnable{

	protected final ConfigManager configManager;
	protected final ButlerManager butlerManager;
	protected final Block clickedBlock;
	protected final Block pairedDoorBlock;
	protected final boolean oldOpenState;
	

	public OneTickLaterTask(ConfigManager configManager, ButlerManager butlerManager, Block clickedBlock, Block pairedDoorBlock, boolean oldOpenState) {
		this.configManager = configManager;
		this.butlerManager = butlerManager;
		this.clickedBlock = clickedBlock;
		this.pairedDoorBlock = pairedDoorBlock;
		this.oldOpenState = oldOpenState;
	}
	
	
	public void handleInteraction(boolean isCancel) throws DoorCloserException {
		// Sanity check
		if (clickedBlock == null) {
			throw new DoorCloserException (null, "NULL block passed into handleInteraction", null);
		}

		// Check if openable
		BlockData blockData = clickedBlock.getBlockData();
		Material blockDoorMaterial = blockData.getMaterial();
		Openable openable = ButlerUtils.getOpenableFromBlock(clickedBlock);
		if (openable == null) {
			throw new DoorCloserException (null, "Bogus block type passed into handleInteraction", clickedBlock.getType().toString());
		}
		
		// Please remember: synchronizing doors is only active if doorCloser is active,
		// but does not check about the material
		boolean newOpenState = openable.isOpen();
		butlerManager.synchronizePairedDoor(pairedDoorBlock, newOpenState);
		
		// Check if state has changed since last tick, which means the open/close was accepted by the server
		if (oldOpenState == newOpenState) {
			butlerManager.printDebug("DEBUG: Door open status was not changed - ignoring.");
			return;			
		}
		
		// Check if door was closed by the player
		if (!newOpenState) {
			butlerManager.printDebug("DEBUG: " + blockDoorMaterial.toString() + " was closed - ignoring.");
			return;
		}

		// check to see if it is a type of block we want to close. 
		if (!configManager.getConfigData().getOpenablesInScope().contains(blockDoorMaterial)) {
			butlerManager.printDebug("DEBUG: Material is not in scope: " + blockDoorMaterial.toString());
			return;			
		}
		
		// All prerequisites are fulfilled, so lets trigger the lateron close
		// We expect the paired block is null if type is not door (e.g. gate or trapdoor)
		if (isCancel) {
			butlerManager.closeDoor(clickedBlock);
			butlerManager.synchronizePairedDoor(pairedDoorBlock, false);
			butlerManager.printDebug("DEBUG: Closer for " + blockDoorMaterial.toString() + " cancelled.");
		} else {
			butlerManager.scheduleCloseTask(clickedBlock, pairedDoorBlock, configManager.getConfigData().getSecondsToRemainOpen());
			butlerManager.printDebug("DEBUG: Closer for " + blockDoorMaterial.toString() + " scheduled.");
		}
	}


	@Override
	public void run() {
		try {
			butlerManager.printDebug("DEBUG: DoorCloser triggered.");
			butlerManager.removeOneTickLaterTask(this);
			handleInteraction(false);
		} catch (DoorCloserException e) {
			butlerManager.printWarning(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
