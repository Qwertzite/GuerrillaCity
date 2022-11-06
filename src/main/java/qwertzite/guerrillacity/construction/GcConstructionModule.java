package qwertzite.guerrillacity.construction;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.datagen.ModelCubeAll;
import qwertzite.guerrillacity.core.datagen.ModelCubeBottomTop;
import qwertzite.guerrillacity.core.init.BlockRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;

public class GcConstructionModule extends GcModuleBase {
	
	public static final CreativeModeTab GC_CREATIVE_TAB = new CreativeModeTab(GuerrillaCityCore.MODID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(GREEN_SANDBAG.get());
		}
	};
	
	public static final ResourceKey<Item> KEY_WHITE_EMPTY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_white");
	public static final ResourceKey<Item> KEY_GREEN_EMPTY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_green");
	public static final ResourceKey<Item> KEY_GRAY_EMPTY_SAND_BAG = ItemRegister.registryKey("sandbag_empty_gray");
	
	public static final ResourceKey<Block> KEY_WHITE_SANDBAG = BlockRegister.registryKey("sandbag_white");
	public static final ResourceKey<Block> KEY_GREEN_SANDBAG = BlockRegister.registryKey("sandbag_green");
	public static final ResourceKey<Block> KEY_GRAY_SANDBAG = BlockRegister.registryKey("sandbag_gray");
	public static final ResourceKey<Block> KEY_SAND_SANDBAG = BlockRegister.registryKey("sandbag_sand");
	public static final ResourceKey<Block> KEY_TARMAC = BlockRegister.registryKey("tarmac");
	public static final ResourceKey<Block> KEY_WATER_BOUND_GRAVEL = BlockRegister.registryKey("water_bound_gravel");
	
	public static final RegistryObject<Item> WHITE_EMPTY_SAND_BAG = 
			ItemRegister.$(KEY_WHITE_EMPTY_SAND_BAG, () -> new Item(new Item.Properties().tab(GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> GREEN_EMPTY_SAND_BAG = 
			ItemRegister.$(KEY_GREEN_EMPTY_SAND_BAG, () -> new Item(new Item.Properties().tab(GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> GRAY_EMPTY_SAND_BAG = 
			ItemRegister.$(KEY_GRAY_EMPTY_SAND_BAG, () -> new Item(new Item.Properties().tab(GC_CREATIVE_TAB))).register();
	
	public static final RegistryObject<Block> WHITE_SANDBAG = BlockRegister.$(KEY_WHITE_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("sandbag_white_top", "sandbag_white", "sandbag_white"))
			.setTab(GC_CREATIVE_TAB)
			.register();
	
	public static final RegistryObject<Block> GREEN_SANDBAG = BlockRegister.$(KEY_GREEN_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("sandbag_green_top", "sandbag_green", "sandbag_green"))
			.setTab(GC_CREATIVE_TAB)
			.register();
	
	public static final RegistryObject<Block> GRAY_SANDBAG = BlockRegister.$(KEY_GRAY_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("sandbag_gray_top", "sandbag_gray", "sandbag_gray"))
			.setTab(GC_CREATIVE_TAB)
			.register();
	
	public static final RegistryObject<Block> SAND_SANDBAG = BlockRegister.$(KEY_SAND_SANDBAG,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.SAND)))
			.setModel(new ModelCubeBottomTop("sandbag_sand_top", "sandbag_sand", "sandbag_sand"))
			.setTab(GC_CREATIVE_TAB)
			.register();
	
	public static final RegistryObject<Block> TARMAC = BlockRegister.$(KEY_TARMAC,
			() -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(4.0f, 10.0f).sound(SoundType.STONE)))
			.setModel(new ModelCubeBottomTop("block_tarmac", "block_tarmac_side", "minecraft:stone"))
			.setTab(GC_CREATIVE_TAB)
			.register();
	
	private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	public static final RegistryObject<Block> WATER_BOUND_GRAVEL = BlockRegister.$(KEY_WATER_BOUND_GRAVEL,
			() -> new Block(BlockBehaviour.Properties.of(Material.SAND).strength(1.5f, 4.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeAll("water_bound_gravel"))
			.setTab(GC_CREATIVE_TAB)
			.setCustomDrop((self) -> LootTable.lootTable()
					.withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).add(LootItem.lootTableItem(self)))
					.withPool(LootPool.lootPool().when(HAS_SILK_TOUCH.invert()).when(ExplosionCondition.survivesExplosion()).add(LootItem.lootTableItem(Blocks.GRAVEL)))
					.withPool(LootPool.lootPool().when(HAS_SILK_TOUCH.invert()).when(LootItemRandomChanceCondition.randomChance(0.25F)).add(LootItem.lootTableItem(Items.CLAY_BALL))))
			.register();
}
