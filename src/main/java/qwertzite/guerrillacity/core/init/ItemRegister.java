package qwertzite.guerrillacity.core.init;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class ItemRegister {
	
	private static final Set<ItemRegister> ITEM_ENTRY = new HashSet<>();
	private static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(Registry.ITEM_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { ITEM_REGISTRY.register(bus); }
	
	public static ItemRegister $(ResourceKey<Item> regKey, Supplier<Item> item) {
		return new ItemRegister(regKey, item);
	}
	
	public static ResourceKey<Item> registryKey(String name) {
		return ResourceKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
	
	public static GcItemModelProvider getModelProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
		return new GcItemModelProvider(ITEM_ENTRY, generator, fileHelper);
	}
	
	private ResourceKey<Item> registryKey;
	private Supplier<Item> item;
	private RegistryObject<Item> registryObject;
	
	private ItemRegister(ResourceKey<Item> regKey, Supplier<Item> item) {
		this.registryKey = regKey;
		this.item = item;
	}
	
	public RegistryObject<Item> register() {
		ITEM_ENTRY.add(this);
		return this.registryObject = ITEM_REGISTRY.register(this.registryKey.location().getPath(), this.item);
	}
	
	public ResourceKey getRegistrykey() { return this.registryKey; }
}
