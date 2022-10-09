package qwertzite.guerrillacity.core.util;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
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
}
