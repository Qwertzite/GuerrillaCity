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
import qwertzite.guerrillacity.core.datagen.GcItemModelProvider;
import qwertzite.guerrillacity.core.datagen.ModelBase;

public class ItemRegister {
	
	private static final Set<ItemRegister> ENTRY = new HashSet<>();
	private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registry.ITEM_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { REGISTRY.register(bus); }
	
	public static ResourceKey<Item> registryKey(String name) {
		return ResourceKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
	
	public static GcItemModelProvider getModelProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
		return new GcItemModelProvider(ENTRY, generator, fileHelper);
	}
	
	public static ItemRegister $(ResourceKey<Item> regKey, Supplier<Item> item) {
		return new ItemRegister(regKey, item);
	}
	
	private ResourceKey<Item> registryKey;
	private Supplier<Item> item;
	
	private ModelBase model;
	
	private ItemRegister(ResourceKey<Item> regKey, Supplier<Item> item) {
		this.registryKey = regKey;
		this.item = item;
	}
	
	public ItemRegister setModel(ModelBase model) {
		this.model = model;
		return this;
	}
	
	public RegistryObject<Item> register() {
		ENTRY.add(this);
		return REGISTRY.register(this.registryKey.location().getPath(), this.item);
	}
	
	public ResourceKey<Item> getRegistrykey() { return this.registryKey; }
	public ModelBase getCustomModel() { return this.model; }
}
