package qwertzite.guerrillacity.construction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;

public class GcConstructionModule extends GcModuleBase {
	
	public static final ResourceKey<Item> KEY_WHITE_SAND_BAG = ItemRegister.registryKey("sandbag_empty_white");
	public static final ResourceKey<Item> KEY_GREEN_SAND_BAG = ItemRegister.registryKey("sandbag_empty_green");
	public static final ResourceKey<Item> KEY_GRAY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_gray");
	
	public static final RegistryObject<Item> WHITE_SAND_BAG = 
			ItemRegister.$(KEY_WHITE_SAND_BAG, () -> new Item(new Item.Properties())).register();
	public static final RegistryObject<Item> GREEN_SAND_BAG = 
			ItemRegister.$(KEY_GREEN_SAND_BAG, () -> new Item(new Item.Properties())).register();
	public static final RegistryObject<Item> GRAY_SAND_BAG = 
			ItemRegister.$(KEY_GRAY_SAND_BAG, () -> new Item(new Item.Properties())).register();
	
	
	
}
