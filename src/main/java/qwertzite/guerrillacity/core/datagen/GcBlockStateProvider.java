package qwertzite.guerrillacity.core.datagen;

import java.util.Set;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.BlockRegister;

public class GcBlockStateProvider extends BlockStateProvider {

	private Set<BlockRegister> entries;
	
	public GcBlockStateProvider(Set<BlockRegister> entries, DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, GuerrillaCityCore.MODID, exFileHelper);
		this.entries = entries;
	}

	@Override
	protected void registerStatesAndModels() {
		for (BlockRegister register : entries) {
			Block block = register.getRegistryObject().get();
			String name = register.getRegistrykey().location().getPath();
			ModelFile model;
			ModelBase base = register.getDefaultModel();
			if (base != null) model = base.applyModel(this.models(), name); 
			else model = models().cubeAll(register.getRegistrykey().location().getPath(), new ResourceLocation(GuerrillaCityCore.MODID, "block/" + name));
			simpleBlock(block, model);
//			simpleBlockItem(block, model);
		}
	}
	
	@Override
	public String getName() {
		return "GC Block States";
	}
}
