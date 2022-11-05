package qwertzite.guerrillacity.core.util;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.util.math.Rectangle;

/**
 * Utility class for handling BlockPos, ChunkPos, BoundingBox, etc...
 * @author Qwertzite
 * @date 2022/10/05
 */
public class PosUtil {
	
	public static BoundingBox getChunkBoundingBox(ChunkPos chunk) {
		return new BoundingBox(chunk.getBlockX(0), 0, chunk.getBlockZ(0), chunk.getBlockX(15), 256, chunk.getBlockZ(15));
	}
	
	public static Rectangle getChunkBoundingRectangle(ChunkPos chunk) {
		return new Rectangle(chunk.getBlockX(0), chunk.getBlockZ(0), SectionPos.SECTION_SIZE, SectionPos.SECTION_SIZE);
	}
}