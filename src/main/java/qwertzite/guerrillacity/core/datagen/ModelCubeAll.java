package qwertzite.guerrillacity.core.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;

public class ModelCubeAll extends ModelBase {

	private ResourceLocation texture;
	
	public ModelCubeAll(String texture) {
		this.texture = textureLocation("block", texture);
	}
	
	@Override
	public ModelFile applyModel(ModelProvider<?> modelProvider, String modelName) {
		return modelProvider.cubeAll(modelName, this.texture);
	}
}
