package qwertzite.guerrillacity.construction;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.core.GcCommon;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.datagen.ModelCubeAll;
import qwertzite.guerrillacity.core.datagen.ModelCubeBottomTop;
import qwertzite.guerrillacity.core.init.BlockRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.init.RecipeRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;

public class GcConstructionModule extends GcModuleBase {
	
	public static final ResourceKey<Item> KEY_WHITE_EMPTY_GABION = ItemRegister.registryKey("gabion_empty_white");
	public static final ResourceKey<Item> KEY_GREEN_EMPTY_GABION = ItemRegister.registryKey("gabion_empty_green");
	public static final ResourceKey<Item> KEY_GRAY_EMPTY_GABION = ItemRegister.registryKey("gabion_empty_gray");
	
	public static final ResourceKey<Block> KEY_WHITE_GABION = BlockRegister.registryKey("gabion_white");
	public static final ResourceKey<Block> KEY_GREEN_GABION = BlockRegister.registryKey("gabion_green");
	public static final ResourceKey<Block> KEY_GRAY_GABION = BlockRegister.registryKey("gabion_gray");
	public static final ResourceKey<Block> KEY_SAND_GABION = BlockRegister.registryKey("gabion_sand");
	public static final ResourceKey<Block> KEY_TARMAC = BlockRegister.registryKey("tarmac");
	public static final ResourceKey<Block> KEY_WATER_BOUND_GRAVEL = BlockRegister.registryKey("water_bound_gravel");
	
	public static final RegistryObject<Item> WHITE_EMPTY_GABION = 
			ItemRegister.$(KEY_WHITE_EMPTY_GABION, () -> new Item(new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("White Gabion").setLocalisedName(GcLangLocale.JP_JP, "白色の土嚢").register();
	public static final RegistryObject<Item> GREEN_EMPTY_GABION = 
			ItemRegister.$(KEY_GREEN_EMPTY_GABION, () -> new Item(new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Green Gabion").setLocalisedName(GcLangLocale.JP_JP, "緑色の土嚢").register();
	public static final RegistryObject<Item> GRAY_EMPTY_GABION = 
			ItemRegister.$(KEY_GRAY_EMPTY_GABION, () -> new Item(new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Gray Gabion").setLocalisedName(GcLangLocale.JP_JP, "灰色の土嚢").register();
	
	public static final RegistryObject<Block> WHITE_GABION = BlockRegister.$(KEY_WHITE_GABION,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("gabion_white_top", "gabion_white", "gabion_white"))
			.setTab(GcCommon.GC_CREATIVE_TAB)
			.setLocalisedNameEn("White Gabion").setLocalisedName(GcLangLocale.JP_JP, "白色の土嚢")
			.register();
	
	public static final RegistryObject<Block> GREEN_GABION = BlockRegister.$(KEY_GREEN_GABION,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("gabion_green_top", "gabion_green", "gabion_green"))
			.setTab(GcCommon.GC_CREATIVE_TAB)
			.setLocalisedNameEn("Green Gabion").setLocalisedName(GcLangLocale.JP_JP, "緑色の土嚢")
			.register();
	
	public static final RegistryObject<Block> GRAY_GABION = BlockRegister.$(KEY_GRAY_GABION,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeBottomTop("gabion_gray_top", "gabion_gray", "gabion_gray"))
			.setTab(GcCommon.GC_CREATIVE_TAB)
			.setLocalisedNameEn("Gray Gabion").setLocalisedName(GcLangLocale.JP_JP, "灰色の土嚢")
			.register();
	
	public static final RegistryObject<Block> SAND_GABION = BlockRegister.$(KEY_SAND_GABION,
			() -> new Block(BlockBehaviour.Properties.of(Material.DIRT).strength(0.25f, 15.0f).sound(SoundType.SAND)))
			.setModel(new ModelCubeBottomTop("gabion_sand_top", "gabion_sand", "gabion_sand"))
			.setTab(GcCommon.GC_CREATIVE_TAB)
			.setLocalisedNameEn("Gabion filled with Sand").setLocalisedName(GcLangLocale.JP_JP, "砂入りの土嚢")
			.register();
	
	public static final RegistryObject<Block> TARMAC = BlockRegister.$(KEY_TARMAC,
			() -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(4.0f, 10.0f).sound(SoundType.STONE)))
			.setModel(new ModelCubeBottomTop("block_tarmac", "block_tarmac_side", "minecraft:stone"))
			.setTab(GcCommon.GC_CREATIVE_TAB)
			.addToTag(BlockTags.MINEABLE_WITH_PICKAXE)
			.setLocalisedNameEn("Tarmac").setLocalisedName(GcLangLocale.JP_JP, "アスファルト")
			.register();
	
	private static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	public static final RegistryObject<Block> WATER_BOUND_GRAVEL = BlockRegister.$(KEY_WATER_BOUND_GRAVEL,
			() -> new Block(BlockBehaviour.Properties.of(Material.SAND).strength(1.5f, 4.0f).sound(SoundType.GRAVEL)))
			.setModel(new ModelCubeAll("water_bound_gravel"))
			.setTab(GcCommon.GC_CREATIVE_TAB)
			.addToTag(BlockTags.MINEABLE_WITH_SHOVEL)
			.setCustomDrop((self) -> LootTable.lootTable()
					.withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).add(LootItem.lootTableItem(self)))
					.withPool(LootPool.lootPool().when(HAS_SILK_TOUCH.invert()).when(ExplosionCondition.survivesExplosion()).add(LootItem.lootTableItem(Blocks.GRAVEL)))
					.withPool(LootPool.lootPool().when(HAS_SILK_TOUCH.invert()).when(LootItemRandomChanceCondition.randomChance(0.25F)).add(LootItem.lootTableItem(Items.CLAY_BALL))))
			.setLocalisedNameEn("Water-bound Gravel").setLocalisedName(GcLangLocale.JP_JP, "水締めマカダム")
			.register();
	
	static {
		emptyGabionRecipe("white", WHITE_EMPTY_GABION, Items.WHITE_WOOL, Items.WHITE_DYE);
		emptyGabionRecipe("green", GREEN_EMPTY_GABION, Items.GREEN_WOOL, Items.GREEN_DYE);
		emptyGabionRecipe("gray", GRAY_EMPTY_GABION, Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
		gabionFillRecipe("white", WHITE_EMPTY_GABION, WHITE_GABION, Items.DIRT);
		gabionFillRecipe("green", GREEN_EMPTY_GABION, GREEN_GABION, Items.DIRT);
		gabionFillRecipe("gray", GRAY_EMPTY_GABION, GRAY_GABION, Items.DIRT);
		gabionFillRecipe("sand", WHITE_EMPTY_GABION, SAND_GABION, Items.SAND);
		RecipeRegister.shapeless(TARMAC, 8)
		.addIngredient(Items.GRAVEL, 8)
		.addIngredient(Items.LAVA_BUCKET);
		RecipeRegister.shapeless(WATER_BOUND_GRAVEL, 4)
		.addIngredient(Items.GRAVEL, 4)
		.addIngredient(Items.CLAY_BALL)
		.addIngredient(Items.WATER_BUCKET);
	}

	private static void emptyGabionRecipe(String name, RegistryObject<Item> gabion, Item item, Item dye) {
		RecipeRegister.shaped(gabion, 16)
		.setRecipeName("gabion_empty_" + name + "_from_coloured_wool")
		.setGroup("empty_gabion")
		.setPattern(
				" W ",
				"W W",
				" W ")
		.putItemDefinition('W', item);
		
		RecipeRegister.shaped(gabion, 16)
		.setRecipeName("gabion_empty_" + name + "_with_dye")
		.setGroup("empty_gabion")
		.setPattern(
				" W ",
				"WdW",
				" W ")
		.putItemDefinition('W', ItemTags.WOOL)
		.putItemDefinition('d', dye);
	}

	private static void gabionFillRecipe(String name, RegistryObject<Item> empty, RegistryObject<Block> filled, Item filler) {
		RecipeRegister.shapeless(filled, 4)
		.setGroup("gabion")
		.setRecipeName("gabion_" + name + "_from_empty_bag")
		.addIngredient(empty)
		.addIngredient(filler);
	}
	
	public void init(IEventBus bus) {}
}
