package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.datagen.ModelBase;

public class ItemRegister {
	
	private static final Set<ItemRegister> ENTRY = new HashSet<>();
	private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registry.ITEM_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { REGISTRY.register(bus); }
	
	public static Set<ItemRegister> getEntries() {
		return ENTRY;
	}
	
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
	private Map<GcLangLocale, String> localName = new HashMap<>();
	
	private ModelBase model;
	
	private RegistryObject<Item> registryObject;
	
	private ItemRegister(ResourceKey<Item> regKey, Supplier<Item> item) {
		this.registryKey = regKey;
		this.item = item;
	}
	
	public ItemRegister setModel(ModelBase model) {
		this.model = model;
		return this;
	}
	
	public ItemRegister setLocalisedNameEn(String name) {
		this.localName.put(GcLangLocale.EN_GB, name);
		this.localName.put(GcLangLocale.EN_US, name);
		return this;
	}
	
	public ItemRegister setLocalisedName(GcLangLocale locale, String name) {
		this.localName.put(locale, name);
		return this;
	}
	
	public RegistryObject<Item> register() {
		ENTRY.add(this);
		return registryObject = REGISTRY.register(this.registryKey.location().getPath(), this.item);
	}
	
	public ResourceKey<Item> getRegistrykey() { return this.registryKey; }
	public String getLocalName(GcLangLocale locale) { return this.localName.getOrDefault(locale, null); }
	public ModelBase getCustomModel() { return this.model; }
	public RegistryObject<Item> getRegistryObject() { return this.registryObject; }
}
