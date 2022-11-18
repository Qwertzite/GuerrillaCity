package qwertzite.guerrillacity.combat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.combat.entity.Mortar120mmShellEntity;
import qwertzite.guerrillacity.combat.event.MortarEventHandler;
import qwertzite.guerrillacity.combat.item.CombatShovelItem;
import qwertzite.guerrillacity.combat.item.Mortar120mmItem;
import qwertzite.guerrillacity.combat.model.Mortar120mmModel;
import qwertzite.guerrillacity.combat.model.Mortar120mmShellModel;
import qwertzite.guerrillacity.combat.network.Mortar120mmCtrlPacket;
import qwertzite.guerrillacity.combat.renderer.Mortar120mmRenderer;
import qwertzite.guerrillacity.combat.renderer.Mortar120mmShellRenderer;
import qwertzite.guerrillacity.core.common.GcCommon;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.init.EntityRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.init.RecipeRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import qwertzite.guerrillacity.core.network.GcNetwork;

public class GcCombatModule extends GcModuleBase {
	
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_WOOD = ItemRegister.registryKey("wooden_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_STONE = ItemRegister.registryKey("stone_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_GOLD = ItemRegister.registryKey("gold_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_IRON = ItemRegister.registryKey("iron_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_DIAMOND = ItemRegister.registryKey("diamond_combat_shovel");
	public static final ResourceKey<Item> KEY_COMBAT_SHOVEL_NETHERITE = ItemRegister.registryKey("netherite_combat_shovel");
	
	public static final ResourceKey<Item> KEY_MORTAR_120MM = ItemRegister.registryKey("mortar_120mm"); // M120
	public static final ResourceKey<Item> KEY_MORTAR_SHELL_120MM_HE = ItemRegister.registryKey("mortar_shell_120mm_he"); // M933
	public static final ResourceKey<EntityType<?>> KEY_MORTAR_120MM_ENTITY = EntityRegister.registryKey("mortar_120mm");
	public static final ResourceKey<EntityType<?>> KEY_MORTAR_120MM_SHELL_ENTITY = EntityRegister.registryKey("mortar_120mm_shell");
	
	public static final RegistryObject<Item> COMBAT_SHOVEL_WOOD = ItemRegister.$(KEY_COMBAT_SHOVEL_WOOD,
			() -> new CombatShovelItem(Tiers.WOOD, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Wooden Combat Shovel").setLocalisedName(GcLangLocale.JP_JP, "木の戦闘円匙").register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_STONE = ItemRegister.$(KEY_COMBAT_SHOVEL_STONE,
			() -> new CombatShovelItem(Tiers.STONE, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Stone Combat Shovel").setLocalisedName(GcLangLocale.JP_JP, "石の戦闘円匙").register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_GOLD = ItemRegister.$(KEY_COMBAT_SHOVEL_GOLD,
			() -> new CombatShovelItem(Tiers.GOLD, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Golden Combat Shovel").setLocalisedName(GcLangLocale.JP_JP, "金の戦闘円匙").register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_IRON = ItemRegister.$(KEY_COMBAT_SHOVEL_IRON,
			() -> new CombatShovelItem(Tiers.IRON, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Iron Combat Shovel").setLocalisedName(GcLangLocale.JP_JP, "鉄の戦闘円匙").register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_DIAMOND = ItemRegister.$(KEY_COMBAT_SHOVEL_DIAMOND,
			() -> new CombatShovelItem(Tiers.DIAMOND, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Diamond Combat Shovel").setLocalisedName(GcLangLocale.JP_JP, "ダイヤモンドの戦闘円匙").register();
	public static final RegistryObject<Item> COMBAT_SHOVEL_NETHERITE = ItemRegister.$(KEY_COMBAT_SHOVEL_NETHERITE,
			() -> new CombatShovelItem(Tiers.NETHERITE, 2.0f, -2.5f, new Item.Properties().tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("Netherite Combat Shovel").setLocalisedName(GcLangLocale.JP_JP, "ネザライトの戦闘円匙").register();
	
	public static final RegistryObject<Item> MORTAR_120MM = ItemRegister.$(KEY_MORTAR_120MM, 
			() -> new Mortar120mmItem(new Item.Properties().stacksTo(1).tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("120mm Mortar").setLocalisedName(GcLangLocale.JP_JP, "120mm 迫撃砲").register();
	public static final RegistryObject<Item> MORTAR_SHELL_120MM_HE = ItemRegister.$(KEY_MORTAR_SHELL_120MM_HE,
			() -> new Item(new Item.Properties().stacksTo(4).tab(GcCommon.GC_CREATIVE_TAB)))
			.setLocalisedNameEn("120mm HE Mortar Shell").setLocalisedName(GcLangLocale.JP_JP, "120mm 迫撃砲 HE弾").register();
	
	public static final RegistryObject<EntityType<Mortar120mmEntity>> MORTAR_120MM_ENTITY = EntityRegister.$(KEY_MORTAR_120MM_ENTITY,
			() -> EntityType.Builder.<Mortar120mmEntity>of(Mortar120mmEntity::new, MobCategory.MISC).sized(1.0f, 1.4f).clientTrackingRange(8).build(KEY_MORTAR_120MM_ENTITY.toString()),
			m -> new Mortar120mmRenderer(m))
			.addModelLayer(Mortar120mmModel.MORTAR_MODEL, Mortar120mmModel::create)
			.build();
	
	public static final RegistryObject<EntityType<Mortar120mmShellEntity>> MORTAR_120MM_SHELL_ENTITY = EntityRegister.$(KEY_MORTAR_120MM_SHELL_ENTITY,
			() -> EntityType.Builder.<Mortar120mmShellEntity>of(Mortar120mmShellEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(KEY_MORTAR_120MM_SHELL_ENTITY.toString()),
			m -> new Mortar120mmShellRenderer(m))
			.addModelLayer(Mortar120mmShellModel.SHELL_MODEL, Mortar120mmShellModel::create)
			.build();
	
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
		
		// TODO: shell recipe, mortar recipe
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
	
	public GcCombatModule() {
		MinecraftForge.EVENT_BUS.register(new MortarEventHandler());
		GcNetwork.registerPacket(Mortar120mmCtrlPacket.class);
	}
	
	public void init(IEventBus bus) {}
}
