// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference


package de.polarwolf.doorcloser.utils;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.plugin.Plugin;

import de.polarwolf.doorcloser.config.DoorCloserConfig;
import de.polarwolf.doorcloser.events.DoorCloseTask;
import de.polarwolf.doorcloser.events.OneTickLaterTask;

public class DoorCloserUtil {
	
	protected final Plugin plugin;
	protected final DoorCloserConfig config;

	protected static final int TICKS_PER_SECOND = 20;

	public DoorCloserUtil(Plugin plugin, DoorCloserConfig config) {
		this.plugin = plugin;
		this.config = config;
	}

	public void printDebug(String s) {
		if (config.isDebug()) {
			plugin.getLogger().info(s);
		}
	}
	
	public void printWarning(String s) {
		plugin.getLogger().warning(s);
	}

	// handles getting the Openable from a specific block
	// returns null if not an openable
	public static Openable getOpenableFromBlock(Block block) {
		if (block != null) {
			BlockData data = block.getBlockData();
			if (data instanceof Openable) {
				return (Openable)data;
			}
		}
		return null;
	}

	// handles getting the door from a specific block
	// returns null if not a door
	public static Door getDoorFromBlock(Block block) {
		if (block != null) {
			BlockData data = block.getBlockData();
			if (data instanceof Door) {
				return (Door)data;
			}
		}
		return  null;
	}
	
	public void playCloseNoise(Block openableBlock) {
		if (openableBlock != null) {
			BlockData data = openableBlock.getBlockData();
			if (config.isPlaySound()) {
				if (data instanceof TrapDoor) {
					openableBlock.getWorld().playSound(openableBlock.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 1);
				} else if (data instanceof Gate) {
					openableBlock.getWorld().playSound(openableBlock.getLocation(), Sound.BLOCK_FENCE_GATE_CLOSE, 1, 1);
				} else if (data instanceof Door) {
					openableBlock.getWorld().playSound(openableBlock.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
				}

			}
		}
	}

	public Boolean openDoor(Block doorBlock) {
		Openable openable = getOpenableFromBlock(doorBlock);
		if (openable == null) {
			printWarning("Bogus block type passed into openDoor.");
			return false;			
		}
		if (openable.isOpen()) {
			printDebug("DEBUG: Door is already open - ignoring.");
			return false;
		}
		openable.setOpen(true);
		doorBlock.setBlockData(openable);
		printDebug("DEBUG: Opening door.");
		return true;
	}

	public Boolean closeDoor(Block doorBlock) {
		Openable openable = getOpenableFromBlock(doorBlock);
		if (openable == null) {
			printWarning("Bogus block type passed into closeDoor.");
			return false;			
		}
		if (!openable.isOpen()) {
			printDebug("DEBUG: Door is already closed - ignoring.");
			return false;
		}
		openable.setOpen(false);
		doorBlock.setBlockData(openable);
		printDebug("DEBUG: Closing door.");
		return true;
	}
	
	public Boolean synchronizePairedDoor(Block pairedDoorBlock, Boolean destinationState) {
		// we expect that pairedDoorBlock is only set if synchronization is enabled in config
		Door pairedDoor = getDoorFromBlock(pairedDoorBlock);
		if (pairedDoor== null) {
			return false;
		}
		Boolean isPairedDoorOpen = pairedDoor.isOpen();
		if (isPairedDoorOpen.equals(destinationState)) {
			return false;
		}
		if (destinationState) {
			printDebug("DEBUG: Synchronizing paired door to open.");			
			openDoor(pairedDoorBlock);
		} else {
			printDebug("DEBUG: Synchronizing paired door to close.");			
			closeDoor(pairedDoorBlock);
		}
		return true;
	}

	public void scheduleOneTickLaterTask(Block clickedBlock, Block pairedDoorBlock, Boolean openState) {
		OneTickLaterTask task = new OneTickLaterTask(config, this, clickedBlock, pairedDoorBlock, openState);
		task.runTask(plugin);
	}

	public void scheduleCloseTask(Block door1Block, Block pairedDoorBlock, int seconds) {
		DoorCloseTask task = new DoorCloseTask(this, door1Block, pairedDoorBlock);
		task.runTaskLater(plugin, (long)seconds * TICKS_PER_SECOND);
	} 

}
