package qwertzite.guerrillacity.worldgen;

import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
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
				Decoration.TOP_LAYER_MODIFICATION,
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
			
			Map<BlockPos, BlockState> statemap = CityStructureProvider.getBlockStatesForChunk(
					chunkPos, genLevel.getSeed(), genLevel);
			
			for (var e : statemap.entrySet()) {
				genLevel.setBlock(e.getKey(), e.getValue(), Block.UPDATE_CLIENTS);
			}
			
			ModLog.trace("Generated City piece at chunk pos " + chunkPos);
		}
	}
	
}
