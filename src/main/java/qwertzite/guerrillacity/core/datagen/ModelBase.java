package qwertzite.guerrillacity.core.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import qwertzite.guerrillacity.GuerrillaCityCore;

public abstract class ModelBase {

	public abstract ModelFile applyModel(ModelProvider<?> modelProvider, String modelName);
	
	protected ResourceLocation textureLocation(String type, String name) {
		if (name.contains(":")) {
			String[] split = name.split(":", 2);
			return new ResourceLocation(split[0], type + "/" + split[1]);
		} else {
			return new ResourceLocation(GuerrillaCityCore.MODID, type + "/" + name);
		}
	}
}
