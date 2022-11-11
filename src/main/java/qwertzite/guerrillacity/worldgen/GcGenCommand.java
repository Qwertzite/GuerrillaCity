package qwertzite.guerrillacity.worldgen;

import java.util.Set;

import com.mojang.brigadier.Command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.command.CommandOption;
import qwertzite.guerrillacity.core.init.CommandRegister;
import qwertzite.guerrillacity.core.util.math.Rectangle;
import qwertzite.guerrillacity.worldgen.city.CityStructureProvider;
import qwertzite.guerrillacity.worldgen.city.CityWard;
import qwertzite.guerrillacity.worldgen.city.WardPos;

public class GcGenCommand {
	
	public static void registerCommand() {
		{
			CommandOption<Long> seed = CommandOption.longArg("seed")
					.setDefaultValue(ctx -> 0L)
					.setDescription("Seed to be used to generate a city.");
			CommandOption<BlockPos> pos = CommandOption.blockPos("pos")
					.setDefaultValue(ctx -> new BlockPos(WorldCoordinates.current().getPosition(ctx.getSource())))
					.setDescription("Position of city to be generated.");
			
			CommandRegister.$("gen", "ward", ctx -> {
				generate(pos.getValue(), ctx.getSource(), seed.getValue());
				return Command.SINGLE_SUCCESS;
			}).setPermissionLevel(2).addOption(seed).addOption(pos)
			.setUsageString("Generate city.");
		}
		{
			CommandRegister.$("gen", "clear_cache", ctx -> {
				CityStructureProvider.clearCache();
				ctx.getSource().sendSuccess(Component.literal("Cleared city gen cache."), true);
				return Command.SINGLE_SUCCESS;
			}).setPermissionLevel(2)
			.setUsageString("Clears city generation related caches.");
		}
		{
			CommandOption<BlockPos> pos = CommandOption.blockPos("pos")
								.setDefaultValue(ctx -> new BlockPos(WorldCoordinates.current().getPosition(ctx.getSource())))
								.setDescription("Position to check.");
			CommandRegister.$("gen", "check_biome", ctx -> {
				BlockPos p = pos.getValue();
				ChunkPos cp = new ChunkPos(p);
				boolean valid = CityStructureProvider.checkChunkApplicaleBiome(ctx.getSource().getLevel(), cp);
				ctx.getSource().sendSuccess(Component.literal("%s biome within chunk=%s".formatted(valid ? "Valid" : "Invalid", cp)), true);
				Holder<Biome> biome = CityStructureProvider.getBiomeAt(ctx.getSource().getLevel(), p.getX(), p.getY(), p.getZ());
				ctx.getSource().sendSuccess(Component.literal("Biome at pos=%s: %s".formatted(p, biome)), true);
				return Command.SINGLE_SUCCESS;
			}).setPermissionLevel(2).addOption(pos)
			.setUsageString("Checks biome in the chunk which includes the given position.");
		}
	}
	
	private static void generate(BlockPos pos, CommandSourceStack source, long seed) {
		ModLog.info("generating city ward with seed %s...", seed);
		try {
			WardPos wardPos = WardPos.contains(pos);
			CityWard ward = new CityWard(wardPos.getBaseBlockPos(pos.getY()), seed);
			Rectangle boundingBox = wardPos.getWardBoundingRectangle();
			ward.beginInitialisation(Set.of(boundingBox), Set.of());
			ModLog.info("initialised city ward");
			ServerLevel level = source.getLevel();
			
			var stateMap = ward.computeBlockStateForBoudingBox(level, boundingBox);
			ModLog.info("generated buildings");
			for (var e : stateMap.entrySet()) {
				level.setBlock(e.getKey(), e.getValue(), 3);
			}
			
			System.out.println("pos=%s, bb=%s".formatted(pos, boundingBox));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("generated city ward at %s".formatted(pos));
	}
}
