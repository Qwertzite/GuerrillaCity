package qwertzite.guerrillacity.core.datagen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.BiomeRegister;

public class GcBiomeTagsProvider extends ForgeRegistryTagsProvider<Biome> {

	public GcBiomeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, ForgeRegistries.BIOMES, GuerrillaCityCore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		for (var entry : BiomeRegister.getEntries()) {
			for (TagKey<Biome> tag : entry.getTagsToAdd()) {
				tag(tag).add(entry.getRegistryObject().get());
			}
			
		}
	}
	
	@Override
	public String getName() {
		return "GC Biome Tags";
	}
}
