package qwertzite.guerrillacity.core.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

/**
 * 
 * MineCraft-specific utility methods.
 * 
 * @author Qwertzite
 * @date 2022/09/28
 */
public class McUtil {
	
	/**
	 * For those occasions when {@link World#isRemote} cannot be accessed.
	 * 
	 * @return whether this MOD is run on server.
	 */
	public static boolean isServer() {
		return Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER;
	}
	
	/**
	 * Human friendly biome expression.
	 * @param pBiomeHolder
	 * @return
	 */
	public static String printBiome(Holder<Biome> pBiomeHolder) {
		return pBiomeHolder.unwrap().map((p_205377_) -> {
			return p_205377_.location().toString();
		}, (p_205367_) -> {
			return "[unregistered " + p_205367_ + "]";
		});
	}
	
	/**
	 * DO NOT MODIFY RETURNED ARRAY.
	 */
	private static final Direction[] HORIZONTAL_DIR = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
	public static Direction[] horizontalDir() {
		return HORIZONTAL_DIR;
	}
	
	public static BoundingBox boundingBox(int x1, int y1, int z1, int x2, int y2, int z2) {
		if (x1 > x2) {
			var tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if (y1 > y2) {
			var tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		if (z1 > z2) {
			var tmp = z1;
			z1 = z2;
			z2 = tmp;
		}
		return new BoundingBox(x1, y1, z1, x2, y2, z2);
	}
}
