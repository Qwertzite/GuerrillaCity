package qwertzite.guerrillacity.core.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class BiomeRegister {
	
	private String biomeName;
	private Biome biome;
	
	private ResourceLocation key;
	
	public BiomeRegister(String name, Biome biome) {
		this.biomeName = name;
		this.biome = biome;
	}
	
	public Biome confirmed() {
		this.key = new ResourceLocation(GuerrillaCityCore.MODID, this.biomeName);
		return this.biome;
	}
	
	public void register(RegisterHelper<Biome> helper) {
		helper.register(this.key, this.biome);
	}
}
