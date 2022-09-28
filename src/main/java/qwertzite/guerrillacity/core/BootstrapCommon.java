package qwertzite.guerrillacity.core;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import qwertzite.guerrillacity.core.module.GcModuleBase;

public class BootstrapCommon {
	
	public void initBiomes(RegisterHelper<Biome> helper, GcModuleBase module) {
		module.registerBiomes(helper);
	}
	
}
