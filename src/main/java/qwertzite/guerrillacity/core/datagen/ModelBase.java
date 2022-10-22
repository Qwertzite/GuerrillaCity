package qwertzite.guerrillacity.core.datagen;

import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;

public abstract class ModelBase {

	public abstract ModelFile applyModel(ModelProvider<?> modelProvider, String modelName);

}
