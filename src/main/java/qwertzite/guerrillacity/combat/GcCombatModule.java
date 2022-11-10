package qwertzite.guerrillacity.combat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.combat.item.CombatShovelItem;
import qwertzite.guerrillacity.core.GcCommon;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.init.RecipeRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;

public class GcCombatModule extends GcModuleBase {
	
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_WOOD = ItemRegister.registryKey("wooden_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_STONE = ItemRegister.registryKey("stone_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_GOLD = ItemRegister.registryKey("gold_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_IRON = ItemRegister.registryKey("iron_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_DIAMOND = ItemRegister.registryKey("diamond_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_NETHERITE = ItemRegister.registryKey("netherite_combat_shovel");
	
	public static final RegistryObject<Item> COMBAT_SHOVEL_WOOD = ItemRegister.$(KEY_COMBAT_SHOVEL_WOOD,
			() -> new CombatShovelItem(Tiers.WOOD, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_STONE = ItemRegister.$(KEY_COMBAT_SHOVEL_STONE,
			() -> new CombatShovelItem(Tiers.STONE, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_GOLD = ItemRegister.$(KEY_COMBAT_SHOVEL_GOLD,
			() -> new CombatShovelItem(Tiers.GOLD, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_IRON = ItemRegister.$(KEY_COMBAT_SHOVEL_IRON,
			() -> new CombatShovelItem(Tiers.IRON, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_DIAMOND = ItemRegister.$(KEY_COMBAT_SHOVEL_DIAMOND,
			() -> new CombatShovelItem(Tiers.DIAMOND, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB))).register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_NETHERITE = ItemRegister.$(KEY_COMBAT_SHOVEL_NETHERITE,
			() -> new CombatShovelItem(Tiers.NETHERITE, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB))).register();
	
	static {
		combatShovelRecipe("wooden", COMBAT_SHOVEL_WOOD, ItemTags.PLANKS);
		combatShovelRecipe("stone", COMBAT_SHOVEL_STONE, ItemTags.STONE_TOOL_MATERIALS);
		combatShovelRecipe("gold", COMBAT_SHOVEL_GOLD, Items.GOLD_INGOT);
		combatShovelRecipe("iron", COMBAT_SHOVEL_IRON, Items.IRON_INGOT);
		combatShovelRecipe("diamond", COMBAT_SHOVEL_DIAMOND, Items.DIAMOND);
		RecipeRegister.upgrade(COMBAT_SHOVEL_NETHERITE)
		.setRecipeName("combat_shovel_netherite")
		.setGroup("combat_shovel")
		.setBaseItem(COMBAT_SHOVEL_DIAMOND)
		.setMaterial(Items.NETHERITE_INGOT);
	}
	
	private static void combatShovelRecipe(String name, RegistryObject<Item> shovel, Item item) {
		RecipeRegister.shaped(shovel)
		.setRecipeName("combat_shovel_" + name)
		.setGroup("combat_shovel")
		.setPattern(
				" E ",
				"EsE",
				" s ")
		.putItemDefinition('E', item)
		.putItemDefinition('s', Items.STICK);
	}
	
	private static void combatShovelRecipe(String name, RegistryObject<Item> shovel, TagKey<Item> item) {
		RecipeRegister.shaped(shovel)
		.setRecipeName("combat_shovel_" + name)
		.setGroup("combat_shovel")
		.setPattern(
				" E ",
				"EsE",
				" s ")
		.putItemDefinition('E', item)
		.putItemDefinition('s', Items.STICK);
	}
	
}
