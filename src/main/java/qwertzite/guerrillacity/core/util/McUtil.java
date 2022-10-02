package qwertzite.guerrillacity.core.util;

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
}
