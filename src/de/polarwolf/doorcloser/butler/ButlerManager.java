// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.butler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.plugin.Plugin;

import de.polarwolf.doorcloser.config.ConfigManager;
import de.polarwolf.doorcloser.exception.DoorCloserException;
import de.polarwolf.doorcloser.scheduler.DoorCloseTask;
import de.polarwolf.doorcloser.scheduler.OneTickLaterTask;

public class ButlerManager {
	
	public static final int TICKS_PER_SECOND = 20;
	
	protected final Plugin plugin;
	protected final ConfigManager configManager;
	
	protected List<OneTickLaterTask> oneTickLaterTasks = new ArrayList<>();
	protected List<DoorCloseTask> doorCloseTasks = new ArrayList<>();
	
	
	public ButlerManager(Plugin plugin, ConfigManager configManager) {
		this.plugin = plugin;
		this.configManager = configManager;
	}
	

	public void printDebug(String s) {
		if (configManager.isDebug()) {
			plugin.getLogger().info(s);
		}
	}
	

	public void printWarning(String s) {
		plugin.getLogger().warning(s);
	}

	
	public void playCloseNoise(Block openableBlock) {
		if ((openableBlock == null) || !configManager.getConfigData().isPlaySound()) {
			return;
		}

		BlockData data = openableBlock.getBlockData();

		if (data instanceof TrapDoor) {
			openableBlock.getWorld().playSound(openableBlock.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 1);
		} else if (data instanceof Gate) {
			openableBlock.getWorld().playSound(openableBlock.getLocation(), Sound.BLOCK_FENCE_GATE_CLOSE, 1, 1);
		} else if (data instanceof Door) {
			openableBlock.getWorld().playSound(openableBlock.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
		}
	}


	// Opens a door, gate or trapdoor.
	public boolean openDoor(Block doorBlock) throws DoorCloserException {
		if (doorBlock == null) {
			throw new DoorCloserException (null, "NULL block passed into openDoor", null);
		}

		Openable openable = ButlerUtils.getOpenableFromBlock(doorBlock);
		if (openable == null) {
			throw new DoorCloserException (null, "Bogus block type passed into openDoor", doorBlock.getType().toString());
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


	// Closes a door, gate or trapdoor.
	public boolean closeDoor(Block doorBlock) throws DoorCloserException {
		if (doorBlock == null) {
			throw new DoorCloserException (null, "NULL block passed into closeDoor", null);
		}

		Openable openable = ButlerUtils.getOpenableFromBlock(doorBlock);
		if (openable == null) {
			throw new DoorCloserException (null, "Bogus block type passed into closeDoor", doorBlock.getType().toString());
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
	

	// Synchronize both wings of a double-door
	public boolean synchronizePairedDoor(Block pairedDoorBlock, boolean destinationState) throws DoorCloserException {
		// we expect that pairedDoorBlock is only set if synchronization is enabled in config
		Door pairedDoor = ButlerUtils.getDoorFromBlock(pairedDoorBlock);
		if (pairedDoor== null) {
			return false;
		}

		boolean isPairedDoorOpen = pairedDoor.isOpen();
		if (isPairedDoorOpen == destinationState) {
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
	
	
	public void scheduleOneTickLaterTask(Block clickedBlock, Block pairedDoorBlock, boolean openState) {
		OneTickLaterTask task = new OneTickLaterTask(configManager, this, clickedBlock, pairedDoorBlock, openState);
		task.runTask(plugin);
		oneTickLaterTasks.add(task);
	}


	public void scheduleCloseTask(Block clickedBlock, Block pairedDoorBlock, int seconds) {
		DoorCloseTask task = new DoorCloseTask(this, clickedBlock, pairedDoorBlock);
		task.runTaskLater(plugin, (long)seconds * TICKS_PER_SECOND);
		doorCloseTasks.add(task);
	} 
	
	
	public void removeOneTickLaterTask (OneTickLaterTask task) {
		oneTickLaterTasks.remove(task);
	}
	
	
	public void removeDoorCloseTast (DoorCloseTask task) {
		doorCloseTasks.remove(task);
	}
	
	
	protected void cancelOneTickLaterTask() throws DoorCloserException {
		List<OneTickLaterTask> myList = new ArrayList<>(oneTickLaterTasks);
		for (OneTickLaterTask myTask : myList) {
			myTask.cancel();
			myTask.handleInteraction(true);
		}
	}
	
	
	protected void cancelDoorCloseTask() throws DoorCloserException {
		List<DoorCloseTask> myList = new ArrayList<>(doorCloseTasks);
		for (DoorCloseTask myTask : myList) {
			myTask.cancel();
			myTask.handleClose(true);
		}
	}
	
	
	public void cancelAll() {
		try {
			printDebug("Cancelling all");
			cancelOneTickLaterTask();
			cancelDoorCloseTask();
 		} catch (DoorCloserException e) {
			printWarning(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public int getTaskCount() {
		return oneTickLaterTasks.size() + doorCloseTasks.size();
	}

}
