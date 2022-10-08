package qwertzite.guerrillacity.worldgen;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.RegistryObject;

public class EveryChunkStructurePlacement extends StructurePlacement {
	
	public static final Codec<StructurePlacement> CODEC = Codec.unit(() -> new EveryChunkStructurePlacement());
	public static RegistryObject<StructurePlacementType<?>> TYPE;
	
	public EveryChunkStructurePlacement() {
		super(/* offset */ Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, /* frequency */ 1.0F, /* salt */ 0, Optional.empty());
	}
	
	@Override
	public boolean isStructureChunk(ChunkGenerator chunkGenerator, RandomState rand, long p_227057_, int p_227058_, int p_227059_) {
		return true;
	}

	@Override
	protected boolean isPlacementChunk(ChunkGenerator chunkGenerator, RandomState rand, long p_227045_, int p_227046_, int p_227047_) {
		return true;
	}
	
	@Override
	public StructurePlacementType<?> type() {
		return TYPE.get();
	}

}
