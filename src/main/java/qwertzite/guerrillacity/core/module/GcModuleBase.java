package qwertzite.guerrillacity.core.module;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import qwertzite.guerrillacity.core.init.BiomeRegister;

public abstract class GcModuleBase {
	
	private final List<BiomeRegister> biomes;
	
	public GcModuleBase() {
		this.biomes = new LinkedList<>();
	}
	
	public Biome biome(BiomeRegister biome) {
		this.biomes.add(biome);
		return biome.confirmed();
	}
	
	public void registerBiomes(RegisterHelper<Biome> helper) {
		for (BiomeRegister reg : this.biomes) {
			reg.register(helper);
		}
	}
}
