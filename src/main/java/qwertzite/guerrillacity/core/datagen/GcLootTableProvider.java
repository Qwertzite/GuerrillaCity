package qwertzite.guerrillacity.core.datagen;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import qwertzite.guerrillacity.core.init.BlockRegister;

public class GcLootTableProvider extends LootTableProvider {
	
	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(
			Pair.of(BlockRegister::getBlockDropProvider, LootContextParamSets.BLOCK));
	
	public GcLootTableProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
		return tables;
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
//		for (ResourceLocation resourcelocation : Sets.difference(BuiltInLootTables.all(), map.keySet())) {
//			validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
//		}
		map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationtracker, resourceLocation, lootTable));
		// suppress vanilla validation.
	}
	
	@Override
	public String getName() {
		return "GuerrillaCity2 loot tables";
	}
}
