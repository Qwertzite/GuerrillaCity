package qwertzite.guerrillacity.worldgen;

import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.BiomeRegister;
import qwertzite.guerrillacity.core.init.ConfigRegister;
import qwertzite.guerrillacity.core.init.RegionRegister;
import qwertzite.guerrillacity.core.init.SurfaceRuleRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import terrablender.api.SurfaceRuleManager.RuleCategory;

/**
 * 
 * 
 * @author Qwertzite
 * @date 2022/09/29
 */
public class GcWorldGenModule extends GcModuleBase {
	
	public static final String NAME_CITY_BIOME = "city";
	public static final String NAME_SNOWY_CITY_BIOME = "snowy_city";
	
	public static final ResourceKey<Biome> KEY_CITY_BIOME = BiomeRegister.registryKey(NAME_CITY_BIOME);
	public static final ResourceKey<Biome> KEY_SNOWY_CITY_BIOME = BiomeRegister.registryKey(NAME_SNOWY_CITY_BIOME);
	
	public static RegistryObject<Biome> CITY_BIOME;
	public static RegistryObject<Biome> SNOWY_CITY_BIOME;
	
	public static final ResourceLocation REGION_CITY = new ResourceLocation(GuerrillaCityCore.MODID, "region_city");
	
	public static final TagKey<Biome> TAG_IS_CITY = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, "is_city"));
	
	public static final ConfigRegister<Integer> CITY_REGION_WEIGHT = ConfigRegister.intConfig("gen", "region_weight", 4, 1, Integer.MAX_VALUE, "The amount of city region.\nThe larger the more city region.");
	
	public GcWorldGenModule() {}
	
	public void init(IEventBus bus) {
		CITY_BIOME = BiomeRegister.register(KEY_CITY_BIOME, () -> CityBiomes.plains(false));
		SNOWY_CITY_BIOME = BiomeRegister.register(KEY_SNOWY_CITY_BIOME, () -> CityBiomes.plains(true));
		
		DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_REGISTER = DeferredRegister.create(Registry.STRUCTURE_PLACEMENT_TYPE_REGISTRY, GuerrillaCityCore.MODID);
		EveryChunkStructurePlacement.TYPE = STRUCTURE_PLACEMENT_REGISTER
				.register("every_chunk", () -> { return () -> EveryChunkStructurePlacement.CODEC; });
		
		DeferredRegister<Structure> STRUCTURE_REGISTER = DeferredRegister.create(Registry.STRUCTURE_REGISTRY, GuerrillaCityCore.MODID);
		CityStructure.INSTANCE = STRUCTURE_REGISTER
				.register("city_building", () -> new CityStructure());
		
		
		DeferredRegister<StructureType<?>> STRUCTURE_TYPE_REGISTER = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, GuerrillaCityCore.MODID);
		DeferredRegister<StructureSet> STRUCTURE_SET_REGISTRY = DeferredRegister.create(Registry.STRUCTURE_SET_REGISTRY, GuerrillaCityCore.MODID);
		DeferredRegister<StructurePieceType> STRUCTURE_PIECE_REGISTER = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, GuerrillaCityCore.MODID);


		CityStructure.TYPE = STRUCTURE_TYPE_REGISTER
				.register("city_building", () -> { return () -> CityStructure.CODEC; });
		
		
		STRUCTURE_SET_REGISTRY
				.register("city_building",
						() -> new StructureSet(
								List.of(StructureSet.entry(CityStructure.INSTANCE.getHolder().get())),
								new EveryChunkStructurePlacement()));
		
		CityStructure.CityStructurePiece.TYPE = STRUCTURE_PIECE_REGISTER
				.register("city_building", () -> CityStructure.CityStructurePiece::new);
		
		
		STRUCTURE_REGISTER.register(bus);
		STRUCTURE_TYPE_REGISTER.register(bus);
		STRUCTURE_SET_REGISTRY.register(bus);
		STRUCTURE_PIECE_REGISTER.register(bus);
		STRUCTURE_PLACEMENT_REGISTER.register(bus);
		
		GcGenCommand.registerCommand();
	}
	
	@Override
	public void postInit() {
		RegionRegister.register(new CityRegion(REGION_CITY, 5));
//		RegionRegister.register(new CityRegion(REGION_CITY, CITY_REGION_WEIGHT.getValue()));
		SurfaceRuleRegister.register(RuleCategory.OVERWORLD, CitySurfaceRules.makeRules());
	}
}
