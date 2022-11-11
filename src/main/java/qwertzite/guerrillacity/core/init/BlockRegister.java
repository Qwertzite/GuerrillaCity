package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.datagen.GcLootTblBlockDrop;
import qwertzite.guerrillacity.core.datagen.ModelBase;
import qwertzite.guerrillacity.core.datagen.ModelBlockItem;

public class BlockRegister {

	private static final Set<BlockRegister> ENTRY = new HashSet<>();
	private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registry.BLOCK_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { REGISTRY.register(bus); }
	
	public static Set<BlockRegister> getEntries() {
		return ENTRY;
	}
	
	// ==== util ====
	
	public static ResourceKey<Block> registryKey(String name) {
		return ResourceKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
	
	// ==== provider ====
	
	public static BlockLoot getBlockDropProvider() {
		return new GcLootTblBlockDrop(ENTRY);
	}
	
	// ==== main ====
	
	public static BlockRegister $(ResourceKey<Block> regKey, Supplier<Block> block) {
		return new BlockRegister(regKey, block);
	}
	
	private ResourceKey<Block> registryKey;
	private Supplier<Block> block;
	
	private ModelBase defaultModel;
	private CreativeModeTab tab;
	private Function<Block, LootTable.Builder> customDrop;
	private Set<TagKey<Block>> tagsToAddThisBlock = new HashSet<>();
	private Map<GcLangLocale, String> localName = new HashMap<>();
	
	private RegistryObject<Block> regObj;
	
	private BlockRegister(ResourceKey<Block> regKey, Supplier<Block> block) {
		this.registryKey = regKey;
		this.block = block;
	}
	
	public BlockRegister setModel(ModelBase model) {
		this.defaultModel = model;
		return this;
	}
	
	public BlockRegister setTab(CreativeModeTab tab) {
		this.tab = tab;
		return this;
	}
	
	public BlockRegister setCustomDrop(Function<Block, LootTable.Builder> customDrop) {
		this.customDrop = customDrop;
		return this;
	}
	
	public BlockRegister addToTag(TagKey<Block> tag) {
		this.tagsToAddThisBlock.add(tag);
		return this;
	}
	
	public BlockRegister setLocalisedNameEn(String name) {
		this.localName.put(GcLangLocale.EN_GB, name);
		this.localName.put(GcLangLocale.EN_US, name);
		return this;
	}
	
	public BlockRegister setLocalisedName(GcLangLocale locale, String name) {
		this.localName.put(locale, name);
		return this;
	}
	
	public RegistryObject<Block> register() {
		ENTRY.add(this);
		this.regObj = REGISTRY.register(this.registryKey.location().getPath(), this.block);
		ItemRegister.$(ItemRegister.registryKey(this.registryKey.location().getPath()), () -> new BlockItem(regObj.get(), new Item.Properties().tab(this.tab)))
		.setModel(new ModelBlockItem(this.registryKey))
		.register();
		return this.regObj;
	}
	
	public ResourceKey<Block> getRegistrykey() { return this.registryKey; }
	public ModelBase getDefaultModel() { return this.defaultModel; }
	public RegistryObject<Block> getRegistryObject() { return this.regObj; }
	public boolean hasCustomDrop() { return this.customDrop != null; }
	public LootTable.Builder getCustomDrop() { return this.customDrop.apply(this.regObj.get()); }
	public Set<TagKey<Block>> getTagsToAdd() { return this.tagsToAddThisBlock; }
	public String getLocalName(GcLangLocale locale) { return this.localName.getOrDefault(locale, null); }
}
