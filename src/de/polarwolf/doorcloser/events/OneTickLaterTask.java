// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.doorcloser.config.DoorCloserConfig;
import de.polarwolf.doorcloser.utils.DoorCloserUtil;

public class OneTickLaterTask extends BukkitRunnable{

	protected final DoorCloserConfig config;
	protected final DoorCloserUtil util;
	protected final Block clickedBlock;
	protected final Block pairedDoorBlock;
	protected final Boolean oldOpenState;
	
	public OneTickLaterTask(DoorCloserConfig config, DoorCloserUtil util, Block clickedBlock, Block pairedDoorBlock, Boolean oldOpenState) {
		this.config = config;
		this.util = util;
		this.clickedBlock = clickedBlock;
		this.pairedDoorBlock = pairedDoorBlock;
		this.oldOpenState = oldOpenState;
	}

	protected void handleTrapDoorInteraction(Boolean isOpen) {
		if (isOpen) {
			util.scheduleCloseTask(clickedBlock, null, config.getSecondsToRemainOpen());
			util.printDebug("DEBUG: Closer for TrapDoor scheduled.");
		} else {
			util.printDebug("DEBUG: Trapdor was closed - ignoring.");
		}
	}

	protected void handleGateInteraction(Boolean isOpen) {
		if (isOpen) {
			util.scheduleCloseTask(clickedBlock, null, config.getSecondsToRemainOpen());
			util.printDebug("DEBUG: Closer for Gate scheduled.");
		} else {
			util.printDebug("DEBUG: Gate was closed - ignoring.");
		}
	}

	protected void handleDoorInteraction(Boolean isOpen) {
		if (isOpen) {
			util.scheduleCloseTask(clickedBlock, pairedDoorBlock, config.getSecondsToRemainOpen());
			util.printDebug("DEBUG: Closer for Door scheduled.");
		} else {
			util.printDebug("DEBUG: Door was closed - ignoring.");
		}
	}
	
	public void handleInteraction() {
		BlockData blockData = clickedBlock.getBlockData();
		Material blockDoorType = blockData.getMaterial();

		Openable openable = DoorCloserUtil.getOpenableFromBlock(clickedBlock);
		if (openable == null) {
			util.printWarning("Bogus block type passed into handleInteraction.");
			return;
		}
		
		Boolean newOpenState = openable.isOpen();

		util.synchronizePairedDoor(pairedDoorBlock, newOpenState);
		
		if (oldOpenState.equals(newOpenState)) {
			util.printDebug("DEBUG: Door open status was not changed - ignoring.");
			return;			
		}

		// check to see if it is a type of block we want to close. 
				if (blockData instanceof TrapDoor && config.getTrapDoorsInScope().contains(blockDoorType)) {
			handleTrapDoorInteraction(newOpenState);
			return;
		}
				 
		if (blockData instanceof Gate && config.getGatesInScope().contains(blockDoorType)) {
			handleGateInteraction(newOpenState);
			return;
		}

		if (blockData instanceof Door && config.getDoorsInScope().contains(blockDoorType)) {
			handleDoorInteraction(newOpenState);
			return;
		}

		util.printDebug("DEBUG: Material is not in scope.");		
	}

	@Override
	public void run() {
		util.printDebug("DEBUG: DoorCloser triggered.");
		handleInteraction();
	}	

}
