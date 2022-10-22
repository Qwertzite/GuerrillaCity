package qwertzite.guerrillacity.core.datagen;

import java.util.Set;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.ItemRegister;

public class GcItemModelProvider extends ItemModelProvider {
	private Set<ItemRegister> registers;
	
	public GcItemModelProvider(Set<ItemRegister> registers, DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, GuerrillaCityCore.MODID, existingFileHelper);
		this.registers = registers;
	}

	@Override
	protected void registerModels() {
		ModelFile generatedModel = new UncheckedModelFile("item/handheld");
		for (ItemRegister register : registers) {
			String itemName = register.getRegistrykey().location().getPath();
			ModelBase base = register.getCustomModel();
			if (base != null) base.applyModel(this, itemName);
			else getBuilder(itemName).parent(generatedModel).texture("layer0", "item/" + itemName);
		}
	}

	@Override
	public String getName() {
		return "ToLaserBlade Item Models";
	}
}
