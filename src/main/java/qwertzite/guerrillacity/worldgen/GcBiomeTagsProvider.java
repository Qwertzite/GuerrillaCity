package qwertzite.guerrillacity.worldgen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class GcBiomeTagsProvider extends ForgeRegistryTagsProvider<Biome> {

	public GcBiomeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, ForgeRegistries.BIOMES, GuerrillaCityCore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		
		tag(GcWorldGenModule.TAG_IS_CITY).add(GcWorldGenModule.KEY_CITY_BIOME);
	}
}
