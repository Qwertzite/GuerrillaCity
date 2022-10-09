package qwertzite.guerrillacity.worldgen.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	 * @param validArea
	 * @param forbiddenArea
	 */
	public synchronized void beginInitialisation(Set<BoundingBox> validArea, Set<BoundingBox> forbiddenArea) {
		assert(!this.initialised);
		assert(!this.initialising);
		this.initialising = true;
		
		
		// generate city blocks and make them do their jobs.
		// TODO: implement
		this.buildings = IntStream.range(0, 16*16/2).parallel().mapToObj(i ->  {
			Random rand = new Random();
			int xWidth = rand.nextInt(8, 32);
			int zWidth = rand.nextInt(8, 32);
			int height = rand.nextInt(4, 12);
			int posX = this.offset.getX() + rand.nextInt(1, 16*16 - xWidth - 1);
			int posZ = this.offset.getZ() + rand.nextInt(1, 16*16 - zWidth - 1);
			BoundingBox circumBB = new BoundingBox(posX, 64, posZ, posX + xWidth, 64 + height, posZ + zWidth);
			if (forbiddenArea.stream().anyMatch(bb -> bb.intersects(circumBB))) return null; // 範囲外のものがあったら
			return new BuildingEntry(circumBB);
		}).filter(e -> e != null).collect(Collectors.toSet());
		
		
		this.initialised = true;
		this.initialising = false;
		ModLog.info("Initialied city ward at " + this.offset);
	}
	
	/**
	 * 
	 * @param genAreaBB absolute coordinate
	 * @return absolute coordinates and new block states.
	 */
	public Map<BlockPos, BlockState> computeBlockStateForBoudingBox(@Nullable BoundingBox genAreaBB) {
		HashMap<BlockPos, BlockState> map = new HashMap<>();
		
//		int x0 = genAreaBB.minX();
//		int y0 = GcConsts.GROUND_HEIGHT;
//		int z0 = genAreaBB.minZ();
//		int x0 = bb.minX() - origin.getX();
//		int y0 = 64;
//		int z0 = bb.minX() - origin.getX();
//		BlockState state = Blocks.REDSTONE_BLOCK.defaultBlockState();
		BlockState stone = Blocks.STONE.defaultBlockState();
		
		// TODO: implement
//		for (int x = 1; x < 15; x++) {
//			for (int z = 1; z < 15; z++) {
//				map.put(new BlockPos(x0 + x, y0 , z0 + z), state);
//			}
//		}
//		map.put(new BlockPos(x0 + 0, y0 , z0 + 0), state);
//		map.put(new BlockPos(x0 + 0, y0 , z0 +15), state);
//		map.put(new BlockPos(x0 +15, y0 , z0 + 0), state);
//		map.put(new BlockPos(x0 +15, y0 , z0 +15), state);
		
		
		Set<BuildingEntry> buildings = this.buildings.stream().filter(
				building -> building.getCircumBox().intersects(genAreaBB)).collect(Collectors.toSet());
		System.out.println("valid buildings = " + buildings.size());
		
		buildings.stream().forEach(building -> {
			BoundingBox bb = building.getCircumBox();
			for (int x = bb.minX(); x <= bb.maxX(); x++) {
				for (int y = bb.minY(); y <= bb.maxY(); y++) {
					for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
						BlockPos pos = new BlockPos(x, y, z);
						if (genAreaBB.isInside(pos)) {
							map.put(pos, stone);
						}
					}
				}
			}
		});
		System.out.println("CityWard#compute map=" + map.size());
		
		// TODO: building context
		
		return map;
	}

	
	public BlockPos getOffset() {
		return this.offset;
	}
	
}
