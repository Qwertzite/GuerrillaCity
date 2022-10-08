package qwertzite.guerrillacity.core.util;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Utility class for handling BlockPos, ChunkPos, BoundingBox, etc...
 * @author Qwertzite
 * @date 2022/10/05
 */
public class PosUtil {
	
	public static BoundingBox getChunkBoundingBox(ChunkPos chunk) {
		return new BoundingBox(chunk.getBlockX(0), 0, chunk.getBlockZ(0), chunk.getBlockX(15), 256, chunk.getBlockZ(15));
	}
}
