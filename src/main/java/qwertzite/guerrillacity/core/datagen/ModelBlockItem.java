package qwertzite.guerrillacity.core.datagen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class ModelBlockItem extends ModelBase {

	private ResourceKey<Block> block;
	
	public ModelBlockItem(ResourceKey<Block> block) {
		this.block = block;
	}
	
	@Override
	public ModelFile applyModel(ModelProvider<?> modelProvider, String modelName) {
		return modelProvider.getBuilder(modelName).parent(new UncheckedModelFile(new ResourceLocation(GuerrillaCityCore.MODID, "block/" + block.location().getPath())));
	}
}
