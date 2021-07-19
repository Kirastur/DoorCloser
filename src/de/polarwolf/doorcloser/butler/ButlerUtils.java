// This work contains code from the original DoorCloser-Plugin written by Psychlist1972
// He has released his work using the Apache 2.0 license
// Please see https://github.com/Psychlist1972/Minecraft-DoorCloser for reference

package de.polarwolf.doorcloser.butler;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;

public class ButlerUtils {
	

	private ButlerUtils() {
		throw new IllegalStateException("Utility class");
	}


	// Gets the Openable from a specific block.
	// Returns null if the given block is not an openable.
	public static Openable getOpenableFromBlock(Block block) {
		if (block != null) {
			BlockData data = block.getBlockData();
			if (data instanceof Openable) {
				return (Openable)data;
			}
		}
		return null;
	}


	// Gets the door from a specific block.
	// Returns null if the given block is not a door.
	public static Door getDoorFromBlock(Block block) {
		if (block != null) {
			BlockData data = block.getBlockData();
			if (data instanceof Door) {
				return (Door)data;
			}
		}
		return  null;
	}
	

	public static boolean isOppositeHinge(Hinge hinge, Hinge pairedHinge) {
		return (hinge == Hinge.LEFT && pairedHinge == Hinge.RIGHT) ||
			   (hinge == Hinge.RIGHT && pairedHinge == Hinge.LEFT);		
	}
	

	public static Block findPairedBlock(Block doorBlock, Hinge hinge, BlockFace face) {
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

}
