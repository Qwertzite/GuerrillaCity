package qwertzite.guerrillacity.worldgen.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.GcConsts;

/**
 * Generates CityBlocks and sequentially place blocks for each building.
 * @author Qwertzite
 * @date 2022/10/08
 */
public class CityWard {
	public static final int WARD_SIZE_BIT = 4;
	public static final int WARD_SIZE_CHUNKS = 1 << WARD_SIZE_BIT;
	
	private final BlockPos offset;
	private final long wardSeed; // must be unique for each wards.
	
	private boolean initialising;
	private volatile boolean initialised = false;
	
	private Set<BuildingEntry> buildings;
	
	public CityWard(WardPos wardPos, long worldSeed) {
		this(wardPos.getBaseBlockPos(GcConsts.GROUND_HEIGHT), new Random(worldSeed + 17*wardPos.hashCode()).nextLong());
	}
	
	/**
	 * 
	 * @param offset The minimum x, z coordinates and y, ground level.
	 * @param wardSeed Ward specific generation seed.
	 */
	public CityWard(BlockPos offset, long wardSeed) {
		this.offset = offset;
		this.wardSeed = wardSeed;
	}
	
	/**
	 * All building elements must intersect with validArea and must not with forbiddenArea.
	 * @param validArea relative position to CityWarda
	 * @param forbiddenArea relative position to CityWard
	 */
	public synchronized void beginInitialisation(Set<BoundingBox> validArea, Set<BoundingBox> forbiddenArea) {
		assert(!this.initialised);
		assert(!this.initialising);
		this.initialising = true;
		
		
		// generate city blocks and make them do their jobs.
		// TODO: implement
		
		
		this.initialised = true;
		this.initialising = false;
		ModLog.info("Initialied city ward at " + this.offset);
	}
	
	/**
	 * 
	 * @param bb absolute coordinate
	 * @return absolute coordinates and new block states.
	 */
	public Map<BlockPos, BlockState> computeBlockStateForBoudingBox(BlockPos origin, @Nullable BoundingBox bb) {
		HashMap<BlockPos, BlockState> map = new HashMap<>();
		
		int x0 = bb.minX();
		int y0 = GcConsts.GROUND_HEIGHT;
		int z0 = bb.minZ();
//		int x0 = bb.minX() - origin.getX();
//		int y0 = 64;
//		int z0 = bb.minX() - origin.getX();
		BlockState state = Blocks.REDSTONE_BLOCK.defaultBlockState();
		
		// TODO: implement
		for (int x = 1; x < 15; x++) {
			for (int z = 1; z < 15; z++) {
				map.put(new BlockPos(x0 + x, y0 , z0 + z), state);
			}
		}
		map.put(new BlockPos(x0 + 0, y0 , z0 + 0), state);
		map.put(new BlockPos(x0 + 0, y0 , z0 +15), state);
		map.put(new BlockPos(x0 +15, y0 , z0 + 0), state);
		map.put(new BlockPos(x0 +15, y0 , z0 +15), state);
		
		return map;
//		return Map.of();
	}

	
	public BlockPos getOffset() {
		return this.offset;
	}
	
}
