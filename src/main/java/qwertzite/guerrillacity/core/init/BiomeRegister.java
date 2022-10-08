package qwertzite.guerrillacity.core.init;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class BiomeRegister {
	
	private static final DeferredRegister<Biome> BIOME_REGISTER = DeferredRegister.create(Registry.BIOME_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { BIOME_REGISTER.register(bus); }
	
	public static RegistryObject<Biome> register(ResourceKey<Biome> registryKey, Supplier<Biome> biome) {
		return BIOME_REGISTER.register(registryKey.location().getPath(), biome);
	}
	
	public static ResourceKey<Biome> registryKey(String name) {
		return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
}
