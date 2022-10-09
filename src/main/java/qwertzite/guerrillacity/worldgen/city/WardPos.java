package qwertzite.guerrillacity.worldgen.city;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

/**
 * It is recommended to obtain WardPos instance via {@link WardPos#of(int, int)} to obtain interned object.
 * 
 * @param wx ward index x direction
 * @param wz ward index z direction
 * 
 * @author Qwertzite
 * @date 2022/10/08
 */
public record WardPos(int wx, int wz) {
	private static final Interner<WardPos> INTERNER = Interners.newWeakInterner();
	
	public static WardPos of(int wardX, int wardZ) {
		return INTERNER.intern(new WardPos(wardX, wardZ));
	}
	
	public static WardPos of(ChunkPos chunkPos) {
		int wardX = chunkPos.x >> CityWard.WARD_SIZE_BIT;
		int wardZ = chunkPos.z >> CityWard.WARD_SIZE_BIT;
		return WardPos.of(wardX, wardZ);
	}
	
	public int getChunkX() {
		return this.wx << CityWard.WARD_SIZE_BIT;
	}
	
	public int getChunkZ() {
		return this.wz << CityWard.WARD_SIZE_BIT;
	}
	
	public int getBlockX() {
		return SectionPos.sectionToBlockCoord(this.getChunkX());
	}
	
	public int getBlockZ() {
		return SectionPos.sectionToBlockCoord(this.getChunkZ());
	}
	
	/**
	 *BlockPos with minimum x and z coordinates.
	 * @param y y of returning BlockPos will be set to this value.
	 * @return
	 */
	public BlockPos getBaseBlockPos(int y) {
		return new BlockPos(this.getBlockX(), y, this.getBlockZ());
	}
	
	/**
	 * Returns Chunks within this CityWard.
	 * @return parallel stream
	 */
	public Stream<ChunkPos> getChunksWithin() {
		long streamSize = 1L << (CityWard.WARD_SIZE_BIT + CityWard.WARD_SIZE_BIT);
		int attributes = Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
		
		return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkPos>(streamSize, attributes) {
			private final int wxc = getChunkX();
			private final int wzc = getChunkZ();
			private int cx;
			private int cz;
			private final int max = 1 << SectionPos.SECTION_BITS;
			
			@Override
			public boolean tryAdvance(Consumer<? super ChunkPos> action) {
				if (cz >= max) {
					return false;
				}
				if (cx >= max) {
					this.cz++;
					this.cx = 0;
				}
				ChunkPos chunkPos = new ChunkPos(wxc + cx, wzc + cz);
				this.cx++;
				
				action.accept(chunkPos);
				return true;
			}
		}, true);
	}
}