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
import net.minecraft.world.level.block.Blocks;
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
import qwertzite.guerrillacity.core.util.PosUtil;

public class CityStructure extends Structure {
	
	public static final Codec<Structure> CODEC = Codec.unit(() -> new CityStructure()); // simpleCodec(CityStructure::new);
	public static final TagKey<Biome> BIOME_TAG_KEY = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, "has_city_building"));
	
	public static RegistryObject<Structure> INSTANCE;
	public static RegistryObject<StructureType<?>> TYPE;
	
	public CityStructure() {
		super(new Structure.StructureSettings(
				BuiltinRegistries.BIOME.getOrCreateTag(GcWorldGenModule.TAG_IS_CITY),
//				HolderSet.direct(GcWorldGenModule.CITY_BIOME.getHolder().get()), // FIXME: use tags to enable other mod biomes to have city buildings.
				Map.of(),
				Decoration.SURFACE_STRUCTURES,
				TerrainAdjustment.NONE));
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		BlockPos pos = context.chunkPos().getBlockAt(0, 64, 0);
		
		return Optional.of(new Structure.GenerationStub(pos, piecesBuilder -> {
//			StructurePiece startingPiece = new ExampleStructurePiece(0, x+1, y+8, z+1); // FIXME
//			startingPiece.addChildren(startingPiece, structurePiecesBuilder, context.random());
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
			
			for (int y = 60; y <= 80; y++) {
				var pos = chunkPos.getBlockAt(0, y, 0);
				if (!genLevel.getBlockState(pos).isAir() && y != 256) continue;
				genLevel.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
			}
			genLevel.setBlock(chunkPos.getBlockAt(0, 70, 0), Blocks.DIAMOND_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
		}
		
	}
}
