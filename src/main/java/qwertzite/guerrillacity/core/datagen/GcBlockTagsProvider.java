package qwertzite.guerrillacity.core.datagen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.BlockRegister;

public class GcBlockTagsProvider extends BlockTagsProvider {

	public GcBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(pGenerator, GuerrillaCityCore.MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags() {
		for (BlockRegister register : BlockRegister.getEntries()) {
			Block block = register.getRegistryObject().get();
			for (TagKey<Block> key : register.getTagsToAdd()) {
				tag(key).add(block);
			}
		}
	}
	
	@Override
	public String getName() {
		return "GC Block Tags";
	}
}
