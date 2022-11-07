package qwertzite.guerrillacity.core.datagen;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.RecipeRegister;

public class GcRecipeProvider extends RecipeProvider {
	
	public GcRecipeProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}
	
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
		for (var register : RecipeRegister.getShapedRecipeEntries()) {
			ItemLike output = register.getOutput().get();
			var builder = ShapedRecipeBuilder.shaped(output, register.getCount());
			builder.unlockedBy("has_" + ForgeRegistries.ITEMS.getKey(output.asItem()), has(output));
			if (register.hasGroup()) builder.group(GuerrillaCityCore.MODID + ":" +  register.getGroup());
			
			for (var ptn : register.getPattern()) { builder.pattern(ptn); }
			for (var e : register.getDefineItemLike().entrySet()) {
				var ingredient = e.getValue().get();
				builder.define(e.getKey(), ingredient);
				builder.unlockedBy("has_" + ForgeRegistries.ITEMS.getKey(ingredient.asItem()), has(ingredient));
			}
			for (var e : register.getDefineTagkey().entrySet()) {
				var ingredient = e.getValue().get();
				builder.define(e.getKey(), ingredient);
				builder.unlockedBy("has_" + ingredient.location(), has(ingredient));
			}
			
			for (var e : register.getUnlockTrigger().entrySet()) { builder.unlockedBy(e.getKey(), e.getValue()); }
			if (register.getRecipeName() != null) {
				builder.save(pFinishedRecipeConsumer, new ResourceLocation(GuerrillaCityCore.MODID, register.getRecipeName()));
			} else {
				builder.save(pFinishedRecipeConsumer);
			}
		}
		
		for (var register : RecipeRegister.getShapelessRecipeEntries()) {
			ItemLike output = register.getOutput().get();
			var builder = ShapelessRecipeBuilder.shapeless(output, register.getCount());
			builder.unlockedBy("has_" + ForgeRegistries.ITEMS.getKey(output.asItem()), has(output));
			if (register.hasGroup()) builder.group(register.getGroup());
			
			for (var e : register.getIngredientItemLike()) {
				int count = e.getIntA();
				var ingredient = e.getB().get();
				builder.requires(ingredient, count);
				builder.unlockedBy("has_" + ForgeRegistries.ITEMS.getKey(ingredient.asItem()), has(ingredient));
			}
			for (var e : register.getIngredientTagKey()) {
				var ingredient = e.get();
				builder.requires(ingredient);
				builder.unlockedBy("has_" + ingredient.location(), has(ingredient));
			}
			
			for (var e : register.getUnlockTrigger().entrySet()) { builder.unlockedBy(e.getKey(), e.getValue()); }
			if (register.getRecipeName() != null) {
				builder.save(pFinishedRecipeConsumer, new ResourceLocation(GuerrillaCityCore.MODID, register.getRecipeName()));
			} else {
				builder.save(pFinishedRecipeConsumer);
			}
		}
	}
	
	@Override
	public String getName() {
		return "gc recipes";
	}
}
