// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.listener;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.polarwolf.doorcloser.butler.ButlerManager;
import de.polarwolf.doorcloser.butler.ButlerUtils;
import de.polarwolf.doorcloser.config.ConfigManager;
import de.polarwolf.doorcloser.exception.DoorCloserException;

public class PlayerInteractionListener implements Listener {
	
	protected final ConfigManager configManager;
	protected final ButlerManager butlerManager;
	

	public PlayerInteractionListener(ConfigManager configManager, ButlerManager butlerManager) {
		this.configManager = configManager;
		this.butlerManager = butlerManager;
	}
	
	
	protected Block findPairedDoorBlock(Block doorBlock) {
		Door door = ButlerUtils.getDoorFromBlock(doorBlock);
		if (door == null) {
			butlerManager.printDebug("DEBUG: Block is not a door - no paired door detection needed.");
			return null;
		}

		Hinge hinge = door.getHinge();
		BlockFace face = door.getFacing();

		butlerManager.printDebug("DEBUG: door face=" + face.toString());
		butlerManager.printDebug("DEBUG: door isOpen()=" + door.isOpen());
		butlerManager.printDebug("DEBUG: door hinge=" + hinge.toString());

		Block pairedDoorBlock = ButlerUtils.findPairedBlock(doorBlock, hinge, face);
			
		if (pairedDoorBlock == null) {
			// no block next door. That would be ... odd
			// door is a single door, not double
			butlerManager.printDebug("DEBUG: Neighbor block not found ... odd.");
			return null;
		}
			
		// check the block we found that is opposite the hinge. If it
		// is a door and has a hinge that is opposite this one, then
		// it is our pair
		// check to see if that block is actually a door
		Door pairedDoor = ButlerUtils.getDoorFromBlock(pairedDoorBlock);
		if (pairedDoor == null) {
			// neighbor block is not a door.
			// door is a single door, not a double door
			butlerManager.printDebug("DEBUG: Neighbor block is not a door. So this is not a double door.");
			return null;
		}

		butlerManager.printDebug("DEBUG: Door neighbor is a door.");
		butlerManager.printDebug("DEBUG: paired door face=" + pairedDoor.getFacing().toString());
		butlerManager.printDebug("DEBUG: paired door isOpen()=" + pairedDoor.isOpen());
		butlerManager.printDebug("DEBUG: paired door hinge=" + pairedDoor.getHinge().toString());

		Hinge pairedHinge = pairedDoor.getHinge();
		if (!ButlerUtils.isOppositeHinge(hinge, pairedHinge) ) {
			// neighbor block has hinge on same side
			// not the pair for this door
			butlerManager.printDebug("DEBUG: Neighbor has hinge on same side. Not a double door.");
			return null;			
		}
		
		butlerManager.printDebug("DEBUG: Found paired / double door.");
		// we're good!
		return pairedDoorBlock;
	}
	

	protected Openable findOpenableFromInteraction(Block clickedBlock, Player player, Action action) {

		// right clicks only
		if (action != Action.RIGHT_CLICK_BLOCK) {
			return null;
		}

		// check to see if we're ignoring creative mode
		if ((player.getGameMode() == GameMode.CREATIVE) && (configManager.getConfigData().isIgnoreIfInCreative())) {
			return null;
		}
	
		// check to see if we're ignoring sneaking
		if ((player.isSneaking()) && (configManager.getConfigData().isIgnoreIfSneaking())) {
			return null;
		}
		
		// check to see if we care about this type of block. In our case, we want
		// something that implements Openable (gate, trap door, door).
		return ButlerUtils.getOpenableFromBlock(clickedBlock);
	}
	

	protected void handleOpenable(Block clickedBlock, Openable openable) throws DoorCloserException {

		butlerManager.printDebug("DEBUG: Performing DoorCloser Checks.");		
		Block pairedDoorBlock = null; 

		// Handling door-specific stuff
		// Please remember: synchronizing doors is only active if doorCloser is active,
		// but does not check about the material
		Door door = ButlerUtils.getDoorFromBlock(clickedBlock);
		if (door != null) {

			// check to see if they clicked the top of the door. If so, change to the block below it.
			// Necessary because server only supports the door operations on the lower block.
			// Do this only for doors so as not to mess up stacked gates or stacked trap doors
			// This could fail if you somehow manage to get two doors stacked on top of each other.
			if (door.getHalf() == Half.TOP) {
				butlerManager.printDebug("DEBUG: Handling click on top half of door");
				clickedBlock = clickedBlock.getRelative(BlockFace.DOWN);
			}

			if (configManager.getConfigData().isSynchronizeDoubleDoor()) {
				pairedDoorBlock = findPairedDoorBlock (clickedBlock);
				if (configManager.getConfigData().isOptimisticDoubleDoorSync()) {
					butlerManager.synchronizePairedDoor(pairedDoorBlock, !openable.isOpen());
				}
			}

		}

		// We have checked all, now we can trigger the one-tick-later Task
		butlerManager.printDebug("DEBUG: Preparing one-tick-later Task.");		
		butlerManager.scheduleOneTickLaterTask(clickedBlock, pairedDoorBlock, openable.isOpen());
	}
		      				
	
	protected void handlePlayerInteractionEvent(PlayerInteractEvent e) throws DoorCloserException {
		Block clickedBlock = e.getClickedBlock();
		Player player = e.getPlayer();
		Action action = e.getAction();
		Openable openable = findOpenableFromInteraction(clickedBlock, player, action);
		if (openable != null) {
			handleOpenable(clickedBlock, openable);
		}
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractionEvent(PlayerInteractEvent event) {
		try {
			handlePlayerInteractionEvent(event);
		} catch (DoorCloserException e) {
			butlerManager.printWarning(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
