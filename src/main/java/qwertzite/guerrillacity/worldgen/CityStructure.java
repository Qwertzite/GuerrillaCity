package qwertzite.guerrillacity.worldgen;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.PosUtil;
import qwertzite.guerrillacity.worldgen.city.CityStructureProvider;

public class CityStructure extends Structure {
	
	public static final Codec<Structure> CODEC = Codec.unit(() -> new CityStructure()); // simpleCodec(CityStructure::new);
	public static final TagKey<Biome> BIOME_TAG_KEY = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, "has_city_building"));
	
	public static RegistryObject<Structure> INSTANCE;
	public static RegistryObject<StructureType<?>> TYPE;
	
	@SuppressWarnings("deprecation")
	public CityStructure() {
		super(new Structure.StructureSettings(
				BuiltinRegistries.BIOME.getOrCreateTag(GcWorldGenModule.TAG_IS_CITY),
				Map.of(),
				Decoration.SURFACE_STRUCTURES,
				TerrainAdjustment.NONE));
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		BlockPos pos = context.chunkPos().getBlockAt(0, 64, 0);
		
		return Optional.of(new Structure.GenerationStub(pos, piecesBuilder -> {
			ChunkPos chunkPos = context.chunkPos();
			piecesBuilder.addPiece(new CityStructurePiece(chunkPos));
		}));
	}

	@Override
	public StructureType<?> type() {
		return TYPE.get();
	}
	
	public static class CityStructurePiece extends StructurePiece {
		public static RegistryObject<StructurePieceType> TYPE;
		
		public CityStructurePiece(StructurePieceSerializationContext serialisationContext, CompoundTag tag) {
			super(TYPE.get(), tag);
		}
		
		public CityStructurePiece(ChunkPos chunkPos) {
			super(TYPE.get(), 0, PosUtil.getChunkBoundingBox(chunkPos));
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag compoundTag) {}

		@Override
		public void postProcess(WorldGenLevel genLevel, StructureManager structureManager, ChunkGenerator generator,
				RandomSource rand, BoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
			
			@SuppressWarnings("deprecation")
			Map<BlockPos, BlockState> statemap = CityStructureProvider.getBlockStatesForChunk(
					chunkPos, genLevel.getSeed(), genLevel, BuiltinRegistries.BIOME.getOrCreateTag(GcWorldGenModule.TAG_IS_CITY)::contains); // 本当はstructure側から情報を取りたいところ...
			
			BlockState buildinbgState = ITEM_BY_DYE.get(DyeColor.byId(new Random(genLevel.getSeed() + chunkPos.hashCode()).nextInt(0, DyeColor.values().length))).defaultBlockState();
			
			for (var e : statemap.entrySet()) {
				genLevel.setBlock(e.getKey(), buildinbgState, Block.UPDATE_CLIENTS);
			}
			
			var state = (chunkPos.x % 16 == 0 || chunkPos.z % 16 == 0) ? Blocks.GOLD_BLOCK.defaultBlockState() : Blocks.DIAMOND_BLOCK.defaultBlockState();
			for (int y = 60; y <= 70; y++) {
				var pos = chunkPos.getBlockAt(0, y, 0);
				if (!genLevel.getBlockState(pos).isAir() && y != 70) continue;
				genLevel.setBlock(pos, state, Block.UPDATE_CLIENTS);
			}
			genLevel.setBlock(chunkPos.getBlockAt(0, 70, 0), state, Block.UPDATE_CLIENTS);
			
			ModLog.trace("Generated City piece at chunk pos " + chunkPos);
		}
		
		   private static final Map<DyeColor, Block> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (p_29841_) -> {
			      p_29841_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
			      p_29841_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
			      p_29841_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
			      p_29841_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
			      p_29841_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
			      p_29841_.put(DyeColor.LIME, Blocks.LIME_WOOL);
			      p_29841_.put(DyeColor.PINK, Blocks.PINK_WOOL);
			      p_29841_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
			      p_29841_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
			      p_29841_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
			      p_29841_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
			      p_29841_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
			      p_29841_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
			      p_29841_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
			      p_29841_.put(DyeColor.RED, Blocks.RED_WOOL);
			      p_29841_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
			   });
	}
	
}
