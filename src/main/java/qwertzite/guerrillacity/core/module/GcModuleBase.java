package qwertzite.guerrillacity.core.module;

import net.minecraftforge.event.server.ServerStartingEvent;

public abstract class GcModuleBase {
	
	public GcModuleBase() {
	}
	
	public void onServerStarting(ServerStartingEvent evt) {
		
	}
	
//	public void onRegionAndSurfaceRuleRegistration() {
//		for (BiomeRegister reg : this.biomes) {
//			reg.registerBiome(helper);
//		}
//	}
}
