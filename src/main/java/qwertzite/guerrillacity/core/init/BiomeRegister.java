package qwertzite.guerrillacity.core.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class BiomeRegister {
	
	private static final DeferredRegister<Biome> BIOME_REGISTER = DeferredRegister.create(Registry.BIOME_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus event) { BIOME_REGISTER.register(event); }
	
	public static Biome register(ResourceKey<Biome> registryKey, Biome biome) {
		BIOME_REGISTER.register(registryKey.location().getPath(), () -> biome);
		return biome;
	}
	
	public static ResourceKey<Biome> registryKey(String name) {
		return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
}
