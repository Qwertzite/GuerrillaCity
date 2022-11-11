package qwertzite.guerrillacity.worldgen;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class CityBiomes {
	public static Biome plains(boolean snowy) {
		// based on OverworlsBiomes#plains
		
		BiomeBuilder biomeBuilder = new BiomeBuilder();
		BiomeSpecialEffects.Builder specialEffects = new BiomeSpecialEffects.Builder();
		MobSpawnSettings.Builder mobspawnSettings = new MobSpawnSettings.Builder();
		BiomeGenerationSettings.Builder biomeGeneration = new BiomeGenerationSettings.Builder();
		
		Precipitation downfallType = snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN; // rainfall or snowfall 
		float tempereature = snowy ? 0.0F : 0.8F;
		float downFall = snowy ? 0.5F : 0.4F;
		biomeBuilder.precipitation(downfallType).temperature(tempereature).downfall(downFall);
		
		specialEffects.waterColor(0x3F76E4).waterFogColor(0x050533).fogColor(0xC0D8FF).skyColor(calculateSkyColor(tempereature))
				.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(null);  // null is for default music.
		
//		globalOverworldGeneration(biomeGeneration);
//		BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeGeneration);
		BiomeDefaultFeatures.addDefaultCrystalFormations(biomeGeneration);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biomeGeneration);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeGeneration);
//		BiomeDefaultFeatures.addDefaultSprings(biomeGeneration);
		BiomeDefaultFeatures.addSurfaceFreezing(biomeGeneration);
		
		// vegetations are removed
		if (snowy) { CityBiomes.snowySpawns(mobspawnSettings); }
		else { CityBiomes.plainsSpawns(mobspawnSettings); }
		
		BiomeDefaultFeatures.addDefaultOres(biomeGeneration);
		BiomeDefaultFeatures.addDefaultSoftDisks(biomeGeneration);
//		if (snowy) { BiomeDefaultFeatures.addDefaultGrass(biomeGeneration);}
//		else { BiomeDefaultFeatures.addPlainVegetation(biomeGeneration); }
		BiomeDefaultFeatures.addDefaultMushrooms(biomeGeneration); // might cause mushrooms to grow in buildings and basements.
		
		return biomeBuilder
				.specialEffects(specialEffects.build())
				.mobSpawnSettings(mobspawnSettings.build())
				.generationSettings(biomeGeneration.build())
				.build();
	}
	
	private static int calculateSkyColor(float temperature) {
		float $$1 = Mth.clamp(temperature / 3.0F, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
	}
	
	private static void plainsSpawns(MobSpawnSettings.Builder p_126793_) {
		BiomeDefaultFeatures.farmAnimals(p_126793_);
//		p_126793_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 5, 2, 6));
//		p_126793_.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 1, 3));
		BiomeDefaultFeatures.commonSpawns(p_126793_);
	}

	private static void snowySpawns(MobSpawnSettings.Builder builder) {
		builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 2, 3)); // weight, minCount, maxCount
//		builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 1, 2));
		BiomeDefaultFeatures.caveSpawns(builder);
		BiomeDefaultFeatures.monsters(builder, 95, 5, 20, false); // zombie, zombie villager, skeleton, spawn drowned
		builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 80, 4, 4));
	}
}
