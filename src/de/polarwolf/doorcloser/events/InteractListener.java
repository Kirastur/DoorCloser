// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.events;

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
import de.polarwolf.doorcloser.config.DoorCloserConfig;
import de.polarwolf.doorcloser.utils.DoorCloserUtil;

public class InteractListener implements Listener {
	
	protected final DoorCloserConfig config;
	protected final DoorCloserUtil util;
	
	public InteractListener(DoorCloserConfig config, DoorCloserUtil util) {
		this.config = config;
		this.util = util;
	}
	
	protected Block findPairedBlock(Block doorBlock, Hinge hinge, BlockFace face) {
		Block pairedDoorBlock = null;
		switch (face)
		{
			case NORTH:
				if (hinge == Hinge.LEFT) {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.EAST);	
				} else {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.WEST);	
				}
				break;

			case SOUTH:
				if (hinge == Hinge.LEFT) {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.WEST);	
				} else {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.EAST);	
				}
				break;

			case EAST:
				if (hinge == Hinge.LEFT) {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.SOUTH);	
				} else {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.NORTH);	
				}
				break;
				
			case WEST:
				if (hinge == Hinge.LEFT) {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.NORTH);	
				} else {
					pairedDoorBlock = doorBlock.getRelative(BlockFace.SOUTH);	
				}
				break;

			default:
				pairedDoorBlock = null;
				break;
		}
		return pairedDoorBlock;
	}
	
	protected boolean compareHinge(Hinge hinge, Hinge pairedHinge) {
		return (hinge == Hinge.LEFT && pairedHinge == Hinge.RIGHT) ||
			   (hinge == Hinge.RIGHT && pairedHinge == Hinge.LEFT);		
	}
		
	protected Block findPairedDoorBlockIfDoubleDoor(Block doorBlock) {
		Door door = DoorCloserUtil.getDoorFromBlock(doorBlock);
		if (door == null) {
			util.printDebug("DEBUG: Openable Block is not a door - no paird door detection needed.");
			return null;
		}

		Hinge hinge = door.getHinge();
		BlockFace face = door.getFacing();

		util.printDebug("DEBUG: door face=" + face.toString());
		util.printDebug("DEBUG: door isOpen()=" + door.isOpen());
		util.printDebug("DEBUG: door hinge=" + hinge.toString());

		Block pairedDoorBlock = findPairedBlock(doorBlock, hinge, face);
			
		if (pairedDoorBlock == null) {
			// no block next door. That would be ... odd
			// door is a single door, not double
			util.printDebug("DEBUG: Neighbor block not found ... odd.");
			return null;
		}
			
		// check the block we found that is opposite the hinge. If it
		// is a door and has a hinge that is opposite this one, then
		// it is our pair
		// check to see if that block is actually a door
		Door pairedDoor = DoorCloserUtil.getDoorFromBlock(pairedDoorBlock);
		if (pairedDoor == null) {
			// neighbor block is not a door.
			// door is a single door, not a double door
			util.printDebug("DEBUG: Neighbor block is not a door. Not a double door.");
			return null;
		}

		util.printDebug("DEBUG: Door neighbor is a door.");
		util.printDebug("DEBUG: paired door face=" + pairedDoor.getFacing().toString());
		util.printDebug("DEBUG: paired door isOpen()=" + pairedDoor.isOpen());
		util.printDebug("DEBUG: paired door hinge=" + pairedDoor.getHinge().toString());

		Hinge pairedHinge = pairedDoor.getHinge();
		if (!compareHinge(hinge, pairedHinge) ) {
			// neighbor block has hinge on same side
			// not the pair for this door
			util.printDebug("DEBUG: Neighbor has hinge on same side. Not a double door.");
			return null;			
		}
		
		util.printDebug("DEBUG: Found paired / double door.");
		// we're good!
		return pairedDoorBlock;
	}
	
	protected Openable analyzeInteraction(Block clickedBlock, Player player, Action action) {

		// right clicks only
		if (action != Action.RIGHT_CLICK_BLOCK) {
			return null;
		}

		// check to see if we're ignoring creative mode
		if ((player.getGameMode() == GameMode.CREATIVE) && (config.isIgnoreIfInCreative())) {
			return null;
		}
	
		// check to see if we're ignoring sneaking
		if ((player.isSneaking()) && (config.isIgnoreIfSneaking())) {
			return null;
		}
		
		// check to see if we care about this type of block. In our case, we want
		// something that implements Openable (gate, trap door, door).
		return DoorCloserUtil.getOpenableFromBlock(clickedBlock);
	}
	
	protected void inspectOpenable(Block clickedBlock, Openable openable) {

		util.printDebug("DEBUG: Performing DoorCloser Checks.");		
		Block pairedDoorBlock = null; 

		// Handling door-specific stuff
		// Please remember: synchronizing doors is only active if doorCloser is active,
		// but does not check about the material
		Door door = DoorCloserUtil.getDoorFromBlock(clickedBlock);
		if (door != null) {

			// check to see if they clicked the top of the door. If so, change to the block below it.
			// Necessary because server only supports the door operations on the lower block.
			// Do this only for doors so as not to mess up stacked gates or stacked trap doors
			// This could fail if you somehow manage to get two doors stacked on top of each other.
			if (door.getHalf() == Half.TOP) {
				util.printDebug("DEBUG: Handling click on top half of door");
				clickedBlock = clickedBlock.getRelative(BlockFace.DOWN);
			}

			if (config.isSynchronizeDoubleDoor()) {
				pairedDoorBlock = findPairedDoorBlockIfDoubleDoor (clickedBlock);
				if (config.isOptimisticDoubleDoorSync()) {
					util.synchronizePairedDoor(pairedDoorBlock, !openable.isOpen());
				}
			}

		}

		// We have checked all, now we can trigger the one-tick-later Task
		util.printDebug("DEBUG: Preparing one-tick-later Task.");		
		util.scheduleOneTickLaterTask(clickedBlock, pairedDoorBlock, openable.isOpen());
	}
		      				
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void blockInteract(PlayerInteractEvent e) {
		Action action = e.getAction();
		Block clickedBlock = e.getClickedBlock();
		Player player = e.getPlayer();
		Openable openable = analyzeInteraction(clickedBlock, player, action);
		if (openable != null) {
			inspectOpenable(clickedBlock, openable);
		}
	}

}
