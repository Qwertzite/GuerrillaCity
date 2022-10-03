package qwertzite.guerrillacity.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.BiomeRegister;
import qwertzite.guerrillacity.core.init.RegionRegister;
import qwertzite.guerrillacity.core.init.SurfaceRuleRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import terrablender.api.SurfaceRuleManager.RuleCategory;

/**
 * 
 * 
 * @author Qwertzite
 * @date 2022/09/29
 */
public class GcWorldGenModule extends GcModuleBase {
	
	public static final String NAME_CITY_BIOME = "city";
	public static final String NAME_SNOWY_CITY_BIOME = "snowy_city";
	
	public static final ResourceKey<Biome> KEY_CITY_BIOME = BiomeRegister.registryKey(NAME_CITY_BIOME);
	public static final ResourceKey<Biome> KEY_SNOWY_CITY_BIOME = BiomeRegister.registryKey(NAME_SNOWY_CITY_BIOME);
	
	public static Biome CITY_BIOME;
	public static Biome SNOWY_CITY_BIOME;
	
	public static ResourceLocation REGION_CITY = new ResourceLocation(GuerrillaCityCore.MODID, "region_city");
	
	public GcWorldGenModule() {
		CITY_BIOME = BiomeRegister.register(KEY_CITY_BIOME, CityBiomes.plains(false));
		SNOWY_CITY_BIOME = BiomeRegister.register(KEY_SNOWY_CITY_BIOME, CityBiomes.plains(true));
		
		RegionRegister.register(new CityRegion(REGION_CITY, 5));
		SurfaceRuleRegister.register(RuleCategory.OVERWORLD, CitySurfaceRules.makeRules());
	}
}
