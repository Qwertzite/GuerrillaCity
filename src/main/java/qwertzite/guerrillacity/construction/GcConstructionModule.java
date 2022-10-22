package qwertzite.guerrillacity.construction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.core.datagen.ModelCubeBottomTop;
import qwertzite.guerrillacity.core.init.BlockRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;

public class GcConstructionModule extends GcModuleBase {
	
	public static final ResourceKey<Item> KEY_WHITE_EMPTY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_white");
	public static final ResourceKey<Item> KEY_GREEN_EMPTY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_green");
	public static final ResourceKey<Item> KEY_GRAY_EMPTY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_gray");
	
	public static final ResourceKey<Block> KEY_WHITE_SANDBAG = BlockRegister.registryKey("sandbag_white");
	public static final ResourceKey<Block> KEY_GREEN_SANDBAG = BlockRegister.registryKey("sandbag_green");
	public static final ResourceKey<Block> KEY_GRAY_SANDBAG = BlockRegister.registryKey("sandbag_gray");
	public static final ResourceKey<Block> KEY_SAND_SANDBAG = BlockRegister.registryKey("sandbag_sand");
	
	public static final RegistryObject<Item> WHITE_EMPTY_SAND_BAG = 
			ItemRegister.$(KEY_WHITE_EMPTY_SAND_BAG, () -> new Item(new Item.Properties())).register();
	public static final RegistryObject<Item> GREEN_EMPTY_SAND_BAG = 
			ItemRegister.$(KEY_GREEN_EMPTY_SAND_BAG, () -> new Item(new Item.Properties())).register();
	public static final RegistryObject<Item> GRAY_EMPTY_SAND_BAG = 
			ItemRegister.$(KEY_GRAY_EMPTY_SAND_BAG, () -> new Item(new Item.Properties())).register();
	
	public static final RegistryObject<Block> WHITE_SANDBAG = BlockRegister.$(KEY_WHITE_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("sandbag_white_top", "sandbag_white", "sandbag_white"))
			.register();
	
	public static final RegistryObject<Block> GREEN_SANDBAG = BlockRegister.$(KEY_GREEN_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("sandbag_green_top", "sandbag_green", "sandbag_green"))
			.register();
	
	public static final RegistryObject<Block> GRAY_SANDBAG = BlockRegister.$(KEY_GRAY_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("sandbag_gray_top", "sandbag_gray", "sandbag_gray"))
			.register();
	
	public static final RegistryObject<Block> SAND_SANDBAG = BlockRegister.$(KEY_SAND_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.SAND)))
			.setModel(new ModelCubeBottomTop("sandbag_sand_top", "sandbag_sand", "sandbag_sand"))
			.register();
}
