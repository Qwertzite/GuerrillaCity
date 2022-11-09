package qwertzite.guerrillacity.worldgen.city;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.GcConsts;
import qwertzite.guerrillacity.core.util.math.Rectangle;
import qwertzite.guerrillacity.core.util.math.Vec2i;

/**
 * Generates CityBlocks and sequentially place blocks for each building.
 * @author Qwertzite
 * @date 2022/10/08
 */
public class CityWard {
	
	private final BlockPos offset;
	private final long wardSeed; // must be unique for each wards.
	
	private volatile boolean initialised = false;
	
	private CityGenResult result;
	
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
	public void beginInitialisation(Set<Rectangle> validArea, Set<Rectangle> forbiddenArea) {
		if (this.initialised) return;
		this.initialised = true;
		ModLog.info("Begin initialisation of city at " + this.offset);
		
		// generate city blocks and make them do their jobs.
		if (!validArea.isEmpty()) {
			EnumMap<Direction, Integer> roadMap = new EnumMap<>(Direction.class);
			roadMap.put(Direction.EAST, 100);
			roadMap.put(Direction.WEST, 100);
			roadMap.put(Direction.NORTH, 100);
			roadMap.put(Direction.SOUTH, 100);
			
			CityBlock cityBlock = new CityBlock(wardSeed, 0,
					new Rectangle(new Vec2i(offset.getX() + 1, offset.getZ() + 1), new Vec2i(CityConst.WARD_SIZE_BLOCKS - 2, CityConst.WARD_SIZE_BLOCKS - 2)),
					validArea, forbiddenArea, this.offset.getY(), roadMap);
			var fjp = new ForkJoinPool();
			cityBlock.init(fjp);
			this.result = cityBlock.postInit(fjp);
			ModLog.info("Initialied city ward at " + this.offset);
		} else {
			this.result = CityGenResult.EMPTY;
			ModLog.info("Initialised city ward with empty result. pos=" + this.offset);
		}
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("Initalised city ward" + "\n");
		sb.append("Position: " + this.offset + "\n");
		sb.append("Buildings: " + this.result.getBuildings().size() + "\n");
		sb.append("Road count: " + this.result.getRoadCount() + "\n");
		sb.append("Road length: " + this.result.getRoadElements().size()*4 + "\n");
		ModLog.info(sb.toString());
	}
	
	/**
	 * 
	 * @param genAreaBB absolute coordinate
	 * @return absolute coordinates and new block states.
	 */
	public Map<BlockPos, BlockState> computeBlockStateForBoudingBox(LevelAccessor level, @Nullable Rectangle genAreaBB) {
		CityGenContext context = new CityGenContext(level, genAreaBB);
		
		Set<RoadElement> roads = this.result.getRoadElements().stream().filter(e -> e.getCircumBox().intersects(genAreaBB)).collect(Collectors.toSet());
		roads.stream().forEach(e -> e.generateRoadBase(context));
		roads.stream().forEach(e -> e.generateRoadBody(context));
		roads.stream().forEach(e -> e.generateRoadSurface(context));
		
		this.result.getBuildings().stream().filter(e -> e.getCircumBox().intersects(genAreaBB)).forEach(e -> e.generate(context));
		this.result.getBuildings().stream().filter(
				building -> building.getCircumBox().intersects(genAreaBB)).forEach(building -> building.generate(context));
//		Map<BlockPos, BlockState> mapmap = map.entrySet().stream()
//				.filter(e -> genAreaBB.isInside(e.getKey()))
//				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		
		return context.getStateMap();
	}
	
	public BlockPos getOffset() {
		return this.offset;
	}
	
	public boolean isInitialised() {
		return this.initialised;
	}
}
