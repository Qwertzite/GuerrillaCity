package qwertzite.guerrillacity.worldgen.city;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.util.GcConsts;
import qwertzite.guerrillacity.core.util.PosUtil;

/**
 * Provides each chunk with BlockPos and BlockState by creating according wards.
 * @author Qwertzite
 * @date 2022/10/08
 */
public class CityStructureProvider {
	private static final Map<WardPos, CityWard> CACHE = new ConcurrentHashMap<>();
	private static long seed;
	
	public static Map<BlockPos, BlockState> getBlockStatesForChunk(ChunkPos chunkPos, long seed, LevelAccessor biomeSource, Predicate<Holder<Biome>> validBiome) {
		synchronized (CityStructureProvider.class) {
			if (CityStructureProvider.seed != seed) {
				CityStructureProvider.seed = seed;
				CityStructureProvider.CACHE.clear(); // OPTIMISE: notify workers to abort all process.
			}
		}
		WardPos wardPos = WardPos.of(chunkPos);
		CityWard cityWard;
		synchronized (wardPos) { // This is possible because wardPos is interned.
			if (!CACHE.containsKey(wardPos)) {
				cityWard = new CityWard(wardPos, seed);
				CACHE.put(wardPos, cityWard);
				CityStructureProvider.initialiseCityWard(cityWard, wardPos, biomeSource, validBiome);
			} else {
				cityWard = CACHE.get(wardPos);
			}
		}
		
		var chunkBB = new BoundingBox(
				chunkPos.getMinBlockX(),                                                     0, chunkPos.getMinBlockZ(),
				chunkPos.getMaxBlockX(), GcConsts.GROUND_HEIGHT + GcConsts.MAX_BUILDING_HEIGHT, chunkPos.getMaxBlockZ());
		
		return cityWard.computeBlockStateForBoudingBox(chunkBB);
	}
	
	private static void initialiseCityWard(CityWard cityWard, WardPos wardPos, LevelAccessor biomeSource, Predicate<Holder<Biome>> validBiome) {
		
		Map<Boolean, Set<BoundingBox>> groups = wardPos.getChunksWithin().collect(Collectors.partitioningBy(
				cp -> checkAChunkApplicaleBiome(biomeSource, cp, validBiome),
				Collectors.mapping(chunk -> PosUtil.getChunkBoundingBox(chunk), Collectors.toSet())));
		
		cityWard.beginInitialisation(groups.get(true), groups.get(false));
	}
	
	/**
	 * this method is concurrently executed. So there is no need to make he implementation of this method multi-threading.
	 * @param source
	 * @param chunkPos
	 * @param validBiome
	 * @return
	 */
	private static boolean checkAChunkApplicaleBiome(LevelAccessor source, ChunkPos chunkPos, Predicate<Holder<Biome>> validBiome) {
		int cx = chunkPos.getMinBlockX();
		int cy = GcConsts.GROUND_HEIGHT;
		int cz = chunkPos.getMinBlockZ();
		for (int y = 0; y < GcConsts.MAX_BUILDING_HEIGHT; y++) { // outer section
			for (int w = 0; w < 15; w++) {
				if (!validBiome.test(source.getNoiseBiome(QuartPos.fromBlock(cx     +w), QuartPos.fromBlock(cy + y), QuartPos.fromBlock(cz       )))) return false;
				if (!validBiome.test(source.getNoiseBiome(QuartPos.fromBlock(cx +15   ), QuartPos.fromBlock(cy + y), QuartPos.fromBlock(cz     +w)))) return false;
				if (!validBiome.test(source.getNoiseBiome(QuartPos.fromBlock(cx +15 -w), QuartPos.fromBlock(cy + y), QuartPos.fromBlock(cz +15   )))) return false;
				if (!validBiome.test(source.getNoiseBiome(QuartPos.fromBlock(cx       ), QuartPos.fromBlock(cy + y), QuartPos.fromBlock(cz +15 -w)))) return false;
			}
		}
		for (int x = 4; x < 16; x += 4) {
			for (int y = 0; y < GcConsts.MAX_BUILDING_HEIGHT; y += 4) {
				for (int z = 4; z < 16; z += 4) {
					if (!validBiome.test(source.getNoiseBiome(QuartPos.fromBlock(cx + x), QuartPos.fromBlock(cy + y), QuartPos.fromBlock(cz + z)))) return false;
				}
			}
		}
		return true;
	}
}
