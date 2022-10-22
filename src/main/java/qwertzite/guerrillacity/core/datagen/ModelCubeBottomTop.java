package qwertzite.guerrillacity.core.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class ModelCubeBottomTop extends ModelBase {
	
	private ResourceLocation top;
	private ResourceLocation side;
	private ResourceLocation bottom;
	
	public ModelCubeBottomTop(String top, String side, String bottom) {
		this.top = new ResourceLocation(GuerrillaCityCore.MODID, "block/" + top);
		this.side = new ResourceLocation(GuerrillaCityCore.MODID, "block/" +side);
		this.bottom = new ResourceLocation(GuerrillaCityCore.MODID, "block/" +bottom);
	}
	
	@Override
	public ModelFile applyModel(ModelProvider<?> modelProvider, String modelName) {
		return modelProvider.cubeBottomTop(modelName, side, bottom, top);
	}

}
