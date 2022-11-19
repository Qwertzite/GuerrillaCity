package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;

public class BiomeRegister {
	
	private static final Set<BiomeRegister> ENTRY = new HashSet<>();
	private static final DeferredRegister<Biome> REGISTRY = DeferredRegister.create(Registry.BIOME_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { REGISTRY.register(bus); }
	
	public static Set<BiomeRegister> getEntries() {
		return ENTRY;
	}
	
	public static ResourceKey<Biome> registryKey(String name) {
		return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
	
	public static BiomeRegister $(ResourceKey<Biome> regKey, Supplier<Biome> block) {
		return new BiomeRegister(regKey, block);
	}
	
	private ResourceKey<Biome> registryKey;
	private Supplier<Biome> biome;
	
	private Set<TagKey<Biome>> tagsToAddThisBiome = new HashSet<>();
	private Map<GcLangLocale, String> localName = new HashMap<>();
	
	private RegistryObject<Biome> regObj;
	
	private BiomeRegister(ResourceKey<Biome> regKey, Supplier<Biome> block) {
		this.registryKey = regKey;
		this.biome = block;
	}
	
	public BiomeRegister addToTag(TagKey<Biome> tag) {
		this.tagsToAddThisBiome.add(tag);
		return this;
	}
	
	public BiomeRegister setLocalisedNameEn(String name) {
		this.localName.put(GcLangLocale.EN_GB, name);
		this.localName.put(GcLangLocale.EN_US, name);
		return this;
	}
	
	public BiomeRegister setLocalisedName(GcLangLocale locale, String name) {
		this.localName.put(locale, name);
		return this;
	}
	
	public RegistryObject<Biome> register() {
		ENTRY.add(this);
		this.regObj = REGISTRY.register(this.registryKey.location().getPath(), this.biome);
		return this.regObj;
	}
	
	public ResourceKey<Biome> getRegistrykey() { return this.registryKey; }
	public RegistryObject<Biome> getRegistryObject() { return this.regObj; }
	public Set<TagKey<Biome>> getTagsToAdd() { return this.tagsToAddThisBiome; }
	public String getLocalName(GcLangLocale locale) { return this.localName.getOrDefault(locale, null); }
}
