package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.core.util.math.IntObjTuple;

public abstract class RecipeRegister<R extends RecipeRegister<R>> {
	
	private static final Set<ShapedRecipeRegister> SHAPED_ENTRY = new HashSet<>();
	private static final Set<ShapelessRecipeRegister> SHAPELESS_ENTRY = new HashSet<>();
	private static final Set<UpgradeRecipeRegister> UPGRADE_ENTRY = new HashSet<>();
	
	public static Set<ShapedRecipeRegister> getShapedRecipeEntries() { return SHAPED_ENTRY; }
	public static Set<ShapelessRecipeRegister> getShapelessRecipeEntries() { return SHAPELESS_ENTRY; }
	public static Set<UpgradeRecipeRegister> getUpgradeRecipeEntries() { return UPGRADE_ENTRY; }
	
	
	public static ShapedRecipeRegister shaped(RegistryObject<? extends ItemLike> output) {
		return shaped(output, 1);
	}
	
	public static ShapedRecipeRegister shaped(RegistryObject<? extends ItemLike> output, int count) {
		return shaped(() -> output.get(), count);
	}
	
	public static ShapedRecipeRegister shaped(ItemLike output, int count) {
		return shaped(() -> output, count);
	}
	
	public static ShapedRecipeRegister shaped(Supplier<? extends ItemLike> output, int count) {
		return new ShapedRecipeRegister(output, count);
	}
	
	public static ShapelessRecipeRegister shapeless(RegistryObject<? extends ItemLike> output) {
		return shapeless(output, 1);
	}
	
	public static ShapelessRecipeRegister shapeless(RegistryObject<? extends ItemLike> output, int count) {
		return shapeless(() -> output.get(), count);
	}
	
	public static ShapelessRecipeRegister shapeless(ItemLike output, int count) {
		return shapeless(() -> output, count);
	}
	
	public static ShapelessRecipeRegister shapeless(Supplier<? extends ItemLike> output, int count) {
		return new ShapelessRecipeRegister(output, count);
	}
	
	public static UpgradeRecipeRegister upgrade(RegistryObject<? extends ItemLike> output) {
		return upgrade(() -> output.get());
	}
	
	public static UpgradeRecipeRegister upgrade(Supplier<? extends ItemLike> output) {
		return new UpgradeRecipeRegister(output);
	}
	
	
	private final Supplier<? extends ItemLike> output;
	private final int count;
	private String recipeName;
	private String group;
	private final Map<String, CriterionTriggerInstance> unlockTrigger = new HashMap<>();
	
	private RecipeRegister(Supplier<? extends ItemLike> output, int count) {
		this.output = output;
		this.count = count;
	}
	
	@SuppressWarnings("unchecked")
	public R setRecipeName(String name) {
		this.recipeName = name;
		return (R) this;
	}
	
	@SuppressWarnings("unchecked")
	public R setGroup(String group) {
		this.group = group;
		return (R) this;
	}
	
	@SuppressWarnings("unchecked")
	public R addCustomUnlockCriteria(String name, CriterionTriggerInstance triggetInstance) {
		this.getUnlockTrigger().put(name, triggetInstance);
		return (R) this;
	}
	
	public String getRecipeName() {
		return this.recipeName;
	}
	
	public boolean hasGroup() {
		return this.group != null;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public Supplier<? extends ItemLike> getOutput() {
		return output;
	}

	public int getCount() {
		return count;
	}
	
	
	public Map<String, CriterionTriggerInstance> getUnlockTrigger() {
		return unlockTrigger;
	}
	
	public static class ShapedRecipeRegister extends RecipeRegister<ShapedRecipeRegister> {
		
		private String[] pattern;
		private final Map<Character, Supplier<ItemLike>> defineItemLike = new HashMap<>();
		private final Map<Character, Supplier<TagKey<Item>>> defineTagkey = new HashMap<>();
		
		private ShapedRecipeRegister(Supplier<? extends ItemLike> output, int count) {
			super(output, count);
			SHAPED_ENTRY.add(this);
		}
		
		public ShapedRecipeRegister setPattern(String...pattern) {
			this.pattern = pattern;
			return this;
		}
		
		public ShapedRecipeRegister putItemDefinition(char character, ItemLike ingredient) {
			return this.putItemDefinitionItemLike(character, () -> ingredient);
		}
		
		public ShapedRecipeRegister putItemDefinitionItemLike(char character, Supplier<ItemLike> ingredient) {
			this.getDefineItemLike().put(character, ingredient);
			return this;
		}
		
		public ShapedRecipeRegister putItemDefinition(char character, TagKey<Item> ingredient) {
			this.getDefineTagkey().put(character, () -> ingredient);
			return this;
		}
		
		public String[] getPattern() {
			return pattern;
		}
	
		public Map<Character, Supplier<ItemLike>> getDefineItemLike() {
			return defineItemLike;
		}
	
		public Map<Character, Supplier<TagKey<Item>>> getDefineTagkey() {
			return defineTagkey;
		}
	}
	
	public static class ShapelessRecipeRegister extends RecipeRegister<ShapelessRecipeRegister> {
		
		private final Set<IntObjTuple<Supplier<ItemLike>>> ingredientItemLike = new HashSet<>();
		private final Set<Supplier<TagKey<Item>>> ingredientTagkey = new HashSet<>();
		
		private ShapelessRecipeRegister(Supplier<? extends ItemLike> output, int count) {
			super(output, count);
			SHAPELESS_ENTRY.add(this);
		}
		
		public ShapelessRecipeRegister addIngredient(RegistryObject<? extends ItemLike> ingredient) {
			return this.addIngredient(ingredient, 1);
		}
		
		public ShapelessRecipeRegister addIngredient(RegistryObject<? extends ItemLike> ingredient, int count) {
			return this.addIngredient(() -> ingredient.get(), count);
		}
		
		public ShapelessRecipeRegister addIngredient(ItemLike ingredient) {
			return this.addIngredient(() -> ingredient, 1);
		}
		
		public ShapelessRecipeRegister addIngredient(Supplier<ItemLike> ingredient, int count) {
			ingredientItemLike.add(new IntObjTuple<>(count, ingredient));
			return this;
		}
		
		public ShapelessRecipeRegister addIngredient(TagKey<Item> ingredient) {
			ingredientTagkey.add(() -> ingredient);
			return this;
		}
		
		public Set<IntObjTuple<Supplier<ItemLike>>> getIngredientItemLike() {
			return ingredientItemLike;
		}
	
		public Set<Supplier<TagKey<Item>>> getIngredientTagKey() {
			return ingredientTagkey;
		}
	}
	
	public static class UpgradeRecipeRegister extends RecipeRegister<UpgradeRecipeRegister> {
		
		private Supplier<Ingredient> baseItem;
		private Supplier<Ingredient> material;
		
		private UpgradeRecipeRegister(Supplier<? extends ItemLike> output) {
			super(output, 0);
			UPGRADE_ENTRY.add(this);
		}
		
		public UpgradeRecipeRegister setBaseItem(RegistryObject<? extends ItemLike> baseItem) {
			return this.setBaseItem(() -> Ingredient.of(baseItem.get()));
		}
		
		public UpgradeRecipeRegister setBaseItem(ItemLike material) {
			return this.setBaseItem(() -> Ingredient.of(material));
		}
		
		public UpgradeRecipeRegister setBaseItem(Supplier<Ingredient> baseItem) {
			this.baseItem = baseItem;
			return this;
		}
		
		public UpgradeRecipeRegister setMaterial(RegistryObject<? extends ItemLike> baseItem) {
			return this.setBaseItem(() -> Ingredient.of(baseItem.get()));
		}
		
		public UpgradeRecipeRegister setMaterial(ItemLike material) {
			return this.setMaterial(() -> Ingredient.of(material));
		}
		
		public UpgradeRecipeRegister setMaterial(Supplier<Ingredient> baseItem) {
			this.material = baseItem;
			return this;
		}
		
		public Ingredient getBaseItem() {
			return this.baseItem.get();
		}
		
		public Ingredient getMaterial() {
			return this.material.get();
		}
	}
}