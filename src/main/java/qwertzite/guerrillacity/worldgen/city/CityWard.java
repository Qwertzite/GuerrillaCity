package qwertzite.guerrillacity.worldgen.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
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
	 * @param validArea
	 * @param forbiddenArea
	 */
	public synchronized void beginInitialisation(Set<BoundingBox> validArea, Set<BoundingBox> forbiddenArea) {
		assert(!this.initialised);
		assert(!this.initialising);
		this.initialising = true;
		
		// generate city blocks and make them do their jobs.
		
		CityBlock cityBlock = new CityBlock(wardSeed, 0,
				new BoundingBox(
						this.offset.getX() + 1, this.offset.getY(), this.offset.getZ() + 1,
						this.offset.getX() + CityConst.WARD_SIZE_BLOCKS -2, this.offset.getY() + 64, this.offset.getZ() + CityConst.WARD_SIZE_BLOCKS -2),
				validArea, forbiddenArea, this.offset.getY());
		CityGenResult result = cityBlock.init(new ForkJoinPool());
		
		this.buildings = result.getBuildings();
		
		this.initialised = true;
		this.initialising = false;
		ModLog.info("Initialied city ward at " + this.offset);
		ModLog.info("Buildings: " + this.buildings.size());
	}
	
	/**
	 * 
	 * @param genAreaBB absolute coordinate
	 * @return absolute coordinates and new block states.
	 */
	public Map<BlockPos, BlockState> computeBlockStateForBoudingBox(@Nullable BoundingBox genAreaBB) {
		Map<BlockPos, BlockState> map = new HashMap<>();
		
		this.buildings.stream().filter(
				building -> building.getCircumBox().intersects(genAreaBB)).forEach(building -> building.generate(map));
		Map<BlockPos, BlockState> mapmap = map.entrySet().stream()
				.filter(e -> genAreaBB.isInside(e.getKey()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		
		
		return mapmap;
	}
	
	public BlockPos getOffset() {
		return this.offset;
	}
}
