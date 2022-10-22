package qwertzite.guerrillacity.core.init;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.datagen.GcBlockStateProvider;
import qwertzite.guerrillacity.core.datagen.ModelBase;
import qwertzite.guerrillacity.core.datagen.ModelBlockItem;

public class BlockRegister {

	private static final Set<BlockRegister> ENTRY = new HashSet<>();
	private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registry.BLOCK_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { REGISTRY.register(bus); }
	
	public static ResourceKey<Block> registryKey(String name) {
		return ResourceKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
	
	public static BlockStateProvider getBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper) {
		return new GcBlockStateProvider(ENTRY, gen, fileHelper);
	}
	
	public static BlockRegister $(ResourceKey<Block> regKey, Supplier<Block> block) {
		return new BlockRegister(regKey, block);
	}
	
	private ResourceKey<Block> registryKey;
	private Supplier<Block> block;
	
	private ModelBase defaultModel;
	
	private RegistryObject<Block> regObj;
	
	private BlockRegister(ResourceKey<Block> regKey, Supplier<Block> block) {
		this.registryKey = regKey;
		this.block = block;
	}
	
	public BlockRegister setModel(ModelBase model) {
		this.defaultModel = model;
		return this;
	}
	
	public RegistryObject<Block> register() {
		ENTRY.add(this);
		this.regObj = REGISTRY.register(this.registryKey.location().getPath(), this.block);
		ItemRegister.$(ItemRegister.registryKey(this.registryKey.location().getPath()), () -> new BlockItem(regObj.get(), new Item.Properties()))
		.setModel(new ModelBlockItem(this.registryKey))
		.register();
		return this.regObj;
	}
	
	public ResourceKey<Block> getRegistrykey() { return this.registryKey; }
	public ModelBase getDefaultModel() { return this.defaultModel; }
	public RegistryObject<Block> getRegistryObject() { return this.regObj; }
}
